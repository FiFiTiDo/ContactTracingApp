package edu.temple.contacttracer.support;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import edu.temple.contacttracer.support.interfaces.GlobalStateManager;

public class LocationUtils {
    @Nullable
    public static Location getCurrentLocation(Context ctx) {
        LocationManager manager = ctx.getSystemService(LocationManager.class);
        GlobalStateManager globals = (GlobalStateManager) ctx.getApplicationContext();
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return globals.getLastLocation();
    }

    public static boolean checkTracingDistance(Context ctx, Double lat, Double lon) {
        Location loc = new Location("");
        loc.setLatitude(lat);
        loc.setLongitude(lon);

        Location currentLoc = getCurrentLocation(ctx);
        if (currentLoc == null) return false;

        float distance = loc.distanceTo(currentLoc); // Distance in meters
        int maxDistance = PreferencesManager.getTrackingDistance(ctx);

        return distance <= maxDistance;
    }
}
