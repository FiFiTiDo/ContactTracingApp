package edu.temple.contacttracer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.UUID;

import edu.temple.contacttracer.database.entity.ContactEvent;
import edu.temple.contacttracer.support.interfaces.GlobalStateManager;
import edu.temple.contacttracer.support.interfaces.MainPageButtonListener;
import edu.temple.contacttracer.support.interfaces.PermissionManager;
import edu.temple.contacttracer.support.interfaces.SettingsListener;

public class MainActivity extends AppCompatActivity implements PermissionManager, MainPageButtonListener, SettingsListener {
    private GlobalStateManager global;
    private TrackingService.TrackingServiceBinder tsb = null;
    // Tracing service management
    private final ServiceConnection tracingServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            tsb = (TrackingService.TrackingServiceBinder) iBinder;
            tsb.setPermissionManager(MainActivity.this);
            tsb.startTracking();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            tsb = null;
        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ContactEvent event = (ContactEvent) intent.getSerializableExtra(MyFirebaseMessagingService.NEW_TRACE_EVENT);
            showTraceFragment(event);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        global = (GlobalStateManager) getApplicationContext();

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainFrame);
        if (fragment == null) fragment = MainPageFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFrame, fragment)
                .commit();

        Intent intent = getIntent();
        if (intent.hasExtra(MyFirebaseMessagingService.NEW_TRACE_EVENT)) {
            ContactEvent event = (ContactEvent) intent.getSerializableExtra(MyFirebaseMessagingService.NEW_TRACE_EVENT);
            showTraceFragment(event);
        }

        if (global.isDebugMode()) {
            ContactEvent testEvent = new ContactEvent(UUID.randomUUID(), 39.9814667, -75.1551641, 1602450416930L, 1602458416930L);
            showTraceFragment(testEvent);
        }
    }

    private void showTraceFragment(ContactEvent event) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFrame, TraceFragment.newInstance(event))
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(MyFirebaseMessagingService.NEW_TRACE_FILTER);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
        global.setInForeground(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        global.setInForeground(false);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    // Permission management
    @Override
    public boolean hasPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void acquirePermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionManager.REQUEST_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionManager.REQUEST_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.perm_rationale_title)
                            .setMessage(R.string.perm_rational_text)
                            .setPositiveButton("Ok", (dialogInterface, i) -> requestPermissions(
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    PermissionManager.REQUEST_ID
                            ))
                            .create().show();
                } else {
                    Toast.makeText(this, getString(R.string.unable_to_obtain_permission), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            tsb.startTracking();
        }
    }

    @Override
    public void onStartTracking() {
        if (tsb != null) {
            Toast.makeText(this, getString(R.string.service_already_running), Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, TrackingService.class);
        startService(intent);
        bindService(intent, tracingServiceConn, BIND_AUTO_CREATE);
    }

    @Override
    public void onStopTracking() {
        if (tsb == null) {
            Toast.makeText(this, getString(R.string.service_not_running), Toast.LENGTH_LONG).show();
            return;
        }

        tsb.stopTracking();
        unbindService(tracingServiceConn);
        tsb = null;
    }

    @Override
    public void onOpenSettings() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFrame, new SettingsFragment())
                .addToBackStack("Settings page")
                .commit();
    }

    @Override
    public void onGenerateId() {
        new Thread(() -> {
            global.getDb().generateId(true);
            runOnUiThread(() -> Toast.makeText(
                    MainActivity.this,
                    getString(R.string.uuid_generated_toast),
                    Toast.LENGTH_LONG
            ).show());
        }).start();
    }

    @Override
    public void onDistanceUpdate(int distance) {
        if (tsb != null) tsb.restartTracking(distance);
    }
}