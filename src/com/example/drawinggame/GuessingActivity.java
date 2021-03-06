package com.example.drawinggame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.example.drawinggame.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class GuessingActivity extends Activity implements OnClickListener {

	private ImageButton doneBtn;
	
	public EditText guessStringET;
	
	private static final boolean AUTO_HIDE = false;
	private static final int AUTO_HIDE_DELAY_MILLIS = 2500;
	private static final boolean TOGGLE_ON_CLICK = true;
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	private SystemUiHider mSystemUiHider;
	
	public static int counterInt = 0;
	private Bitmap backgroundImg;
	private Canvas canvas;
	public static Bitmap bitMap;
	
	
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
		
		
		//backgroundImg = WordBankActivity.tinyDB.getImage("backgroundImg");
		//Toast.makeText(getApplicationContext(), "backgroundImg" + backgroundImg.getByteCount(), Toast.LENGTH_SHORT);
		// backgroundIMG is null; 
		//canvas = new Canvas(backgroundImg);
		//canvas = new Canvas(bitMap);
		
		
		
		
		
		//FileInputStream fin = null;
		Intent intent = getIntent();
		String pathImage = intent.getStringExtra("imagePath");
		long picName = intent.getLongExtra("picName", 0);
		loadImageFromStorage(pathImage, picName);

		
	   /* try {
	        fin = openFileInput("myImage.jpg");
	        if(fin !=null && ((CharSequence) fin).length() > 0) {
	            Bitmap bmp = BitmapFactory.decodeFile(getFilesDir().getAbsolutePath() + "/myImage.jpg");; 
	            img.setImageBitmap(bmp);
	         } else {
	            //input stream has not much data to convert into  Bitmap
	        	 System.out.println("Error mutha");
	          }
	    } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
		*/
		
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

	@Override
	public void onBackPressed()
	{
		AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
		saveDialog.setTitle("Exit Current Game");
		saveDialog.setMessage("Exit Current Game Fo Realz?");
		saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				GuessingActivity.this.finish();
			}
		});
		saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				dialog.cancel();
			}
		});
		saveDialog.show();
	}
	
	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	
	private void startDrawingActivity() {
		SharedPreferences dataCount = getSharedPreferences("counterInt", 0);
		int counterInt = dataCount.getInt("counterInt", 0);
		
		Intent i = new Intent(this, DrawingActivity.class);
		i.putExtra("guessString", guessStringET.getText().toString());
		
		counterInt += 1;
		SharedPreferences data = getSharedPreferences("counterInt", 0);
		SharedPreferences.Editor editor = data.edit();
		editor.putInt("counterInt", counterInt);
		editor.commit();
		
		startActivity(i);
		this.finish();
	}
	
	private void loadImageFromStorage(String path, long picName){

	    try {
	    	System.out.println("Path: "+path+" picName: "+picName);
	        File f=new File(path, picName + ".jpg");
	        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
	            ImageView img=(ImageView)findViewById(R.id.imageView1);
	        img.setImageBitmap(b);
	        //System.out.println("image: "+ img.getDrawingCacheBackgroundColor());
	    } 
	    catch (FileNotFoundException e) 
	    {
	        e.printStackTrace();
	    }
	}
}
