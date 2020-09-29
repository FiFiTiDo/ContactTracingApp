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
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import edu.temple.contacttracer.support.PreferencesManager;
import edu.temple.contacttracer.support.listeners.PermissionManager;

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
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle("Contact Tracer")
                .setContentText("Tracking your location for contact tracing purposes.")
                .setContentIntent(pi)
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new TrackingServiceBinder();
    }

    @Override
    public void onDestroy() {
        stopTracking();
    }

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location nextLocation) {
            if (App.lastLocation != null) {
                Long last = App.lastLocation.getTime();
                Long next = nextLocation.getTime();
                long diffMin = (next - last) / 1000 / 60;

                if (diffMin > 0)
                    Log.d("Tracing", "User has been sedentary for " + diffMin + " minutes.");

                if (diffMin >= PreferencesManager.getSedentaryLength(TrackingService.this)) {
                    Log.d("Tracing", "User has been sedentary for the configured period of time.");
                }
            }
            App.lastLocation = nextLocation;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    @SuppressLint("MissingPermission") // Permission is definitely checked
    private void startTracking(int distance) {
        if (permissionManager == null) throw new RuntimeException("Tracking service requires a permission manager.");
        if (!permissionManager.hasPermission()) {
            permissionManager.acquirePermission();
            return;
        }

        loc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, distance, listener);
    }

    public void startTracking() {
        startTracking(PreferencesManager.getTrackingDistance(this));
    }

    public void stopTracking() {
        loc.removeUpdates(listener);
    }
}
