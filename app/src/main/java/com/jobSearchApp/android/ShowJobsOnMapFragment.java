package com.jobSearchApp.android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jobSearchApp.android.ServiceAPI.SeekerAPI;
import com.jobSearchApp.android.ServiceModels.JobInfo;
import com.jobSearchApp.android.ServiceModels.ServiceGenerator;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ShowJobsOnMapFragment extends android.support.v4.app.Fragment
        implements OnMapReadyCallback{

    private GoogleMap mMap;
    private com.google.android.gms.maps.SupportMapFragment mMapFragment;
    private SeekerAPI seekerAPI;
    private Call<List<JobInfo>> call;
    private JobDetailPopUpWindow popUp;

    public ShowJobsOnMapFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.show_jobs_on_map, container, false);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
        mMapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addMarkersToMap();
    }

    private void addMarkersToMap() {

        seekerAPI = ServiceGenerator.createServiceWithAuth(SeekerAPI.class);
        call = seekerAPI.getJobs();
        call.enqueue(new Callback<List<JobInfo>>() {

            @Override
            public void onResponse(Response<List<JobInfo>> response, Retrofit retrofit) {

                if (response.isSuccess()) {
                    for (final JobInfo job : response.body()) {
                        if (job.Location != null) {
                            // Add a marker in coherent job position
                            LatLng jobPosition = new LatLng(job.Location.GeoLatitude, job.Location.GeoLongitude);
                            Marker jobMarker = mMap.addMarker(new MarkerOptions().position(jobPosition).title(job.Name));

                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                     showPopup(ShowJobsOnMapFragment.this, job.Id);
                                }
                            });
                        }
                    }
                    // move the camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(31.76, 35.21)));
                    CameraPosition.Builder cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(31.76, 35.21))      // Sets the center of the map
                            .zoom(8)                   // Sets the zoom
                            .bearing(15)                // -90 = west, 90 = east
                            .tilt(45);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition.build()));

                } else
                    Toast.makeText(getContext(), response.errorBody().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // The method that displays the popup.
    private void showPopup(final Fragment fragment, final int jobId) {
        Point p = new Point();
        int[] location = new int[2];

        this.getView().getLocationOnScreen(location);
        p.set(location[0], location[1]);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int scrHeight = displaymetrics.heightPixels;
        int width = this.getView().getWidth() - 60;
        int height = scrHeight - p.y - 60;

        // Creating the PopupWindow
        popUp = new JobDetailPopUpWindow(fragment.getActivity(), JobDetailPopUpWindow.JobDetailPopupType.SEEK_JOB);
        popUp.setWidth(width);
        popUp.setHeight(height);
        popUp.setFocusable(true);

        // Some offset to align the popup .
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        // Displaying the popup at the specified location, + offsets.
        popUp.showPopup(Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y, jobId);
    }

}
