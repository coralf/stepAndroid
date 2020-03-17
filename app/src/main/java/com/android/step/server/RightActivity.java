package com.android.step.server;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;

import com.android.step.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


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
                    Log.d(TAG, "客户端发来的消息: " + new String(bytes, 0, len, "UTF-8"));
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
