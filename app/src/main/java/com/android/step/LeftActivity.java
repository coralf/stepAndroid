package com.android.step;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class LeftActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left);
        Button btnConfirm = findViewById(R.id.btn_confirm);
        EditText etIp = findViewById(R.id.et_ip);
        btnConfirm.setOnClickListener(v -> {
            String etIpTx = etIp.getText().toString();
            new Thread(() -> {
                Socket socket = null;
                OutputStream outputStream = null;
                try {
                    socket = new Socket(etIpTx, 1210);
                    outputStream = socket.getOutputStream();
                    int i = 0;
                    while (true) {
                        String msg = "hello 我是客户端:" + (i++);
                        outputStream.write(msg.getBytes("UTF-8"));
                        Thread.sleep(5);
                    }
                } catch (Exception e) {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }).start();

        });
    }
}
