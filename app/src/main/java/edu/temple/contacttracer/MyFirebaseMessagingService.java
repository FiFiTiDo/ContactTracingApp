package edu.temple.contacttracer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import edu.temple.contacttracer.database.dao.ContactEventDao;
import edu.temple.contacttracer.database.dao.SedentaryLocationDao;
import edu.temple.contacttracer.database.entity.ContactEvent;
import edu.temple.contacttracer.support.interfaces.GlobalStateManager;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TRACKING_TOPIC = "/topics/TRACKING";
    public static final String TRACING_TOPIC = "/topics/TRACING";

    public static final String NEW_TRACE_FILTER = "new-trace";
    public static final String NEW_TRACE_EVENT = "event";
    public static final String NEW_TRACE_TIMESTAMP = "date";

    public static final String TRACE_NOTIFICATION_CHANNEL = "TraceChannel";

    private NotificationManagerCompat notif;
    private GlobalStateManager global;

    public MyFirebaseMessagingService() {
    }

    public static void subscribeToTopics() {
        subscribeToTopic("TRACKING");
        subscribeToTopic("TRACING");
    }

    private static void subscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnCompleteListener(
                task -> Log.d("Firebase", "Successfully subscribed to the topic " + topic)
        ).addOnFailureListener(
                task -> Log.d("Firebase", "Failed to subscribe to the topic " + topic)
        );
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notif = NotificationManagerCompat.from(this);
        global = (GlobalStateManager) getApplicationContext();
        createNotificationChannel();
    }

    @Override
    public void onMessageReceived(RemoteMessage msg) {
        Log.d("Test", "Received message from: " + msg.getFrom() + " with data: " + msg.getData());
        if (msg.getFrom() != null) {
            String payload = msg.getData().get("payload");
            Log.d("Test", "Received payload: " + payload);
            if (payload == null) return; // No payload value

            if (msg.getFrom().equals(TRACKING_TOPIC)) {
                onTracking(payload);
                return;
            } else if (msg.getFrom().equals(TRACING_TOPIC)) {
                onTracing(payload);
                return;
            }
        }

        super.onMessageReceived(msg);
    }

    private void onTracking(String payload) {
        try {
            ContactEvent event = ContactEvent.fromPayload(new JSONObject(payload));
            if (!event.validate(this)) {
                Log.d("Test", "Invalid contact event");
                return; // Invalid event
            }
            new Thread(() -> global.getDb().eventDao().insert(event));
            Log.d("Tracing", event.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onTracing(String payload) {
        try {
            // JSON Manipulation
            JSONObject json = new JSONObject(payload);
            JSONArray jsonUuid = json.getJSONArray("uuids");
            List<UUID> uuids = new ArrayList<>();
            Date timestamp = new Date(json.getLong("date"));

            // Get own uuids
            SedentaryLocationDao locationDao = global.getDb().locationDao();
            List<UUID> myUuids = locationDao.getRecent().stream().map(location -> location.uuid).collect(Collectors.toList());

            // Get contact events
            ContactEventDao eventDao = global.getDb().eventDao();
            List<ContactEvent> recentEvents = eventDao.getRecent();

            for (int i = 0; i < jsonUuid.length(); i++) {
                UUID uuid = UUID.fromString(jsonUuid.getString(i));

                for (UUID otherUuid : uuids)
                    if (uuid == otherUuid) return; // Is own UUID

                for (ContactEvent event : recentEvents)
                    if (uuid == event.uuid) onExposure(timestamp, event); // Was in contact with user
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                TRACE_NOTIFICATION_CHANNEL,
                "Exposure Alerts",
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    private void onExposure(Date timestamp, ContactEvent event) {
        if (global.isInForeground()) {
            Intent intent = new Intent(NEW_TRACE_FILTER);
            intent.putExtra(NEW_TRACE_TIMESTAMP, timestamp);
            intent.putExtra(NEW_TRACE_EVENT, event);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(NEW_TRACE_TIMESTAMP, timestamp);
            intent.putExtra(NEW_TRACE_EVENT, event);
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

            Notification notification = new NotificationCompat.Builder(this, TRACE_NOTIFICATION_CHANNEL)
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setContentTitle(getString(R.string.trace_alert_title))
                    .setContentText(getString(R.string.trace_alert_content))
                    .setContentIntent(pi)
                    .build();
            notif.notify(0, notification);
        }
    }
}
