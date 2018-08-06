package net.asaken1021.taskmonitor;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.ACTIVITY_SERVICE;

public class TaskMonitorClass extends Service {

    private Timer timer;
    List<String> packages = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int resId) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long start = System.currentTimeMillis() - 60000;
                long end = System.currentTimeMillis();
                UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
                List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, start, end);
                getUsageStats(stats);
                Log.d("Timer", "^q^");
            }
        }, 0, 5000);
        return android.app.Service.START_STICKY;
    }

    public void getUsageStats(List<UsageStats> list) {
        for (UsageStats stat : list) {
            packages.add(stat.getPackageName());
            Log.d("PackageName", stat.getPackageName());
        }
        Log.d("getUsageStats", "called");
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
