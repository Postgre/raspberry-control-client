/*
 * Copyright 2012 Romain Guy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skalski.raspberrycontrol;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import com.skalski.raspberrycontrol.Custom_SpotlightView.AnimationSetupCallback;

public class Activity_SplashSpotlight extends Activity {

	/*
	 * SplashSpotlight variables
	 */
	private final int SPLASH_DISPLAY_LENGTH = 2000;

	/*
	 * SplashSpotlight - onCreate()
	 */
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        if (Activity_Settings.pref_splash_screen_disabled(getBaseContext())) {
            Intent intent;
            intent = new Intent(getApplicationContext(), Activity_Login.class);
            startActivity(intent);
            Activity_SplashSpotlight.this.finish();
        }

        ActionBar actionBar = getActionBar();
        actionBar.hide();
		setContentView(R.layout.activity_splashspotlight);

		Custom_SpotlightView spotlight = (Custom_SpotlightView) findViewById(R.id.splash_spotlight);
		spotlight.setAnimationSetupCallback(new AnimationSetupCallback() {
			@Override
			public void onSetupAnimation(Custom_SpotlightView spotlight) {
				createAnimation(spotlight);
			}
		});
	}

	/*
	 * SplashSpotlight - createAnimation()
	 */
	private void createAnimation (final Custom_SpotlightView spotlight) {

		View top = findViewById(R.id.splash_title_1);
		View bottom = findViewById(R.id.splash_title_2);

		final float textHeight = bottom.getBottom() - top.getTop();
		final float startX = top.getLeft();
		final float startY = top.getTop() + textHeight / 2.0f;
		final float endX = Math.max(top.getRight(), bottom.getRight());

		spotlight.setMaskX(endX);
		spotlight.setMaskY(startY);

		spotlight.animate().alpha(1.0f).withLayer().withEndAction(new Runnable() {

			@Override
			public void run() {
				ObjectAnimator moveLeft = ObjectAnimator.ofFloat(spotlight, "maskX", endX, startX);
				moveLeft.setDuration(2000);

				float startScale = spotlight.computeMaskScale(textHeight);
				ObjectAnimator scaleUp = ObjectAnimator.ofFloat(spotlight, "maskScale",
                                                                startScale, startScale * 3.0f);
				scaleUp.setDuration(2000);

				ObjectAnimator moveCenter = ObjectAnimator.ofFloat(spotlight, "maskX",
                                                                   spotlight.getWidth() / 2.0f);
				moveCenter.setDuration(1000);

				ObjectAnimator moveUp = ObjectAnimator.ofFloat(spotlight, "maskY",
                                                               spotlight.getHeight() / 2.0f);
				moveUp.setDuration(1000);

				ObjectAnimator superScale = ObjectAnimator.ofFloat(spotlight, "maskScale",
						spotlight.computeMaskScale(Math.max(spotlight.getHeight(),
                                                            spotlight.getWidth()) * 1.7f));
				superScale.setDuration(2000);

				AnimatorSet set = new AnimatorSet();
				set.play(moveLeft).with(scaleUp);
				set.play(moveCenter).after(scaleUp);
				set.play(moveUp).after(scaleUp);
				set.play(superScale).after(scaleUp);
				set.start();

				set.addListener(new AnimatorListener() {

					@Override
					public void onAnimationStart(Animator animation) {
					}

					@Override
					public void onAnimationRepeat(Animator animation) {
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						findViewById(R.id.splash_content).setVisibility(View.VISIBLE);
						findViewById(R.id.splash_spotlight).setVisibility(View.GONE);
						getWindow().setBackgroundDrawable(null);

			            new Handler().postDelayed(new Runnable()
			            {
			                public void run()
			                {
			                    Activity_SplashSpotlight.this.finish();
			                    Intent mainIntent = new Intent(Activity_SplashSpotlight.this,
                                                               Activity_Login.class);
			                    Activity_SplashSpotlight.this.startActivity(mainIntent);
			                }
			            }, SPLASH_DISPLAY_LENGTH);
					}

					@Override
					public void onAnimationCancel(Animator animation) {
					}
				});
			}
		});
	}

    /*
     * SplashSpotlight - onKeyDown()
     *   Disable BACK button
     */
    @Override
    public boolean onKeyDown (int keyCode, @NonNull KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK
            && event.getRepeatCount() == 0) {
            onBackPressed();
        }

        return true;
    }

    /*
     * SplashSpotlight - onBackPressed()
     *   Disable BACK button
     */
    @Override
    public void onBackPressed () {
    }
}
