package com.android.step;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RightActivity extends AppCompatActivity {


    private static final String TAG = "RightActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_right);

        new Thread(() -> {
            int port = 1210;
            try {
                ServerSocket s = new ServerSocket(port);

                Log.d(TAG, "onCreate: 等待客户端连接");

                Socket socket = s.accept();
                InputStream inputStream = socket.getInputStream();
                byte[] bytes = new byte[1024];
                int len;
                while ((len = inputStream.read(bytes)) != -1) {
                    Log.d(TAG, "onCreate: " + new String(bytes, 0, len, "UTF-8"));
                }
                inputStream.close();
                socket.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


    }
}
