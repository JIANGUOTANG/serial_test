package com.xkdx.serial_test;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android_serialport_api.SerialPort;
public class MainActivity extends Activity {

    private TextView text;
    private String prot = "ttyS4";
    private int baudrate = 9600;
    private static int i = 0;
    SerialUtil serialUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serialUtil = new SerialUtil();
        findViewById(R.id.btn_open).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                serialUtil.openLight();
            }
        });
        findViewById(R.id.btn_receive).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                serialUtil.closeLight();
            }
        });
        findViewById(R.id.openCharge).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                serialUtil.openCharge();
            }
        });
        findViewById(R.id.closeCharge).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                serialUtil.closeCharge();
            }
        });

//        startS
// ervice(new Intent(MainActivity.this,MyService.class));
    }

}
