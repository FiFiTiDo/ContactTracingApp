package edu.temple.contacttracer.support;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.temple.contacttracer.database.entity.UniqueId;

public class ApiManager {
    private RequestQueue queue;
    private static final String SERVER_URL = "https://kamorris.com/lab/ct_tracking.php";

    public ApiManager(Context ctx) {
        this.queue = Volley.newRequestQueue(ctx);
    }

    public void sendLocation(UniqueId currentId, Location loc, long endTime) {
        StringRequest req = new StringRequest(Request.Method.POST, SERVER_URL, response -> {
            // Success
            Log.d("API", "Successfully sent location to remote server.");
        }, error -> {
            // Failure
            Log.d("API", "Failed to send location to remote server.");
            Log.e("API", error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<String, String>() {{
                    put("uuid", currentId.uuid.toString());
                    put("latitude", String.valueOf(loc.getLatitude()));
                    put("longitude", String.valueOf(loc.getLongitude()));
                    put("sedentary_begin", String.valueOf(loc.getTime()));
                    put("sedentary_end", String.valueOf(endTime));
                }};
            }

            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("Content-Type","application/x-www-form-urlencoded");
                }};
            }
        };
    }
}
