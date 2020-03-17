package com.android.step.client.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Client {
    private Socket socket = null;
    private OutputStream outputStream = null;
    private ThreadPoolExecutor poolExecutor = null;


    public Client() {
        poolExecutor = new ThreadPoolExecutor(3, 5,
                1, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(128));

        poolExecutor.execute(new Thread(() -> {
            try {
                socket = new Socket("192.168.199.137", 1210);
                outputStream = socket.getOutputStream();
//                int i = 0;
//                while (true) {
//                    String msg = "hello 我是客户端:" + (i++);
//                    outputStream.write(msg.getBytes("UTF-8"));
//                    Thread.sleep(5);
//                }
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
                e.printStackTrace();
            }

        }));


    }


    public void send(String msg) {
        if (poolExecutor != null) {
            poolExecutor.execute(new Thread(() -> {
                if (outputStream != null) {
                    try {
                        outputStream.write(msg.getBytes("UTF-8"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
    }


}
