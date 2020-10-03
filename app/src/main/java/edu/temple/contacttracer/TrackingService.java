package edu.temple.contacttracer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import edu.temple.contacttracer.database.entity.SedentaryLocation;
import edu.temple.contacttracer.support.ApiManager;
import edu.temple.contacttracer.support.PreferenceUtils;
import edu.temple.contacttracer.support.interfaces.GlobalStateManager;
import edu.temple.contacttracer.support.interfaces.PermissionManager;

public class TrackingService extends Service {
    public static final String CHANNEL_ID = "TrackingServiceChannel";
    public PermissionManager permissionManager;
    private LocationManager loc;
    private ApiManager api;
    private GlobalStateManager global;
    private final LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location nextLocation) {
            if (global.hasLastLocation()) {
                // Calculate minutes user has been sedentary
                Long last = global.getLastLocation().getTime();
                Long next = nextLocation.getTime();
                long diffMin = (next - last) / 1000 / 60;

                if (diffMin > 0) // User has been sedentary for at least a minute for logging purposes
                    Log.d("Tracing", "User has been sedentary for " + diffMin + " minutes.");

                // Check actual sedentary time
                if (diffMin >= PreferenceUtils.getSedentaryLength(TrackingService.this)) {
                    Log.d("Tracing", "User has been sedentary for the configured period of time.");
                    SedentaryLocation loc = SedentaryLocation.makeFromGlobals(global, nextLocation.getTime());
                    global.getDb().locationDao().insert(loc);
                    api.sendLocation(loc);
                }
            }
            global.setLastLocation(nextLocation);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    @Override
    public void onCreate() {
        global = (GlobalStateManager) getApplicationContext();
        loc = getSystemService(LocationManager.class);
        api = new ApiManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        startForeground(1, createNotification());

        return START_NOT_STICKY;
    }

    /**
     * Create the notification channel used by the foreground service notification
     */
    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Tracking Service",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    /**
     * Create the notification for the foreground service
     *
     * @return The notification
     */
    public Notification createNotification() {
        Intent notifIntent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, notifIntent, 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle("Contact Tracer")
                .setContentText("Tracking your location for contact tracing purposes.")
                .setContentIntent(pi)
                .build();
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

    @SuppressLint("MissingPermission") // Permission is definitely checked
    private void startTracking(int distance) {
        if (permissionManager == null)
            throw new RuntimeException("Tracking service requires a permission manager.");
        if (!permissionManager.hasPermission()) {
            permissionManager.acquirePermission();
            return;
        }

        loc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, distance, listener);
    }

    public void startTracking() {
        startTracking(PreferenceUtils.getTrackingDistance(this));
    }

    public void stopTracking() {
        loc.removeUpdates(listener);
    }

    public class TrackingServiceBinder extends Binder {
        public void startTracking() {
            TrackingService.this.startTracking();
        }

        private void startTracking(int distance) {
            TrackingService.this.startTracking(distance);
        }

        public void restartTracking(int distance) {
            TrackingService.this.stopTracking();
            startTracking(distance);
        }

        public void stopTracking() {
            TrackingService.this.stopTracking();
            stopSelf();
        }

        public void setPermissionManager(PermissionManager listener) {
            TrackingService.this.permissionManager = listener;
        }
    }
}
