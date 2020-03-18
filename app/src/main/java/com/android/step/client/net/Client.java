package com.android.step.client.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Client {

    private volatile Socket socket = null;
    private volatile OutputStream outputStream = null;
    private ThreadPoolExecutor poolExecutor = null;


    private Client() {

    }

    private static class SingletonHolder {
        private static Client client = new Client();
    }


    public static Client getClient() {
        return SingletonHolder.client;
    }


    public synchronized void send(String msg) {
        if (poolExecutor == null) {
            poolExecutor = new ThreadPoolExecutor(3, 5,
                    1, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(128));
        }
        if (socket == null) {
            poolExecutor.execute(new Thread(() -> {
                synchronized (getClient()) {
                    try {
                        socket = new Socket("192.168.199.137", 1210);
                        outputStream = socket.getOutputStream();
                    } catch (Exception e) {
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                                outputStream = null;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                        if (socket != null) {
                            try {
                                socket.close();
                                socket = null;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                        e.printStackTrace();
                    }
                }
            }));
        }
        if (outputStream != null) {
            poolExecutor.execute(new Thread(() -> {
                synchronized (getClient()) {
                    try {
                        outputStream.write(msg.getBytes("UTF-8"));
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }));
        }


    }


}
