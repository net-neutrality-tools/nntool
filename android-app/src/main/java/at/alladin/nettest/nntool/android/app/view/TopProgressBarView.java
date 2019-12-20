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

package at.alladin.nettest.nntool.android.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import at.alladin.nettest.nntool.android.app.R;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class TopProgressBarView extends RelativeLayout {

    private TextView leftText;
    private TextView rightText;
    private AlladinTextView leftIcon;
    private AlladinTextView rightIcon;

    public TopProgressBarView(Context context) {
        super(context);
        init();
    }

    public TopProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.top_progress_bar_view, this);
        leftText = findViewById(R.id.top_progress_bar_view_left_text);
        rightText = findViewById(R.id.top_progress_bar_view_right_text);
        leftIcon = findViewById(R.id.top_progress_bar_view_left_icon);
        rightIcon = findViewById(R.id.top_progress_bar_view_right_icon);
    }

    public void setLeftText(final String text) {
        if (leftText != null) {
            leftText.setText(text);
        }
    }

    public void setRightText(final String text) {
        if (rightText != null) {
            rightText.setText(text);
        }
    }

    public void setLeftIcon(final String text) {
        if (leftIcon != null) {
            leftIcon.setText(text);
        }
    }

    public void setRightIcon(final String text) {
        if (rightIcon != null) {
            rightIcon.setText(text);
        }
    }
}
