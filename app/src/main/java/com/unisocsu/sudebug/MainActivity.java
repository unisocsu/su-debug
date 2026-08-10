package com.unisocsu.sudebug;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnActivate = (Button) findViewById(R.id.btn_activate);
        TextView tvStatus = (TextView) findViewById(R.id.tv_status);

        btnActivate.setOnClickListener(v -> {
            try {
                Process su = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(su.getOutputStream());

                os.writeBytes("setprop service.adb.tcp.port 5555\n");
                os.writeBytes("stop adbd\n");
                os.writeBytes("start adbd\n");
                os.writeBytes("exit\n");
                os.flush();

                String ip = getIpAddress();
                tvStatus.setText("ADB Activated!\nConnect via:\nadb connect " + ip + ":5555");
            } catch (Exception e) {
                tvStatus.setText("Error: " + e.getMessage());
            }
        });
    }

    private String getIpAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {}
        return "Unknown IP";
    }
}
