package at.alladin.nettest.nntool.android.app.workflow;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import at.alladin.nettest.nntool.android.app.R;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public abstract class AbstractFullScreenDialogFragment extends DialogFragment {

    Toolbar toolbar;

    OnConfirmListener onConfirmListener;

    OnCloseListener onCloseListener;

    public abstract boolean isCancelable();

    public abstract int getViewId();

    public abstract void onViewCreated(final View v);

    public void setOnConfirmListener(final OnConfirmListener listener) {
        this.onConfirmListener = listener;
    }

    public void setOnCloseListener(final OnCloseListener listener) {
        this.onCloseListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        getDialog().setCancelable(isCancelable());
    }

    public interface OnCloseListener {
        void onClose();
    }

    public interface OnConfirmListener {
        void onConfirm();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(getViewId(), container, false);

        toolbar = v.findViewById(R.id.toolbar_dialog);

        final Button b = v.findViewById(R.id.button_confirm);
        b.setOnClickListener(view -> {
            if (onConfirmListener != null) {
                onConfirmListener.onConfirm();
            }
            else {
                dismiss();
            }
        });

        onViewCreated(v);

        return v;
    }

    protected void setToolbarTitle(final String title) {
        setToolbarTitle(title, false);
    }

    protected void setToolbarTitle(final String title, final boolean hasCloseIcon) {
        if (toolbar == null) {
            return;
        }

        if (hasCloseIcon) {
            toolbar.setNavigationIcon(R.drawable.ic_close);
            toolbar.setNavigationOnClickListener(view -> {
                if (onCloseListener != null) {
                    onCloseListener.onClose();
                }
                else {
                    dismiss();
                }
            });
        }

        toolbar.setTitle(title);

    }
}
