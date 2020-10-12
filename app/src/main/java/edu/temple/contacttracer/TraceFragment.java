package edu.temple.contacttracer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.temple.contacttracer.database.entity.ContactEvent;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TraceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TraceFragment extends Fragment {
    private static final String EVENT_ARG = "event";

    private ContactEvent event;
    private MapView mapView;

    public TraceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param event The contact event
     * @return A new instance of fragment TraceFragment.
     */
    public static TraceFragment newInstance(ContactEvent event) {
        TraceFragment fragment = new TraceFragment();
        Bundle args = new Bundle();
        args.putSerializable(EVENT_ARG, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            event = (ContactEvent) getArguments().getSerializable(EVENT_ARG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trace, container, false);

        view.findViewById(R.id.okButton).setOnClickListener(view1 -> getParentFragmentManager().popBackStack());

        Date startDate = new Date(event.sedentaryBegin);
        Date endDate = new Date(event.sedentaryEnd);

        DateFormat dateFormat = new SimpleDateFormat("MMMM d, Y", Locale.US);
        DateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);

        TextView date = view.findViewById(R.id.date);
        date.setText(String.format("%s - %s", dateFormat.format(startDate), dateFormat.format(endDate)));

        TextView time = view.findViewById(R.id.time);
        time.setText(String.format("%s - %s", timeFormat.format(startDate), timeFormat.format(endDate)));

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        mapView.getMapAsync(map -> {
            LatLng latLng = new LatLng(event.latitude, event.longitude);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Point of contact"));
            map.animateCamera(cameraUpdate);
        });


        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}