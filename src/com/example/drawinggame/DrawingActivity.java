package com.example.drawinggame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.CountDownTimer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

	Timer counter;

	private ArrayList<String> lofWords;

	private Random random; // random.nextInt(int n) inclusive 0 to exclusive n

	private String imagePath;
	
	PicInfo pIInstance = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_drawing);
		drawView = (DrawingView)findViewById(R.id.drawing);
		LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
		currPaint = (ImageButton)paintLayout.getChildAt(0);
		currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

		//TODO

		drawView.setDrawingCacheEnabled(true);

		// this is the important code :)  
		// Without it the view will have a dimension of 0,0 and the bitmap will be null          
		drawView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		drawView.layout(0, 0, drawView.getMeasuredWidth(), drawView.getMeasuredHeight()); 

		drawView.buildDrawingCache(true);

		//end shiet
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

		counter = new Timer(46000,1000);

		counter.start();

		pIInstance = PicInfo.getInstance();
		
		SharedPreferences dataCount = getSharedPreferences("counterInt", 0);
		int counterInt = dataCount.getInt("counterInt", 0);
		//Toast.makeText(getApplicationContext(), "counter: " + counterInt, Toast.LENGTH_SHORT).show();
		
		if(counterInt >= 4){ //CHANGE FOR THE COUNTER
			Intent intent = getIntent();
			String guessStringS = intent.getStringExtra("guessString");
			pIInstance.setGuess(guessStringS, counterInt);
			
			startGalleryActivity();
			SharedPreferences dataCount2 = getSharedPreferences("counterInt", 0);
			int counterInt2 = dataCount2.getInt("counterInt", 0);
			counterInt2 = 0;
			SharedPreferences data = getSharedPreferences("counterInt", 0);
			SharedPreferences.Editor editor = data.edit();
			editor.putInt("counterInt", counterInt2);
			editor.commit();
			counter.cancel();
			finish();
		}

		// Brandon's Guessing shiet
		guessStringTV = (TextView) findViewById(R.id.text_id);
		
		Intent intent = getIntent();
		String guessStringS = intent.getStringExtra("guessString");

		lofWords = WordBankActivity.tinyDB.getList("wordDB");

		if(counterInt == 0 && lofWords.size() > 0){
			random = new Random();
			int lofWArray = random.nextInt(lofWords.size());
			String startWord = lofWords.get(lofWArray);
			guessStringTV.setText(startWord);
			lofWords.remove(lofWArray);
			WordBankActivity.tinyDB.putList("wordDB", lofWords);
			pIInstance.setGuess(startWord, 0);
		}
		else if(guessStringS != null){
			guessStringTV.setText(guessStringS);
			pIInstance.setGuess(guessStringS, counterInt);
		}
		else guessStringTV.setText("Create a new WordBank");
		if(guessStringTV.equals("Create a new WordBank") && lofWords.size() > 0){
			Random random2 = new Random();
			int lofWArray2 = random2.nextInt(lofWords.size());
			String startWord2 = lofWords.get(lofWArray2);
			guessStringTV.setText(startWord2);
			lofWords.remove(lofWArray2);
			WordBankActivity.tinyDB.putList("wordDB", lofWords);
			pIInstance.setGuess(startWord2, 0);
			//TODO FIX THIS SHIEET
		}

	}
	
	/*public void setGuess(String guess, int count){
		if(count == 0) guess0 = guess;
		else if (count == 1) guess1 = guess;
		else if (count == 2) guess2 = guess;
		else if (count == 3) guess3 = guess;
	}*/

	public class Timer extends CountDownTimer{

		public Timer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			tv.setText("Times Up!");
			counter.cancel();
			drawView.setDrawingCacheEnabled(true);


			try {

			} catch (Exception e) {
				e.printStackTrace();
				//Toast.makeText(getApplicationContext(), "file not created", Toast.LENGTH_SHORT).show();
			}
			startGuessingActivity();

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
					counter.cancel();
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
		Bitmap b = Bitmap.createBitmap(drawView.getDrawingCache());
		//System.out.println("BITMAP IS HERE!!!!! b: "+ b.getDensity());
		long picName = UUID.randomUUID().getMostSignificantBits();
		imagePath = saveToInternalSorage(b, picName);
		System.out.println("Here is the imagePath: "+ imagePath + picName+".jpg");	

		Intent i = new Intent(this, GuessingActivity.class);
		i.putExtra("imagePath", imagePath);
		i.putExtra("picName", picName);
		pIInstance.setPath(imagePath);
		SharedPreferences dataCount = getSharedPreferences("counterInt", 0);
		int counterInt = dataCount.getInt("counterInt", 0);
		
		pIInstance.setImage(picName, counterInt);
		//imageSetter(picName, counterInt);
		startActivity(i);
		this.finish();
	}
/*
	private void imageSetter(long picName, int counter) {
		if(counter == 0) image0 = String.valueOf(picName)+".jpg";
		else if(counter == 1) image1 = String.valueOf(picName)+".jpg";
		else if(counter == 2) image2 = String.valueOf(picName)+".jpg";
		else if(counter == 3) image3 = String.valueOf(picName)+".jpg";
	}
*/
	private void startGalleryActivity() {
		Intent i = new Intent(this, GalleryActivity.class);
		startActivity(i);
		this.finish();
	}

	// http://stackoverflow.com/questions/17674634/saving-images-to-internal-memory-in-android
	private String saveToInternalSorage(Bitmap bitmapImage, long picName){
		ContextWrapper cw = new ContextWrapper(getApplicationContext());
		// path to /data/data/yourapp/app_data/imageDir
		File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
		// Create imageDir
		File mypath=new File(directory,picName+".jpg");

		FileOutputStream fos = null;
		try {           

			fos = new FileOutputStream(mypath);

			// Use the compress method on the BitMap object to write image to the OutputStream
			bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		drawView.setDrawingCacheEnabled(false); // clear drawing cache

		drawView.destroyDrawingCache();

		return directory.getAbsolutePath();
	}
	// s is either path or guess string dependant on gVp (guess=0 or path = 1)

}
