package edu.temple.contacttracer.listeners;

public interface PermissionManager {
    public static final int REQUEST_ID = 1;

    public boolean hasPermission();
    public void acquirePermission();
}
