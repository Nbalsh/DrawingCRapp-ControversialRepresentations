package com.example.drawinggame;

import com.example.drawinggame.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class GuessingActivity extends Activity implements OnClickListener {

	private ImageButton doneBtn;
	
	public EditText guessStringET;
	
	private static final boolean AUTO_HIDE = false;
	private static final int AUTO_HIDE_DELAY_MILLIS = 5000;
	private static final boolean TOGGLE_ON_CLICK = true;
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	private SystemUiHider mSystemUiHider;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guessing);
		/**
		 * TODO shit; get image from file database and display it.
		 * Have text box that is displayed over image and a textbox that pops up when screen tapped to guess
		 * Done button [onClick Above]
		 */

		doneBtn = (ImageButton) findViewById(R.id.save_btn);
		doneBtn.setOnClickListener(this);
		
		
		guessStringET = (EditText) findViewById(R.id.text);
		
		
		
		final View controlsView = findViewById(R.id.guessscreen_content_controls);
		final View contentView = findViewById(R.id.guessscreen_content);
		
		// Set up an instance of SystemUiHider to control the system UI for
				// this activity.
				mSystemUiHider = SystemUiHider.getInstance(this, contentView,
						HIDER_FLAGS);
				mSystemUiHider.setup();
				mSystemUiHider
						.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
							// Cached values.
							int mControlsHeight;
							int mShortAnimTime;

							@Override
							@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
							public void onVisibilityChange(boolean visible) {
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
									// If the ViewPropertyAnimator API is available
									// (Honeycomb MR2 and later), use it to animate the
									// in-layout UI controls at the bottom of the
									// screen.
									if (mControlsHeight == 0) {
										mControlsHeight = controlsView.getHeight();
									}
									if (mShortAnimTime == 0) {
										mShortAnimTime = getResources().getInteger(
												android.R.integer.config_shortAnimTime);
									}
									controlsView
											.animate()
											.translationY(visible ? 0 : mControlsHeight)
											.setDuration(mShortAnimTime);
								} else {
									// If the ViewPropertyAnimator APIs aren't
									// available, simply show or hide the in-layout UI
									// controls.
									controlsView.setVisibility(visible ? View.VISIBLE
											: View.GONE);
								}

								if (visible && AUTO_HIDE) {
									// Schedule a hide().
									delayedHide(AUTO_HIDE_DELAY_MILLIS);
								}
							}
						});

				// Set up the user interaction to manually show or hide the system UI.
				contentView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (TOGGLE_ON_CLICK) {
							mSystemUiHider.toggle();
						} else {
							mSystemUiHider.show();
						}
					}
				});

				// Upon interacting with UI controls, delay any scheduled hide()
				// operations to prevent the jarring behavior of controls going away
				// while interacting with the UI.
				findViewById(R.id.save_btn).setOnTouchListener(
						mDelayHideTouchListener);
				
		
	}


	@Override
	public void onClick(View view) {
		// TODO "DONE BUTTON": Go back to drawing screen with guess displayed at top
		if(view.getId()==R.id.save_btn){
			//save drawing
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Done?");
			saveDialog.setMessage("Done? Fo Realz?");
			saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					//public void onClick(View v){
					startDrawingActivity();
					/**
					 * BRANDON CHANGED THIS MICRO METHOD
					 */
				}
			});
			saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					dialog.cancel();
				}
			});
			saveDialog.show();
		}
	}
	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	
	private void startDrawingActivity() {
		Intent i = new Intent(this, DrawingActivity.class);
		i.putExtra("guessString", guessStringET.getText().toString());
		startActivity(i);
		this.finish();
	}
}
