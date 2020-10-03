package edu.temple.contacttracer;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import edu.temple.contacttracer.database.entity.ContactEvent;
import edu.temple.contacttracer.support.interfaces.GlobalStateManager;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TRACKING_TOPIC = "TRACKING";
    private GlobalStateManager global;

    public MyFirebaseMessagingService() {
    }

    public static void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("TRACKING").addOnCompleteListener(
                task -> Log.d("Firebase", "Successfully subscribed to the topic TRACKING")
        );
    }

    @Override
    public void onCreate() {
        super.onCreate();

        global = (GlobalStateManager) getApplicationContext();
    }

    @Override
    public void onMessageReceived(RemoteMessage msg) {
        if (msg.getFrom() != null && msg.getFrom().equals(TRACKING_TOPIC)) {
            String payload = msg.getData().get("payload");
            if (payload == null) return; // No payload value
            try {
                ContactEvent event = ContactEvent.fromPayload(new JSONObject(payload));
                if (!event.validate(this)) return; // Invalid event
                new Thread(() -> global.getDb().eventDao().insert(event));
                Log.d("Tracing", event.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            return;
        }

        super.onMessageReceived(msg);
    }
}
