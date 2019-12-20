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
import at.alladin.nettest.nntool.android.app.util.info.system.CurrentSystemInfo;
import at.alladin.nettest.nntool.android.app.util.info.system.SystemInfoEvent;
import at.alladin.nettest.nntool.android.app.util.info.system.SystemInfoListener;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class CpuAndRamView extends RelativeLayout implements SystemInfoListener {

    private final static String TAG = CpuAndRamView.class.getSimpleName();

    TextView cpuText;
    TextView ramText;

    public CpuAndRamView(Context context) {
        super(context);
        init();
    }

    public CpuAndRamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CpuAndRamView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.cpu_ram_view, this);
        ramText = findViewById(R.id.ram_status_text);
        cpuText = findViewById(R.id.cpu_status_text);
    }

    @Override
    public void onSystemInfoUpdate(final SystemInfoEvent systemInfoEvent) {
        if (systemInfoEvent != null && systemInfoEvent.getCurrentSystemInfo() != null) {
            final CurrentSystemInfo currentSystemInfo = systemInfoEvent.getCurrentSystemInfo();
            final CurrentSystemInfo.MemUsage memUsage = currentSystemInfo.getMemUsage();

            if (memUsage != null && memUsage.getMemoryUsage() != null) {
                ramText.setText(((int) (memUsage.getMemoryUsage() * 100f)) + "%");
            }
            else {
                ramText.setText(R.string.not_available_short);
            }

            if (currentSystemInfo.getCpuUsage() != null) {
                cpuText.setText(((int) (currentSystemInfo.getCpuUsage() * 100f)) + "%");
            }
            else {
                cpuText.setText(R.string.not_available_short);
            }
        }
        else {
            cpuText.setText(R.string.not_available_short);
            ramText.setText(R.string.not_available_short);
        }
    }
}
