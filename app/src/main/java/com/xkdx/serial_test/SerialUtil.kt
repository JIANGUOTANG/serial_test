package com.xkdx.serial_test

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Arrays

import android_serialport_api.SerialPort

/**
 * Created by 11833 on 2018/1/9.
 */

open class SerialUtil {
    private var mSerialPort: SerialPort? = null
    protected var mInputStream: InputStream? = null
    private var mOutputStream: OutputStream? = null
    private val prot = "ttyS3"
    private val baudrate = 9600
    private var i = 0
    private var receiveThread: Thread? = null

    internal var buffer = ByteArray(0)
    internal var startTime: Long = 0//标记上一个开始执行时间

    init {
        OpenSerial()
        receiveThread()
    }

    private fun OpenSerial() {
        // 打开
        try {
            mSerialPort = SerialPort(File("/dev/" + prot), baudrate,
                    0)
            mInputStream = mSerialPort!!.inputStream
            mOutputStream = mSerialPort!!.outputStream
            //            receiveThread();

        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            Log.i("atuan", "打开失败")
            e.printStackTrace()
        }

    }

    private fun receiveThread() {
        // 接收
        receiveThread = object : Thread() {
            override fun run() {
                while (true) {
                    val size: Int
                    val b = read(mInputStream)
                    if (b != null) {
                        if (startTime == 0L) {
                            startTime = System.currentTimeMillis()   //获取开始时间
                            val newBuffer = ByteArray(buffer.size + b.size)
                            //复制数组
                            System.arraycopy(buffer, 0, newBuffer, 0, buffer.size)
                            System.arraycopy(b, 0, newBuffer, buffer.size, b.size)
                            buffer = newBuffer
                        } else {
                            val nowTime = System.currentTimeMillis()//获取当前时间
                            startTime = System.currentTimeMillis()   //重置上一个开始时间
                            val newBuffer = ByteArray(buffer.size + b.size)
                            //复制数组
                            System.arraycopy(buffer, 0, newBuffer, 0, buffer.size)
                            System.arraycopy(b, 0, newBuffer, buffer.size, b.size)
                            buffer = newBuffer
                        }
                    } else {
                        if (startTime != 0L && System.currentTimeMillis() - startTime > 50) {
                            Log.d("jianguotang", Arrays.toString(buffer))
                            startTime = 0
                            buffer = ByteArray(0)
                        }
                    }

                }
            }
        }
        receiveThread!!.start()
    }

    private fun read(input: InputStream?): ByteArray? {
        try {
            val buffer: ByteArray? = readData(input)
            return if (buffer!!.size > 0) buffer else null
        } catch (e: Exception) {
            android.util.Log.d("#ERROR#", "[COM]Read Faild: " + e.message, e)
            return null
        }

    }

    fun openCharge() {
        //打开充电桩
        writeData(Constant.openCharge)
    }
    fun closeCharge() {
        //关闭充电桩
        writeData(Constant.closeCharge)
    }

    fun closeLight() {
        //关闭灯
        writeData(Constant.closeLight)
    }

    fun openLight() {
        //开灯
        writeData(Constant.openLight)
    }

    private fun writeData(code: String) {
        // 发送
        Thread(Runnable {
            try {
                if (HexStrConvertUtil.isHex(code)) {
                    val bs = HexStrConvertUtil.hexStringToByte(code)
                    for (b in bs) {
                        mOutputStream!!.write(b.toInt())
                    }
                }
                Log.i("atuan", "发送成功:1" + i)
                Thread.sleep(5000)
                i += 1
            } catch (e: Exception) {
                Log.i("atuan", "发送失败")
                e.printStackTrace()
            }
        }).start()

    }

    fun onDestroy() {
        if (mSerialPort != null) {
            mSerialPort!!.close()
        }
        if (mInputStream != null) {
            try {
                mInputStream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (mOutputStream != null) {
            try {
                mOutputStream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        if (!receiveThread!!.isInterrupted) {
            receiveThread!!.stop()
        }
    }

    companion object {
        private val number = 0
        val DATABASE_NAME = "User_Flow"

        /**
         * 读取返回数据
         *
         * @return read_buff
         */
        fun readData(mis: InputStream?): ByteArray? {
            var len = 0
            var read_buff: ByteArray? = null
            if (mis != null) {
                try {
                    len = mis.available()
                    read_buff = ByteArray(len)
                    mis.read(read_buff)//读取返回数据
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            return read_buff
        }
    }
}
