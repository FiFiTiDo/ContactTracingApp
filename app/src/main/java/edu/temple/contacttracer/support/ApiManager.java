package edu.temple.contacttracer.support;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import edu.temple.contacttracer.database.entity.SedentaryLocation;

public class ApiManager {
    private static final String SERVER_URL = "https://kamorris.com/lab/ct_tracking.php";
    private final RequestQueue queue;

    public ApiManager(Context ctx) {
        this.queue = Volley.newRequestQueue(ctx);
    }

    public void sendLocation(SedentaryLocation loc) {
        Log.d("API", "Sending location to the remote server");
        Log.d("API Body", loc.toString());
        StringRequest req = new StringRequest(Request.Method.POST, SERVER_URL, response -> {
            // Success
            if (response.contains("OK"))
                Log.d("API", "Successfully sent location to remote server.");
            else
                Log.d("API", "Failed to send location to remote server.");
            Log.d("API Response", response);
        }, error -> {
            // Failure
            Log.d("API", "Failed to send location to remote server.");
            Log.e("API ERROR", error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<String, String>() {{
                    put("uuid", loc.uuid.toString());
                    put("latitude", String.valueOf(loc.latitude));
                    put("longitude", String.valueOf(loc.longitude));
                    put("sedentary_begin", String.valueOf(loc.sedentaryBegin));
                    put("sedentary_end", String.valueOf(loc.sedentaryEnd));
                }};
            }

            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("Content-Type", "application/x-www-form-urlencoded");
                }};
            }
        };
        queue.add(req);
    }
}
