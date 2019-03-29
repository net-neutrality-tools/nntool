package at.alladin.nettest.nntool.android.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public abstract class AbstractCanvasWithLabels extends View {

    protected static int BIG_POINTER_SIZE = 9;

    protected static float ARC_ANGLE = 240f;

    protected View statusView;

    protected int requestedCanvasHeight = 0;
    protected int requestedCanvasWidth  = 0;

    protected int defaultHeight = 100;
    protected int defaultWidth = 100;

    protected int centerX = 50;
    protected int centerY = 50;
    protected float translateStatusY = 0;

    protected float singleArcAngle = 0f;

    protected String[] currentProgressLabels;
    protected String[] allProgressLabels;

    protected double baseTestPhases = 4;

    public AbstractCanvasWithLabels(final Context context) {
        super(context);
    }

    public AbstractCanvasWithLabels(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractCanvasWithLabels(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        allProgressLabels = getAllProgressLabels();
    }

    //TODO: we may not need to enforce an onDraw's existance, but here we are!
    @Override
    protected abstract void onDraw(Canvas canvas);

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // extract their requested height and width.
        int requestedWidth  = MeasureSpec.getSize(widthMeasureSpec);
        int requestedHeight = MeasureSpec.getSize(heightMeasureSpec);

        // make sure we're a square by re-writing the requested dimensions, favoring the smaller of the two as the new h & w of square.
        if (requestedHeight != requestedWidth) {

            // overwrite the larger of the dimensions.
            if (requestedWidth > requestedHeight)
                requestedWidth = requestedHeight;
            else
                requestedHeight = requestedWidth;
        }

        // set the local member variables to the newly discovered desired dimensions.
        requestedCanvasHeight = requestedHeight;
        requestedCanvasWidth  = requestedWidth;

        // calculate the origin.
        centerX = requestedCanvasHeight / 2;
        centerY = requestedCanvasWidth  / 2;

        // spit back the requested dimensions as our accepted dimensions. We'll do our best.
        setMeasuredDimension(requestedWidth, requestedHeight);

        // this seems like a good place to initialize things.
        initialize();
    }

    protected void setStatusText(final String statusText) {
        if (statusView != null && statusView instanceof TextView) {
            if ("QoS".equals(statusText)) {
                ((TextView) statusView).setTypeface(Typeface.SANS_SERIF);
            }
            ((TextView) statusView).setText(statusText);
        }
    }

    protected abstract void initialize();

    protected String[] getAllProgressLabels() {
        final String[] ret = new String[] {"init", "ping", "down", "up"};
        return ret;
    }

    protected String getStringWithWhitespace(final String text, final int whiteSpaces) {
        final StringBuilder builder = new StringBuilder();
        final char[] textChars = text.toCharArray();
        for(int i = 0; i < textChars.length; i++) {
            builder.append(textChars[i]);
            if(i < (textChars.length - 1)) {
                for (int j = 0; j < whiteSpaces; j++) {
                    builder.append(" ");
                }
            }
        }
        return builder.toString();
    }

    protected int getTotalTestPhases () {
        //make the progress value dynamically adjust to the number of test phases (dbl for the dbl arithmetic when dividing)
        int totalTestPhases = (int) baseTestPhases;

        return totalTestPhases;
    }

    protected float coordFW(final float x, final float y) {
        return x / y * (float)requestedCanvasWidth;
    }

    protected float coordFH(final float x, final float y) {
        return x / y * (float)requestedCanvasHeight;
    }

    protected float coordFW(final int x, final int y) {
        return (float) x / y * requestedCanvasWidth;
    }

    protected float coordFH(final int x, final int y) {
        return (float) x / y * requestedCanvasHeight;
    }

    protected static class RingArc {
        final RectF bounds;
        final Path path = new Path();
        final int startX;
        final int startY;
        final float strokeWidth;

        public RingArc(final int arcCenterX, final int arcCenterY, final float strokeWidth, final float absPosition) {
            final int gaugeWidth = (int)((float) arcCenterX * absPosition) - BIG_POINTER_SIZE;
            final int gaugeHeight = (int)((float) arcCenterY * absPosition) - BIG_POINTER_SIZE;
            bounds = new RectF(arcCenterX - gaugeWidth + strokeWidth/2f, arcCenterY - gaugeHeight + strokeWidth/2f,
                    arcCenterX + gaugeWidth - strokeWidth/2f, arcCenterY + gaugeHeight - strokeWidth/2f);
            startX = arcCenterX - gaugeWidth;
            startY = arcCenterY;
            path.addArc(getBounds(), 0, ARC_ANGLE);
            this.strokeWidth = strokeWidth;
        }

        public RectF getBounds() {
            return bounds;
        }

        public RectF getBounds(float offset) {
            final RectF newRect = new RectF(bounds);
            newRect.inset(offset, offset);
            return newRect;
        }

        public float getStrokeWidth() {
            return this.strokeWidth;
        }

        public Path getPath() {
            return path;
        }
    }
}
