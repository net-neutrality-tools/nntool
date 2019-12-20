/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nettest.nntool.android.app.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import at.alladin.nettest.nntool.android.app.R;

/**
 * <p>This abstract fragment class provides a simple way to create (non-)closeable full screen dialogs, containing a simple close button (X) as well as a confirm button,
 * similar to the guidelines found on material.io </p>
 * <p>Usage:
 *  <ul>
 *      <li>Implement {@link #getLayoutId()} to provide an android layout id.</li>
 *      <li>Implement {@link #isCancelable()} to tell the fragment if this dialog is cancelable</li>
 *      <li><strong>Do not override {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}</strong>, instead implement {@link #onViewCreated(View)},
 *          which is called inside {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} after the view has been initialized.</li>
 *      <li>To provide a title and to show/hide the close icon (which is hidden by default) use {@link #setToolbarTitle(String)} or {@link #setToolbarTitle(String, boolean)}</li>
 *      <li>{@link #setOnCloseListener(OnCloseListener)} and {@link #setOnConfirmListener(OnConfirmListener)} can be used to provide listeners for the close and confirm event.</li>
 *  </ul>
 * </p>
 * @author Lukasz Budryk (lb@alladin.at)
 */
public abstract class AbstractFullScreenDialogFragment extends DialogFragment {

    Toolbar toolbar;

    OnConfirmListener onConfirmListener;

    OnCloseListener onCloseListener;

    /**
     * Makes this dialog (non-)cancelable.
     * @return
     */
    public abstract boolean isCancelable();

    /**
     * Provides the android layout ID.
     * @return
     */
    public abstract int getLayoutId();

    /**
     * Is called after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has initialized the fragment's view.
     * @param v
     */
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
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(getLayoutId(), container, false);

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

    /**
     * Set toolbar title for this full screen dialog
     * @param title
     */
    protected void setToolbarTitle(final String title) {
        setToolbarTitle(title, false);
    }

    /**
     * Set toolbar title and show/hide close icon (which is hidden by default) for this full screen dialog
     * @param title
     * @param hasCloseIcon
     */
    protected void setToolbarTitle(final String title, final boolean hasCloseIcon) {
        if (toolbar == null) {
            Log.w(getClass().getSimpleName(), "Toolbar is null.");
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
