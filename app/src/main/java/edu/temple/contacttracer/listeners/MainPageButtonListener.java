package edu.temple.contacttracer.listeners;

import java.io.Serializable;

public interface MainPageButtonListener extends Serializable {
    public void onStartTracking();
    public void onStopTracking();
    public void onOpenSettings();
}
