package com.android.step.server.server;

import android.util.Log;

import com.android.step.utils.Config;
import com.github.mikephil.charting.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {


    private volatile ServerSocket s = null;

    public interface ServerCallBack {

        void message(String msg);

    }

    private static final String TAG = "Server";
    private ServerCallBack serverCallBack;

    private static class ServerSingletonHolder {
        private static Server server = new Server();
    }

    public Server() {

        new Thread(() -> {
            ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(3, 5,
                    1, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(128));
            try {
                if (s == null) {
                    s = new ServerSocket(Config.port);
                }
                while (true) {
                    try {
                        if (!s.isClosed()) {
                            Socket socket = s.accept();
                            poolExecutor.execute(() -> {
                                InputStream inputStream = null;
                                try {
                                    inputStream = socket.getInputStream();
                                    byte[] bytes = new byte[1024];
                                    int len;
                                    while ((len = inputStream.read(bytes)) != -1) {
                                        serverCallBack.message(new String(bytes, 0, len, "UTF-8"));
                                    }
                                    inputStream.close();
                                    socket.close();
                                    s.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onCreate: 等待客户端连接");
        }).start();
    }

    public void setOnServerMessageCallBack(ServerCallBack serverCallBack) {
        this.serverCallBack = serverCallBack;
    }


    public static Server getServer() {
        return ServerSingletonHolder.server;
    }


}
