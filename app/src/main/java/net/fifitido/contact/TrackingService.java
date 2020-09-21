package net.fifitido.contact;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import net.fifitido.contact.listeners.PermissionManager;

public class TrackingService extends Service {
    public static final String CHANNEL_ID = "TrackingServiceChannel";
    private LocationManager loc;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        loc = getSystemService(LocationManager.class);
        createNotificationChannel();
        Intent notifIntent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, notifIntent, 0);

        Notification notif = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Contact Tracer")
                .setContentText("Tracking your location for contact tracing purposes.")
                .build();

        startForeground(1, notif);

        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Tracking Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public PermissionManager permissionManager;

    public class TrackingServiceBinder extends Binder {
        public void startTracking() {
            TrackingService.this.startTracking();
        }

        public void restartTracking() {
            TrackingService.this.stopTracking();
            startTracking();
        }

        public void stopTracking() {
            TrackingService.this.stopTracking();
            stopSelf();
        }

        public void setPermissionManager(PermissionManager listener) {
            TrackingService.this.permissionManager = listener;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new TrackingServiceBinder();
    }

    @Override
    public void onDestroy() {
        stopTracking();
    }

    private Location lastLocation;
    private LocationListener listener = nextLocation -> {
        Long last = lastLocation.getTime();
        Long next = nextLocation.getTime();
        Long diffMin = (next - last) * 1000 * 60;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String defVal = "medium";
        String timeSel = pref.getString("sedentary", defVal);
        if (timeSel == null) timeSel = defVal;

        int min = 15;
        switch (timeSel) {
            case "short":
                min = 5;
                break;
            case "long":
                min = 45;
                break;
        }

        if (diffMin >= min) {
            Log.d("Tracing", "User has been sedentary for the configured period of time.");
        }

        lastLocation = nextLocation;
    };

    @SuppressLint("MissingPermission") // Permission is definitely checked
    public void startTracking() {
        if (permissionManager == null) throw new RuntimeException("Tracking service requires a permission manager.");
        if (!permissionManager.hasPermission()) {
            permissionManager.acquirePermission();
            return;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int defVal = 2;
        String strVal = pref.getString("tracking_distance", String.valueOf(defVal));
        int distance = strVal == null ? defVal : Integer.parseInt(strVal);

        loc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, distance, listener);
    }

    public void stopTracking() {
        loc.removeUpdates(listener);
    }
}
