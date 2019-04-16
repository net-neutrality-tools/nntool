package at.alladin.nettest.nntool.android.app.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;

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
}
