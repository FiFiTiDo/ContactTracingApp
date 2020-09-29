package edu.temple.contacttracer;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import edu.temple.contacttracer.database.entity.ContactEvent;
import edu.temple.contacttracer.support.LocationUtils;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TRACKING_TOPIC = "TRACKING";

    public MyFirebaseMessagingService() {
    }

    public static void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("TRACKING").addOnCompleteListener(
                task -> Log.d("Firebase", "Successfully subscribed to the topic TRACKING")
        );
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage msg) {
        if (msg.getFrom() != null && msg.getFrom().equals(TRACKING_TOPIC)) {
            String payload = msg.getData().get("payload");
            if (payload == null) return;
            try {
                JSONObject data = new JSONObject(payload);
                UUID uuid = UUID.fromString(data.getString("uuid"));
                Double lat = data.getDouble("latitude");
                Double lon = data.getDouble("longitude");
                Long sedentaryBegin = data.getLong("sedentary_begin");
                Long sedentaryEnd = data.getLong("sedentary_end");

                if (App.db.uniqueIdDao().hasById(uuid)) return; // Self location
                if (!LocationUtils.checkTracingDistance(this, lat, lon)) return; // Too far

                ContactEvent event = new ContactEvent(uuid, lat, lon, sedentaryBegin, sedentaryEnd);
                new Thread(() -> App.db.eventDao().insert(event));
                Log.d("Tracing", "New contact event at " + lat + " " + lon);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            return;
        }

        super.onMessageReceived(msg);
    }
}
