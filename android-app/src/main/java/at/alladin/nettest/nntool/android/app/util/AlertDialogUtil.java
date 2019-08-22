package at.alladin.nettest.nntool.android.app.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class AlertDialogUtil {

    public static void showAlertDialog(final Context context, final int titleResId, final int contentResId) {
        showAlertDialog(context, titleResId, contentResId, null);
    }

    public static void showAlertDialog(final Context context, final int titleResId, final int contentResId, final DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(titleResId)
                .setMessage(contentResId)
                .setPositiveButton(android.R.string.ok, listener)
                .show();
    }

    public static void showCancelDialog(final Context context, final int titleResId, final int contentResId, final DialogInterface.OnClickListener onOkListener, final DialogInterface.OnClickListener onCancelListener) {
        showCustomDialog(context, titleResId, contentResId, android.R.string.ok, android.R.string.cancel, onOkListener, onCancelListener);
    }

    public static void showCustomDialog(final Context context, final int titleResId, final int contentResId, final int okButtonResId, final int cancelButtonResId, final DialogInterface.OnClickListener onOkListener, final DialogInterface.OnClickListener onCancelListener) {
        new AlertDialog.Builder(context)
                .setTitle(titleResId)
                .setMessage(contentResId)
                .setPositiveButton(okButtonResId, onOkListener)
                .setNegativeButton(cancelButtonResId, onCancelListener)
                .show();
    }

}
