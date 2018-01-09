package com.xkdx.serial_test;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android_serialport_api.SerialPort;

public class MyService extends Service {
    private SerialPort mSerialPort;
    protected InputStream mInputStream;
    protected OutputStream mOutputStream;
    private String prot = "ttyS4";
    private int baudrate = 9600;
    private static int number = 0;
    private int i = 0;
    public static final String DATABASE_NAME = "User_Flow";
    private Thread receiveThread;
    private StringBuilder sb;


    @Override
    public IBinder onBind(Intent intent) {
        return new MyBind();
    }

    public class MyBind extends Binder {
        public String getMyService() {
            return getNumString();
        }
    }

    public String getNumString() {
        return sb.toString();
    }

    @Override
    public void onCreate() {
        sb = new StringBuilder();
        sb.append("正在接受出口信息：");
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        OpenSerial();
        SendDate();
        return super.onStartCommand(intent, flags, startId);
    }

    private void OpenSerial() {
        // 打开
        try {
            mSerialPort = new SerialPort(new File("/dev/" + prot), baudrate,
                    0);
            mInputStream = mSerialPort.getInputStream();
            mOutputStream = mSerialPort.getOutputStream();
            receiveThread();

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("atuan", "打开失败");
            e.printStackTrace();
        }
    }

    byte[] buffer = new byte[0];
    long startTime = 0;//标记上一个开始执行时间

    private void receiveThread() {
        // 接收
        receiveThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    int size;
                    byte[] b = read(mInputStream);
                    if (b != null) {
                        if (startTime == 0) {
                            startTime = System.currentTimeMillis();   //获取开始时间
                            byte[] newBuffer = new byte[buffer.length + b.length];
                            //复制数组
                            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                            System.arraycopy(b, 0, newBuffer, buffer.length, b.length);
                            buffer = newBuffer;
                        } else {
                            long nowTime = System.currentTimeMillis();//获取当前时间
                            if (nowTime - startTime > 10) {
                                startTime = System.currentTimeMillis();   //重置上一个开始时间
                                Log.d("jianguotang",Arrays.toString(buffer));
                               Tem.getDate(buffer);
                                buffer = new byte[0];
                            }
                            startTime = System.currentTimeMillis();   //重置上一个开始时间
                            byte[] newBuffer = new byte[buffer.length + b.length];
                            //复制数组
                            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                            System.arraycopy(b, 0, newBuffer, buffer.length, b.length);
                            buffer = newBuffer;
                        }
                    }

                }
            }
        };
        receiveThread.start();
    }

    private byte[] read(InputStream input) {
        try {
            byte[] buffer;
            buffer = readData(input);
            return buffer.length > 0 ? buffer : null;
        } catch (Exception e) {
            android.util.Log.d("#ERROR#", "[COM]Read Faild: " + e.getMessage(), e);
            return null;
        }
    }
    /**
     * 读取返回数据
     *
     * @return read_buff
     */
    public static byte[] readData(InputStream mis) {
        int len = 0;
        byte[] read_buff = null;
        if (mis != null) {
            try {
                len = mis.available();
                read_buff = new byte[len];
                mis.read(read_buff);//读取返回数据
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return read_buff;
    }

    private void SendDate() {
        // 发送
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        writeDate(mOutputStream);
                        Log.i("atuan", "发送成功:1" + i);
                        Thread.sleep(5000);
                        i += 1;
                    } catch (Exception e) {
                        Log.i("atuan", "发送失败");
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void writeDate(OutputStream output) throws IOException {
        String code = "19 03 00 00 00 0b 07 d5";
        if (HexStrConvertUtil.isHex(code)) {
            byte[] bs = HexStrConvertUtil.hexStringToByte(code);
            for (byte b : bs) {
                output.write(b);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mSerialPort != null) {
            mSerialPort.close();
        }
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!receiveThread.isInterrupted()) {
            receiveThread.stop();
        }
    }
}
