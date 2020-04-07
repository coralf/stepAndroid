package com.android.step.client.net;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.android.step.utils.Config;

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
    private volatile String serverAddress = null;


    private Client() {
    }

    private static class SingletonHolder {
        private static Client client = new Client();
    }


    public static Client getClient() {
        return SingletonHolder.client;
    }


    public synchronized void send(String msg, Context context) {
        if (poolExecutor == null) {
            poolExecutor = new ThreadPoolExecutor(3, 5,
                    1, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(128));
        }
        if (socket == null) {
            poolExecutor.execute(new Thread(() -> {
                synchronized (getClient()) {
                    try {
                        if (serverAddress == null) {
                            serverAddress = getUrl(context);
                        }
                        socket = new Socket(serverAddress, Config.port);
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

    private String getUrl(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        int ipAddress = dhcpInfo.serverAddress;
        return intToIp(ipAddress);
    }

    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }


}
