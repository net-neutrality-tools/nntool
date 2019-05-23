package at.alladin.nettest.nntool.android.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;

import at.alladin.nettest.nntool.android.app.R;

import static java.lang.Math.log10;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class CanvasArcDoubleGaugeWithLabels extends AbstractCanvasWithLabels {

    private final static int SMALL_POINTER_SIZE = 9;

    private final static int REALLY_SMALL_POINTER_SIZE = SMALL_POINTER_SIZE / 3;

    private Paint arcPaint;
    private Paint arcTextPaint;
    private Paint textPaint;
    private Paint unitPaint;
    private Paint bitmapPaint;

    private RingArc speedRing;
    private RingArc progressRing;

    public CanvasArcDoubleGaugeWithLabels(Context context) {
        super(context);
    }

    public CanvasArcDoubleGaugeWithLabels(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasArcDoubleGaugeWithLabels(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int saveCount = canvas.save();

        arcPaint.setStrokeWidth(coordFW(SMALL_POINTER_SIZE, defaultWidth));

        canvas.save();

        canvas.rotate(90 + ((360f-ARC_ANGLE)/2f), centerX, centerY);
        arcPaint.setColor(getResources().getColor(R.color.gauge_speed_bg));
        canvas.drawArc(speedRing.getBounds(), 0, ARC_ANGLE, false, arcPaint);
        arcPaint.setColor(getResources().getColor(R.color.gauge_progress_bg));
        canvas.drawArc(progressRing.getBounds(), 0, ARC_ANGLE, false, arcPaint);

        //define arcpaint for the gauges
        arcPaint.setStrokeWidth(coordFW(SMALL_POINTER_SIZE, defaultWidth));
        arcPaint.setColor(getResources().getColor(R.color.black));

        //progress arc

        final int progressLabelSize = progressLabels.size();
        final float radProgress = ((float) (2f*Math.PI*(progressRing.getBounds().width()/2f))) * (ARC_ANGLE/360f);
        final float radProgressPart = radProgress / progressLabelSize;

        for (int i = 0; i < progressLabelSize; i++) {
            canvas.drawTextOnPath(progressLabels.get(i), progressRing.getPath(), i * radProgressPart + (radProgressPart / 2) - (arcTextPaint.measureText(progressLabels.get(i)) / 2), coordFH(1, defaultHeight), arcTextPaint);
            if (i < progressLabelSize - 1) {
                canvas.drawArc(progressRing.getBounds(), (i + 1) * (ARC_ANGLE / progressLabelSize), 1f, false, arcPaint);
            }
        }

        // speed arc

        final int speedLabelSize = speedLabels.size();
        final float radSpeed = ((float) (2f*Math.PI*(speedRing.getBounds().width()/2f))) * (ARC_ANGLE/360f);
        final float radSpeedPart = radSpeed / (speedLabelSize - 1);

        //get speed log value
        if (speedMeasurementState != null) {
            final double maxLog = Math.log10(1000d);
            final double gaugeParts = speedLabelSize - 1;

            if (speedMeasurementState.getDownloadMeasurement() != null) {
                //TODO: state checking
                if (speedMeasurementState.getDownloadMeasurement().getThroughputAvgBps() > 10000) {
                    final double downLog = ((gaugeParts - maxLog) + Math.log10(speedMeasurementState.getDownloadMeasurement().getThroughputAvgBps() / 1e6)) / gaugeParts;
                    arcPaint.setColor(getResources().getColor(R.color.gauge_speed_fg));
                    canvas.drawArc(speedRing.getBounds(), 0, (float)(ARC_ANGLE*downLog), false, arcPaint);
                }
            }
        }

        for (int i = 0; i < speedLabelSize; i++) {
            final float hOffset;
            if (i == 0){
                hOffset = i * radSpeedPart + coordFW(2, defaultWidth);
            } else if (i == (speedLabelSize - 1)) {
                hOffset = i * radSpeedPart - arcTextPaint.measureText(speedLabels.get(i)) - coordFW(2, defaultWidth);
            } else {
                hOffset = i * radSpeedPart - (arcTextPaint.measureText(speedLabels.get(i)) / 2);
            }

            canvas.drawTextOnPath(speedLabels.get(i), speedRing.getPath(), hOffset, 0, arcTextPaint);
        }

        arcPaint.setStrokeWidth(coordFW(REALLY_SMALL_POINTER_SIZE, defaultWidth));
        final float speedRingOffset = coordFW(SMALL_POINTER_SIZE - REALLY_SMALL_POINTER_SIZE, defaultWidth) / 2;
        for (int i = 1; i < speedLabelSize - 1; i++) {
            canvas.drawArc(speedRing.getBounds(speedRingOffset), i * (ARC_ANGLE / (speedLabelSize - 1)), 1f, false, arcPaint);
        }


        canvas.restore();


        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void initialize() {
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(coordFW(SMALL_POINTER_SIZE, defaultWidth));
        arcPaint.setAntiAlias(true);

        arcTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcTextPaint.setColor(getResources().getColor(R.color.black));
        arcTextPaint.setTextSize(coordFH(4, defaultHeight));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(getResources().getColor(R.color.theme_text_color));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(coordFH(20, defaultHeight));

        unitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unitPaint.setColor(getResources().getColor(R.color.theme_text_color));
        unitPaint.setTextAlign(Paint.Align.CENTER);
        unitPaint.setTextSize(coordFH(8, defaultHeight));

        bitmapPaint = new Paint();
        bitmapPaint.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.theme_text_color), PorterDuff.Mode.SRC_ATOP));
        bitmapPaint.setFilterBitmap(true);

        speedRing = new RingArc(centerX, centerY, arcPaint.getStrokeWidth(), .625f);
        progressRing = new RingArc(centerX, centerY, arcPaint.getStrokeWidth(), .9f);
    }
}
