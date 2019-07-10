package at.alladin.nettest.nntool.android.app.view;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.workflow.main.MeasurementServerSelectionFragment;
import at.alladin.nettest.nntool.android.app.workflow.tc.TermsAndConditionsFragment;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerResponse;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class MeasurementServerSelectionView extends RelativeLayout {

    private final static String TAG = ProviderAndSignalView.class.getSimpleName();

    TextView serverName;

    private SpeedMeasurementPeerResponse measurementPeerResponse;

    public MeasurementServerSelectionView(Context context) {
        super(context);
        init();
    }

    public MeasurementServerSelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MeasurementServerSelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setSelectedServerByIdentifier(final String identifier) {
        for (SpeedMeasurementPeerResponse.SpeedMeasurementPeer p : measurementPeerResponse.getSpeedMeasurementPeers()) {
            if (p.getIdentifier().equals(identifier)) {
                serverName.setText(p.getName());
                return;
            }
        }
    }

    public void updateServerList(final SpeedMeasurementPeerResponse response) {
        final List<SpeedMeasurementPeerResponse.SpeedMeasurementPeer> peerList = response.getSpeedMeasurementPeers();
        if (peerList == null || peerList.size() == 0) {
            return;
        }
        measurementPeerResponse = response;
        SpeedMeasurementPeerResponse.SpeedMeasurementPeer defaultPeer = null;
        SpeedMeasurementPeerResponse.SpeedMeasurementPeer selectedPeer = null;
        final String selectedIdentifier = ((MainActivity) getContext()).getSelectedMeasurementPeerIdentifier();
        for (SpeedMeasurementPeerResponse.SpeedMeasurementPeer p : peerList) {
            if (defaultPeer == null && p.isDefaultPeer()) {
                defaultPeer = p;
            }
            if (p.getIdentifier().equals(selectedIdentifier)) {
                selectedPeer = p;
                break;
            }
        }
        if (defaultPeer == null) {
            defaultPeer = peerList.get(0);
        }
        if (selectedIdentifier != null && selectedPeer == null) {
            //we previously selected a measurement peer that is no longer available
            //clear the input measurement peer
            ((MainActivity) getContext()).setSelectedMeasurementPeerIdentifier(null);
        }
        serverName.setText(selectedPeer != null ? selectedPeer.getName() : defaultPeer.getName());
    }

    private void init() {
        inflate(getContext(), R.layout.measurement_server_selection_view, this);
        final View container = findViewById(R.id.measurement_server_selection_container);
        container.setOnClickListener(v -> {
            if (measurementPeerResponse == null) {
                return;
            }
            final FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
            MeasurementServerSelectionFragment f = MeasurementServerSelectionFragment.newInstance(measurementPeerResponse.getSpeedMeasurementPeers());
            f.setOnConfirmListener(() -> {
                ((MainActivity) getContext()).setSelectedMeasurementPeerIdentifier(f.getSelectedServerIdentifier());
                setSelectedServerByIdentifier(f.getSelectedServerIdentifier());
                f.dismiss();
            });
            f.show(ft, "MSSF");
        });

        serverName = findViewById(R.id.text_measurement_server_selection_name);
        /*
        serverName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MeasurementServerSelectionFragment f = MeasurementServerSelectionFragment.newInstance();
                final FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                f.show(ft, "MSSF");
            }
        });
        */
    }

}
