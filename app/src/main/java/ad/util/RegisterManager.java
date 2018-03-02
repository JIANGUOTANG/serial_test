package ad.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import ad.Constants;
import ad.bean.Register;
import ad.db.SharePeferenceUtil;
import ad.jpush.util.Logger;
import cn.jpush.android.api.JPushInterface;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by 11833 on 2018/1/16.
 */

public class RegisterManager {
    //广告机注册通过IMEI
    public static void getCodeNumber(final Context context) {
        OkHttpClient okHttpClient = new OkHttpClient();
        if (SharePeferenceUtil.getCodeNumberId(context) == 0) {
            //当前的广告机没有注册，进行注册
            TelephonyManager mTm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            String imei = getMac();
            if (imei!=null) {
                FormBody formBody = new FormBody.Builder().add("codeNumber", imei).build();
                Request request = new Request.Builder()
                        .url(Constants.BASH_PATH + "/serv/advertisementMachine/register")
                        .post(formBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String sp = response.body().string();
                        if (response.code() == 200) {
//                        EventBus.getDefault().post(new EventCodeNumber(machine.getCode_number()));
                            Log.d("jianguotang", sp);
                            Register register = new Gson().fromJson(sp, Register.class);
                            if (register.getId() > 0) {
                                SharePeferenceUtil.setCodeNumberId(context, register.getId());
                                JPushInterface.setDebugMode(true);
                                //注册广告机到推送
                                Log.d("RegisterManager", "注册");
                                JPushInterface.setAlias(context, register.getId() + "", null);// 设置开启日志,发布时请关闭日志
                                JPushInterface.init(context);            // 初始化 JPush
                                Logger.d("APPLOGGER", String.valueOf(register.getId()));
                            }
                        }
                    }
                });
            }
        }
    }
    public static String getMac() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }
}
