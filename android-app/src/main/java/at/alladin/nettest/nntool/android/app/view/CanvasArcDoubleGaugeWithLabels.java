package at.alladin.nettest.nntool.android.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;

import at.alladin.nettest.nntool.android.app.R;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class CanvasArcDoubleGaugeWithLabels extends AbstractCanvasWithLabels {

    private final static int SMALL_POINTER_SIZE = 9;

    private final static int REALLY_SMALL_POINTER_SIZE = SMALL_POINTER_SIZE / 3;

    private Paint arcPaint;
    private Paint textPaint;
    private Paint unitPaint;
    private Paint bitmapPaint;

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
    protected void onDraw(Canvas canvas) {
        final int saveCount = canvas.save();

        final RingArc speedRing = new RingArc(centerX, centerY, arcPaint.getStrokeWidth(), .625f);
        final RingArc progressRing = new RingArc(centerX, centerY, arcPaint.getStrokeWidth(), .9f);

        canvas.save();

        canvas.rotate(90 + ((360f-ARC_ANGLE)/2f), centerX, centerY);
        arcPaint.setColor(getResources().getColor(R.color.gauge_speed_bg));
        canvas.drawArc(speedRing.getBounds(), 0, ARC_ANGLE, false, arcPaint);
        arcPaint.setColor(getResources().getColor(R.color.gauge_progress_bg));
        canvas.drawArc(progressRing.getBounds(), 0, ARC_ANGLE, false, arcPaint);

        canvas.restore();

        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void initialize() {
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(coordFW(SMALL_POINTER_SIZE, defaultWidth));
        arcPaint.setAntiAlias(true);

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
    }
}
