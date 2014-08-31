package com.example.drawinggame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.CountDownTimer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.View.OnClickListener;
import android.widget.Toast;
//hope this works

public class DrawingActivity extends Activity implements OnClickListener{

	private ImageView currPaint, drawBtn; //should be ImageButton?
	private int paintColor;
	private DrawingView drawView;
	private DrawingActivity drawPaint; 

	private float smallBrush, mediumBrush, largeBrush;

	private ImageButton eraseBtn, newBtn, saveBtn;
	
	private TextView tv;
	
	public TextView guessStringTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_drawing);
		drawView = (DrawingView)findViewById(R.id.drawing);
		LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
		currPaint = (ImageButton)paintLayout.getChildAt(0);
		currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));


		smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);
		drawBtn = (ImageButton)findViewById(R.id.draw_btn);
		drawBtn.setOnClickListener(this);

		eraseBtn = (ImageButton) findViewById(R.id.erase_btn);
		eraseBtn.setOnClickListener(this);

		newBtn = (ImageButton)findViewById(R.id.new_btn);
		newBtn.setOnClickListener(this);

		saveBtn = (ImageButton)findViewById(R.id.save_btn);
		saveBtn.setOnClickListener(this);
		
		tv = (TextView)findViewById(R.id.timer);

        Timer counter = new Timer(46000,1000);

        counter.start();
        
        // Brandon's Guessing shiet
        guessStringTV = (TextView) findViewById(R.id.text_id);
        
        Intent intent = getIntent();
		String guessStringS = intent.getStringExtra("guessString");
		guessStringTV.setText(guessStringS);
	}
	
	public class Timer extends CountDownTimer{

        public Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
        	tv.setText("Times Up!");
        	
        	drawView.setDrawingCacheEnabled(true);

			File file = new File(DrawingActivity.this.getFilesDir(), "DrawingCRApp2");
			if(!file.exists()){
				file.mkdirs();
				
			}
			//Toast.makeText(getApplicationContext(), "test2", Toast.LENGTH_SHORT).show();
			File pictureFile = new File(file.getPath(), UUID.randomUUID().toString()+".png");
			
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				//boolean successfullyCompressed = drawView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
				
				//trying crap from: http://developer.android.com/training/basics/data-storage/shared-preferences.html
				
				SharedPreferences sharedPref = DrawingActivity.this.getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(getString(R.string.saved_filed_name), pictureFile.getPath());
				editor.commit();
				
				
				//Toast.makeText(getApplicationContext(), "file compressed: " + successfullyCompressed, Toast.LENGTH_SHORT).show();
				//Toast.makeText(getApplicationContext(), pictureFile.getPath(), Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
				//Toast.makeText(getApplicationContext(), "file not created", Toast.LENGTH_SHORT).show();
			}
			startGuessingActivity();
			//drawView.destroyDrawingCache();
			//drawView.startNew();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv.setText("" + millisUntilFinished/1000);
        }

    }

	public void paintClicked(View view){
		if(view!=currPaint){
			drawView.setErase(false);
			ImageButton imgView = (ImageButton)view;
			String color = view.getTag().toString();
			drawView.setColor(color);
			imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
			currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
			currPaint=(ImageButton)view;
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view.getId()==R.id.draw_btn){
			//draw button clicked
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle("Brush size:");
			brushDialog.setContentView(R.layout.brush_chooser);

			drawView.setErase(false);

			ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					drawView.setBrushSize(smallBrush);
					drawView.setLastBrushSize(smallBrush);
					brushDialog.dismiss();
				}
			});

			ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					drawView.setBrushSize(mediumBrush);
					drawView.setLastBrushSize(mediumBrush);
					brushDialog.dismiss();
				}
			});


			ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					drawView.setBrushSize(largeBrush);
					drawView.setLastBrushSize(largeBrush);
					brushDialog.dismiss();
				}
			}); 	

			brushDialog.show();
		}

		else if(view.getId()==R.id.erase_btn){
			//switch to erase - choose size
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle("Eraser size:");
			brushDialog.setContentView(R.layout.brush_chooser);

			ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					drawView.setErase(true);
					drawView.setBrushSize(smallBrush);
					brushDialog.dismiss();
				}
			});
			ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					drawView.setErase(true);
					drawView.setBrushSize(mediumBrush);
					brushDialog.dismiss();
				}
			});
			ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					drawView.setErase(true);
					drawView.setBrushSize(largeBrush);
					brushDialog.dismiss();
				}
			});
			brushDialog.show();
		}

		else if(view.getId()==R.id.new_btn){
			//new button
			AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
			newDialog.setTitle("New drawing");
			newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
			newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					drawView.startNew();
					dialog.dismiss();
				}
			});
			newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					dialog.cancel();
				}
			});
			newDialog.show();
		}

		else if(view.getId()==R.id.save_btn){
			//save drawing
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Save drawing");
			saveDialog.setMessage("Save drawing to device Gallery?");
			saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
				//public void onClick(View v){
					startGuessingActivity();
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
			//Toast.makeText(getApplicationContext(), "test1", Toast.LENGTH_SHORT).show();
			drawView.setDrawingCacheEnabled(true);

			File file = new File(DrawingActivity.this.getFilesDir(), "DrawingCRApp2");
			if(!file.exists()){
				file.mkdirs();
				
			}
			//Toast.makeText(getApplicationContext(), "test2", Toast.LENGTH_SHORT).show();
			File pictureFile = new File(file.getPath(), UUID.randomUUID().toString()+".png");
			
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				boolean successfullyCompressed = drawView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
				
				//trying crap from: http://developer.android.com/training/basics/data-storage/shared-preferences.html
				
				SharedPreferences sharedPref = DrawingActivity.this.getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(getString(R.string.saved_filed_name), pictureFile.getPath());
				editor.commit();
				
				
				//Toast.makeText(getApplicationContext(), "file compressed: " + successfullyCompressed, Toast.LENGTH_SHORT).show();
				//Toast.makeText(getApplicationContext(), pictureFile.getPath(), Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "file not created", Toast.LENGTH_SHORT).show();
			}
			


			/*String imgSaved = MediaStore.Images.Media.insertImage(
					getContentResolver(), drawView.getDrawingCache(),
					UUID.randomUUID().toString()+".png", "drawing");
			if(imgSaved!=null){
				Toast savedToast = Toast.makeText(getApplicationContext(), 
						"Drawing saved to Gallery!", Toast.LENGTH_SHORT);
				savedToast.show();
			}
			else{
				Toast unsavedToast = Toast.makeText(getApplicationContext(), 
						"Oops! Image could not be saved.", Toast.LENGTH_SHORT);
				unsavedToast.show();
			}
			*/
			drawView.destroyDrawingCache();
		}

	}
	
	@Override
	public void onBackPressed()
	{
		AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
		saveDialog.setTitle("Exit Current Game");
		saveDialog.setMessage("Exit Current Game Fo Realz?");
		saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				DrawingActivity.this.finish();
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
	 * BRANDON"S GUESSING ACTIVITY SHIT
	 */
	private void startGuessingActivity() {
		Intent i = new Intent(this, GuessingActivity.class);
		startActivity(i);
		this.finish();
	}
}
