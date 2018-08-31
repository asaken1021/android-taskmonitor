package net.asaken1021.taskmonitor;

import android.Manifest;
import android.app.usage.UsageEvents;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private Button stopButton;
    private Button reloadButton;

    private static ListView UsedAppList;

    public static List<String> UsedApps = new ArrayList<>();
    public static List<String> UsedAppNames = new ArrayList<>();
    public ArrayAdapter<String> arrayAdapter;

    public static HashMap<String, String> reversed_appKey = new HashMap<String, String>();

    boolean isIntentAllowed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        reloadButton = (Button) findViewById(R.id.reloadButton);
        UsedAppList = (ListView) findViewById(R.id.UsedAppList);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, UsedApps);

        UsedAppList.setAdapter(arrayAdapter);

        UsedAppList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long resId) {
                String packagename = arrayAdapter.getItem(position);
                Log.d("Adapter->onItemClick", packagename);
                PackageManager packageManager = getPackageManager();
                try {
                    packagename = reversed_appKey.get(packagename);
                    Intent intent = packageManager.getLaunchIntentForPackage(packagename);
                    startActivity(intent);
                } catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "このアプリは起動できません。", Toast.LENGTH_SHORT);
                }
            }
        });

        checkPermission();

        arrayAdapter.add("使用履歴の読み取り中...");

        startService(new Intent(getApplicationContext(), TaskMonitorClass.class));
    }

    public void service_start(View v) {
        startService(new Intent(getApplicationContext(), TaskMonitorClass.class));
    }

    public void service_stop(View v) {
        stopService(new Intent(getApplicationContext(), TaskMonitorClass.class));
    }

    public void reload(View v) {
        boolean isFirstAdd = true;
        for (String UsedAppName : UsedAppNames) {
            if (UsedAppName == "") {
                return;
            }
            if (isFirstAdd) {
                arrayAdapter.clear();
                isFirstAdd = false;
            }
            arrayAdapter.add(UsedAppName);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getApplicationContext(), TaskMonitorClass.class));
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.PACKAGE_USAGE_STATS) != PackageManager.PERMISSION_GRANTED) {
            /*
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.PACKAGE_USAGE_STATS)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.PACKAGE_USAGE_STATS}, 0);
            }*/
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.PACKAGE_USAGE_STATS}, 0);
        }
        Log.d("checkPermission", "called");
    }
}
