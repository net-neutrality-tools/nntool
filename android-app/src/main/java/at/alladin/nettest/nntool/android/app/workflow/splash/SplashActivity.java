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
