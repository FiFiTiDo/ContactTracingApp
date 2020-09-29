package edu.temple.contacttracer.support.interfaces;

import java.io.Serializable;

public interface MainPageButtonListener extends Serializable {
    void onStartTracking();

    void onStopTracking();

    void onOpenSettings();
}
