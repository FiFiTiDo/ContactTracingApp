package edu.temple.contacttracer.support.interfaces;

public interface PermissionManager {
    int REQUEST_ID = 1;

    boolean hasPermission();

    void acquirePermission();
}
