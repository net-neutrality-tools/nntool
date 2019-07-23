package at.alladin.nettest.nntool.android.app.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class AlertDialogUtil {

    public static void showAlertDialog(final Context context, final int titleResId, final int contentResId) {
        new AlertDialog.Builder(context)
                .setTitle(titleResId)
                .setMessage(contentResId)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public static void showCancelDialog(final Context context, final int titleResId, final int contentResId, final DialogInterface.OnClickListener onOkListener, final DialogInterface.OnClickListener onCancelListener) {
        new AlertDialog.Builder(context)
                .setTitle(titleResId)
                .setMessage(contentResId)
                .setPositiveButton(android.R.string.ok, onOkListener)
                .setNegativeButton(android.R.string.cancel, onCancelListener)
                .show();
    }
}
