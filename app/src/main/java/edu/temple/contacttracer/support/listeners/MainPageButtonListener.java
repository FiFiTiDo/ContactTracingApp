package edu.temple.contacttracer.support.listeners;

import java.io.Serializable;

public interface MainPageButtonListener extends Serializable {
    public void onStartTracking();
    public void onStopTracking();
    public void onOpenSettings();
}
