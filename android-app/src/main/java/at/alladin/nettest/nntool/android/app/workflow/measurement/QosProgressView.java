package at.alladin.nettest.nntool.android.app.workflow.measurement;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;

/**
 * @author Lukasz Budryk (lb@lume.at)
 */
public class QosProgressView extends LinearLayout {

    private final static String TAG = "QosProgressView";

    private class ViewHolder {
        View mainView;
        ProgressBar progressBar;
        TextView titleText;
    }

    public QosProgressView(Context context) {
        super(context);
        init();
    }

    public QosProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QosProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Map<QosMeasurementType, ViewHolder> viewHolderMap = new HashMap<>();

    private Handler handler = new Handler();

    private Set<QosMeasurementType> qosFinishedSet = new HashSet<>();

    public void init() {
        inflate(getContext(), R.layout.qos_measurement_progress_view, this);

        if (isInEditMode()) {
            for (int i = 0; i < Math.min(6, QosMeasurementType.values().length); i++) {
                setQosProgress(QosMeasurementType.values()[i], i * 15);
            }
        }
    }

    private ViewHolder addQosTestType(final QosMeasurementType qosType) {
        final LinearLayout container = findViewById(R.id.qos_measurement_container);
        final LayoutInflater li = LayoutInflater.from(getContext());
        final View v = li.inflate(R.layout.qos_measurement_item, container, false);
        container.addView(v);
        v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));

        final ViewHolder holder = new ViewHolder();
        holder.mainView = v;
        holder.progressBar = v.findViewById(R.id.progress_qos_measurement_item);
        holder.titleText = v.findViewById(R.id.text_qos_measurement_item);

        holder.progressBar.setProgress(0);

        holder.titleText.setText(qosType.toString());
        viewHolderMap.put(qosType, holder);

        return holder;
    }

    public synchronized void setQosProgress(final QosMeasurementType qosType, final float progress) {
        ViewHolder holder = viewHolderMap.get(qosType);
        if (holder == null) {
            holder = addQosTestType(qosType);
        }
        if (holder.mainView.getVisibility() != View.GONE) {
            holder.progressBar.setProgress((int) (progress * 100f));
        }
    }

    public synchronized void finishQosType(final QosMeasurementType qosType) {
        final ViewHolder holder = viewHolderMap.get(qosType);
        if (holder == null || qosFinishedSet.contains(qosType)) {
            return;
        }

        qosFinishedSet.add(qosType);
        holder.progressBar.setProgress((int) (100f));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //Log.d(TAG, "ON ANIM END " + qosType);
                        holder.mainView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                });


                holder.mainView.startAnimation(anim);
            }
        }, 500);
    }

    private List<QosMeasurementType> getDummyQosTestNameList() {
        return Arrays.asList(QosMeasurementType.values());
    }
}
