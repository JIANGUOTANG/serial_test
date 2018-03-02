package jian.com.ad.jpush.receiver

import ad.jpush.receiver.ReceiverBean
import ad.jpush.util.Logger
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import cn.jpush.android.api.JPushInterface
import com.google.gson.Gson
import com.xkdx.serial_test.SerialUtil
import jian.com.ad.main.lamp.LampControl
import jian.com.ximon.model.light.lamp.ChargeControl
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject

/**
 * 自定义接收器
 *
 *
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val bundle = intent.extras
            Logger.d(TAG, "[MyReceiver] onReceive - " + intent.action + ", extras: " + printBundle(bundle))

            if (JPushInterface.ACTION_REGISTRATION_ID == intent.action) {
                val regId = bundle!!.getString(JPushInterface.EXTRA_REGISTRATION_ID)
                Logger.d(TAG, "[MyReceiver] 接收Registration Id : " + regId!!)
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED == intent.action) {
                Logger.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle!!.getString(JPushInterface.EXTRA_EXTRA)!!)
                //				processCustomMessage(context, bundle);

                val json = bundle.getString(JPushInterface.EXTRA_EXTRA)
                val gson = Gson()
                //解析返回的参数从而判断是什么推送

                val receiverBean: ReceiverBean = gson.fromJson(json, ReceiverBean::class.java)
                Logger.d(TAG, "data " + receiverBean.id)
                if (receiverBean.advertisementID > 0) {
                    //删除数据库中的广告
                    val id:Long = receiverBean.advertisementID.toLong()
                    EventBus.getDefault().post(id)
                    return
                }
                if (receiverBean.needUpdate == 1) {
                    //接收到跟新广告的通知
                    EventBus.getDefault().post("update")
                } else {
                    when {
                        receiverBean.operate == 1 -> //打开灯
                            LampControl().open()
                        receiverBean.operate == 0 -> //关掉灯
                            LampControl().close()
                        receiverBean.operate == 2 -> ChargeControl().close() //关闭充电桩
                        receiverBean.operate == 3 -> ChargeControl().open() //打开充电桩
                    }
                }

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED == intent.action) {
                Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知")
                val notifactionId = bundle!!.getInt(JPushInterface.EXTRA_NOTIFICATION_ID)
                Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId)
                val json = bundle.getString(JPushInterface.EXTRA_ALERT)
                if (json == "1") {
                    var control = SerialUtil()
                    control.openLight()
                } else {
                    var control = SerialUtil()
                    control.closeLight()
                }

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED == intent.action) {
                Logger.d(TAG, "[MyReceiver] 用户点击打开了通知")
            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK == intent.action) {
                Logger.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle!!.getString(JPushInterface.EXTRA_EXTRA)!!)
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE == intent.action) {
                val connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false)
                Logger.w(TAG, "[MyReceiver]" + intent.action + " connected state change to " + connected)
            } else {
                Logger.d(TAG, "[MyReceiver] Unhandled intent - " + intent.action!!)
            }
        } catch (e: Exception) {

        }

    }

    companion object {
        private val TAG = "JIGUANG-Example"
        private val DELETEFILE = 2//删除文件
        private val LAMP = 3//灯光控制
        private val CHARGE = 4//充电

        // 打印所有的 intent extra 数据
        private fun printBundle(bundle: Bundle?): String {
            val sb = StringBuilder()
            for (key in bundle!!.keySet()) {
                if (key == JPushInterface.EXTRA_NOTIFICATION_ID) {
                    sb.append("\nkey:" + key + ", value:" + bundle.getInt(key))
                } else if (key == JPushInterface.EXTRA_CONNECTION_CHANGE) {
                    sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key))
                } else if (key == JPushInterface.EXTRA_EXTRA) {
                    if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                        Logger.i(TAG, "This message has no Extra data")
                        continue
                    }

                    try {
                        val json = JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA))
                        val it = json.keys()

                        while (it.hasNext()) {
                            val myKey = it.next()
                            sb.append("\nkey:" + key + ", value: [" +
                                    myKey + " - " + json.optString(myKey) + "]")
                        }
                    } catch (e: JSONException) {
                        Logger.e(TAG, "Get message extra JSON error!")
                    }

                } else {
                    sb.append("\nkey:" + key + ", value:" + bundle.getString(key))
                }
            }
            return sb.toString()
        }
    }

    //	//send msg to MainActivity
    //	private void processCustomMessage(Context context, Bundle bundle) {
    //		if (MainActivity.isForeground) {
    //			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
    //			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
    //			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
    //			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
    //			if (!ExampleUtil.isEmpty(extras)) {
    //				try {
    //					JSONObject extraJson = new JSONObject(extras);
    //					if (extraJson.length() > 0) {
    //						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
    //					}
    //				} catch (JSONException e) {
    //
    //				}
    //
    //			}
    //			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
    //		}
    //	}
}
