package com.example.drawinggame;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;

@SuppressWarnings("deprecation")
public class GalleryActivity extends Activity { 

	//variable for selection intent
	private final int PICKER = 1;
	//variable to store the currently selected image
	private int currentPic = 0;
	//adapter for gallery view
	private PicAdapter imgAdapt;
	//gallery object
	private Gallery picGallery;
	//image view for larger display
	private ImageView picView;

	private Button mPlay;

	private PicInfo pIInstance = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		//call superclass method and set main content view
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);

		//get the large image view
		picView = (ImageView) findViewById(R.id.picture);

		//get the gallery view
		picGallery = (Gallery) findViewById(R.id.gallery);

		pIInstance = PicInfo.getInstance();
		System.out.println("Dir: " + pIInstance.getPath());
		for(int i = 0; i < 4; i++){
			System.out.println("Path: "+pIInstance.getImage(i));
			System.out.println("Guess: "+pIInstance.getGuess(i));
		}

		//create a new adapter
		imgAdapt = new PicAdapter(this);
		//set the gallery adapter
		picGallery.setAdapter(imgAdapt);

	

		//set long click listener for each gallery thumbnail item
		picGallery.setOnItemLongClickListener(new OnItemLongClickListener() {
			//handle long clicks
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				//update the currently selected position so that we assign the imported bitmap to correct item
				currentPic = position;
				//take the user to their chosen image selection app (gallery or file manager)
				Intent pickIntent = new Intent();
				pickIntent.setType("image/*");
				pickIntent.setAction(Intent.ACTION_GET_CONTENT);
				//we will handle the returned data in onActivityResult
				startActivityForResult(Intent.createChooser(pickIntent, "Select Picture"), PICKER);
				return true;
			}


		});







		//set the click listener for each item in the thumbnail gallery
		picGallery.setOnItemClickListener(new OnItemClickListener() {
			//handle clicks
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				//set the larger image view to display the chosen bitmap calling method of adapter class
				picView.setImageBitmap(imgAdapt.getPic(position));
			}
		});

		mPlay = (Button) findViewById(R.id.play);
		mPlay.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v2) {
				startDrawingActivity();
			}
		});
	}

	public class PicAdapter extends BaseAdapter {

		//use the default gallery background image
		int defaultItemBackground;

		//gallery context
		private Context galleryContext;

		//array to store bitmaps to display
		private Bitmap[] imageBitmaps;
		//placeholder bitmap for empty spaces in gallery
		Bitmap placeholder;

		//constructor
		public PicAdapter(Context c) {

			//instantiate context
			galleryContext = c;

			//create bitmap array
			imageBitmaps  = new Bitmap[10];
			//decode the placeholder image
			placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

			//set placeholder as all thumbnail images in the gallery initially

			for(int q = 0; q < 4; q++){
				imageBitmaps[q] = loadImageFromStorage(pIInstance.getPath(), pIInstance.getImage(q));
			}

			//get the styling attributes - use default Andorid system resources
			TypedArray styleAttrs = galleryContext.obtainStyledAttributes(R.styleable.PicGallery);
			//get the background resource
			defaultItemBackground = styleAttrs.getResourceId(
					R.styleable.PicGallery_android_galleryItemBackground, 0);
			//recycle attributes
			styleAttrs.recycle();
		}

		//BaseAdapter methods

		//return number of data items i.e. bitmap images
		public int getCount() {
			return imageBitmaps.length;
		}

		//return item at specified position
		public Object getItem(int position) {
			return position;
		}

		//return item ID at specified position
		public long getItemId(int position) {
			return position;
		}

		//get view specifies layout and display options for each thumbnail in the gallery
		public View getView(int position, View convertView, ViewGroup parent) {

			//create the view
			ImageView imageView = new ImageView(galleryContext);
			//specify the bitmap at this position in the array
			imageView.setImageBitmap(imageBitmaps[position]);
			//set layout options
			imageView.setLayoutParams(new Gallery.LayoutParams(300, 200));
			//scale type within view area
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			//set default gallery item background
			imageView.setBackgroundResource(defaultItemBackground);
			//return the view
			return imageView;
		}

		//custom methods for this app

		//helper method to add a bitmap to the gallery when the user chooses one
		public void addPic(Bitmap newPic)
		{
			//set at currently selected index
			imageBitmaps[currentPic] = newPic;
		}

		//return bitmap at specified position for larger display
		public Bitmap getPic(int posn)
		{
			//return bitmap at posn index
			return imageBitmaps[posn];
		}

		private Bitmap loadImageFromStorage(String path, long picName){
			Bitmap b = placeholder;
			try {
				File f=new File(path, picName+ ".jpg");
				b = BitmapFactory.decodeStream(new FileInputStream(f));
				//ImageView img=(ImageView)findViewById(R.id.imageView1);
				//img.setImageBitmap(b);
				return b;
				//System.out.println("image: "+ img.getDrawingCacheBackgroundColor());
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
			return null;

		}
	}

	/**
	 * Handle returning from gallery or file manager image selection
	 * - import the image bitmap
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			//check if we are returning from picture selection
			if (requestCode == PICKER) {

				//the returned picture URI
				Uri pickedUri = data.getData();

				//declare the bitmap
				Bitmap pic = null;
				//declare the path string
				String imgPath = "";

				//retrieve the string using media data
				String[] medData = { MediaStore.Images.Media.DATA };
				//query the data
				Cursor picCursor = managedQuery(pickedUri, medData, null, null, null);
				if(picCursor!=null)
				{
					//get the path string
					int index = picCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					picCursor.moveToFirst();
					imgPath = picCursor.getString(index);
				}
				else
					imgPath = pickedUri.getPath();

				//if and else handle both choosing from gallery and from file manager

				//if we have a new URI attempt to decode the image bitmap
				if(pickedUri!=null) {

					//set the width and height we want to use as maximum display
					int targetWidth = 600;
					int targetHeight = 400;

					//sample the incoming image to save on memory resources

					//create bitmap options to calculate and use sample size
					BitmapFactory.Options bmpOptions = new BitmapFactory.Options();

					//first decode image dimensions only - not the image bitmap itself
					bmpOptions.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(imgPath, bmpOptions);

					//work out what the sample size should be

					//image width and height before sampling
					int currHeight = bmpOptions.outHeight;
					int currWidth = bmpOptions.outWidth;

					//variable to store new sample size
					int sampleSize = 1;

					//calculate the sample size if the existing size is larger than target size
					if (currHeight>targetHeight || currWidth>targetWidth) 
					{
						//use either width or height
						if (currWidth>currHeight)
							sampleSize = Math.round((float)currHeight/(float)targetHeight);
						else 
							sampleSize = Math.round((float)currWidth/(float)targetWidth);
					}
					//use the new sample size
					bmpOptions.inSampleSize = sampleSize;

					//now decode the bitmap using sample options
					bmpOptions.inJustDecodeBounds = false;

					//get the file as a bitmap
					pic = BitmapFactory.decodeFile(imgPath, bmpOptions);

					//pass bitmap to ImageAdapter to add to array
					imgAdapt.addPic(pic);
					//redraw the gallery thumbnails to reflect the new addition
					picGallery.setAdapter(imgAdapt);

					//display the newly selected image at larger size
					picView.setImageBitmap(pic);
					//scale options
					picView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				}
			}
		}
		//superclass method
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void startDrawingActivity() {
		Intent i = new Intent(this, DrawingActivity.class);
		startActivity(i);
		this.finish();
	}

}
