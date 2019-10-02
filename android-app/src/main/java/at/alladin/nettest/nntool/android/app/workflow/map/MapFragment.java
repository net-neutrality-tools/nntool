package at.alladin.nettest.nntool.android.app.workflow.map;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.RequestMapMarkerTask;
import at.alladin.nntool.shared.map.MapCoordinate;
import at.alladin.nntool.shared.map.MapMarkerResponse;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class MapFragment extends SupportMapFragment {

    private final static String TAG = MapFragment.class.getSimpleName();

    private final static int MAP_MARKER_SIZE_PARAMETER = 20;

    private GoogleMap map;

    private TileOverlayOptions heatmapOptions;

    private TileOverlayOptions pointmapOptions;

    private int startingZoomLevel;

    private LatLng startingLocation;

    private RequestMapMarkerTask currentRequestMapMarkerTask;

    public static MapFragment newInstance() {
        final MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (heatmapOptions == null || pointmapOptions == null) {
            heatmapOptions = new TileOverlayOptions().tileProvider(new MapOverlayTileProvider(MapOverlayTileProvider.MapOverlayType.HEATMAP, getContext())).zIndex(1000);
            pointmapOptions = new TileOverlayOptions().tileProvider(new MapOverlayTileProvider(MapOverlayTileProvider.MapOverlayType.POINTMAP, getContext())).zIndex(2000);
        }

        final View v = inflater.inflate(R.layout.map_fragment, container, false);

        final RelativeLayout mapContainer = v.findViewById(R.id.mapContainer);
        mapContainer.addView(super.onCreateView(inflater, container, savedInstanceState));

        try {
            startingZoomLevel = getResources().getInteger(R.integer.default_map_start_zoom_level);
            startingLocation = new LatLng(Double.parseDouble(getResources().getString(R.string.default_map_start_center_lat)), Double.parseDouble(getResources().getString(R.string.default_map_start_center_lon)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return v;
    }

    public void onStart() {
        super.onStart();

       getMapAsync(googleMap -> {
               if (googleMap == null) {
                        return;
                    }
            map = googleMap;
            map.setTrafficEnabled(false);
            map.setIndoorEnabled(false);

            map.setInfoWindowAdapter(new MapInfoWindowAdapter(getActivity().getLayoutInflater()));

            map.addTileOverlay(heatmapOptions);
            map.addTileOverlay(pointmapOptions);

            if (startingLocation != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(startingLocation, (float) startingZoomLevel));
            }

            map.setOnMapClickListener(latLng -> {
                if (currentRequestMapMarkerTask != null) {
                    currentRequestMapMarkerTask.cancel(true);
                }

                final MapCoordinate coordinate = new MapCoordinate();
                coordinate.setLat(latLng.latitude);
                coordinate.setLon(latLng.longitude);
                coordinate.setZoom((int) map.getCameraPosition().zoom);
                coordinate.setSize(MAP_MARKER_SIZE_PARAMETER);

                currentRequestMapMarkerTask = new RequestMapMarkerTask(coordinate, getContext(), result -> {
                    if (result != null && result.getMapMarkers() != null && result.getMapMarkers().size() > 0) {
                        Log.d(TAG, result.toString());
                        final MapMarkerResponse.MapMarker marker = result.getMapMarkers().get(0);
                        final Marker googleMarker = map.addMarker(
                                new MarkerOptions()
                                .position(new LatLng(marker.getLatitude(), marker.getLongitude()))
                                .title(getString(R.string.map_marker_popup_title))
                                .snippet(parseMapMarkerIntoString(marker))
                        );
                        googleMarker.showInfoWindow();

                        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(marker.getLatitude(), marker.getLongitude())));
                    }
                });

                currentRequestMapMarkerTask.execute();
            });
        });
    }


    //@Override
    public Integer getTitleStringId() {
        return R.string.title_map;
    }

    //@Override
    public Integer getHelpSectionStringId() {
        return R.string.help_link_map_section;
    }

    private String parseMapMarkerIntoString (final MapMarkerResponse.MapMarker marker) {
        final StringBuilder builder = new StringBuilder();
        for (MapMarkerResponse.MeasurementItem item : marker.getNetworkResult()) {
            addMeasurementItemToStringBuilder(item, builder);
        }

        for (MapMarkerResponse.MeasurementItem item : marker.getMeasurementResults()) {
            addMeasurementItemToStringBuilder(item, builder);
        }

        return builder.toString();
    }

    private void addMeasurementItemToStringBuilder (final MapMarkerResponse.MeasurementItem item, final StringBuilder builder) {
        if (item.getTitle() != null) {
            builder.append(item.getTitle()).append(": ");
        }
        if (item.getValue() != null) {
            builder.append(item.getValue());
        }
        builder.append("\n");
    }
}
