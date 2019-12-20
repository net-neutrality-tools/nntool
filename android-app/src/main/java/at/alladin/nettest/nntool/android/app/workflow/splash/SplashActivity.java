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

package at.alladin.nettest.nntool.android.app.workflow.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_WAIT_MS = 1200;

    final Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_view);
        handler.postDelayed(startMainActivityRunnable, SPLASH_WAIT_MS);
    }


    final Runnable startMainActivityRunnable = new Runnable() {
        @Override
        public void run() {
            final Intent startMainActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
            try {
                Bundle b = ActivityOptionsCompat.makeCustomAnimation(SplashActivity.this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivity(startMainActivityIntent, b);
            }
            catch (final IllegalArgumentException e) {
                //may crash on some samsung(?) or huawei(?) devices
                startActivity(startMainActivityIntent);
            }
            finish();
        }
    };
}
