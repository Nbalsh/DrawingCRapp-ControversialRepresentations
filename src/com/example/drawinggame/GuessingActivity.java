package com.example.drawinggame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class GuessingActivity extends Activity implements OnClickListener {

	ImageButton doneBtn;

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
					//startDrawingActivity();
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
	
	
	private void startDrawingActivity() {
		Intent i = new Intent(this, DrawingActivity.class);
		startActivity(i);
	}
}
