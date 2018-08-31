package net.asaken1021.taskmonitor;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TaskMonitorClass extends Service {

    private Timer timer;
    List<String> packages = new ArrayList<>();
    List<String> appNames = new ArrayList<>();

    PackageManager pm;
    List<ApplicationInfo> appList = new ArrayList<>();
    public static HashMap<String, String> appKey = new HashMap<String, String>();
    public static HashMap<String, String> reversed_appKey = new HashMap<String, String>();

    UsageStatsManager usageStatsManager;
    List<UsageStats> stats = new ArrayList<>();

    ActivityManager activityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        pm = getPackageManager();
        appList = pm.getInstalledApplications(0);
        String[] installedApps = new String[appList.size() + 1];
        int x = 0;
        for (ApplicationInfo info : appList) {
            installedApps[x++] = info.packageName;
            appKey.put(info.packageName, info.loadLabel(pm).toString());
            reversed_appKey.put(info.loadLabel(pm).toString(), info.packageName);
        }
        activityManager = (ActivityManager) getSystemService(getApplicationContext().ACTIVITY_SERVICE);
    }

    public int onStartCommand(Intent intent, int flags, int resId) {
        quickStart();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long start = System.currentTimeMillis() - 60000;
                long end = System.currentTimeMillis();
                usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
                stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
                getUsageStats(stats);
                getAppNames(packages);
                try {
                    MainActivity.UsedApps = packages;
                    MainActivity.UsedAppNames = appNames;
                    MainActivity.reversed_appKey = reversed_appKey;
                } catch (NullPointerException e){
                    //何もしない
                }
                packages = new ArrayList<>();
                appNames = new ArrayList<>();
            }
        }, 0, 5000);
        return android.app.Service.START_STICKY;
    }

    private void getUsageStats(List<UsageStats> list) {
        Log.d("getUsageStats", "called");
        for (UsageStats stat : list) {
            if (stat.getLastTimeUsed() > System.currentTimeMillis() - 120000) { //直近2分以内に使用が開始された場合
                packages.add(stat.getPackageName());
                Log.d("PackageName", stat.getPackageName());
            }
        }
    }

    private void getAppNames(List<String> packageNames) {
        Log.d("getAppNames", "called");
        for (String packageName : packageNames) {
            String appName = appKey.get(packageName);
            appNames.add(appName);
            Log.d("AppName", appName);
        }
    }

    private void quickStart() {
        long start = System.currentTimeMillis() - 60000;
        long end = System.currentTimeMillis();
        usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
        getUsageStats(stats);
        getAppNames(packages);
//                checkUsedAppStatus();
        try {
            MainActivity.UsedApps = packages;
            MainActivity.UsedAppNames = appNames;
            MainActivity.reversed_appKey = reversed_appKey;
        } catch (NullPointerException e){
            //何もしない
        }
        packages = new ArrayList<>();
        appNames = new ArrayList<>();
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}