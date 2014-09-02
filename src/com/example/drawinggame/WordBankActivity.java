package com.example.drawinggame;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


public class WordBankActivity extends Activity implements OnClickListener{
	
	public static ArrayList<String> wordBank;
	
	private EditText editText;
	
	private Button saveWordBtn;
	
	private ImageButton doneBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_word_bank);
		
		editText = (EditText) findViewById(R.id.textWordBank);
		
		doneBtn = (ImageButton) findViewById(R.id.save_btn);
		doneBtn.setOnClickListener(this);
		
		saveWordBtn = (Button) findViewById(R.id.word_bank);
		saveWordBtn.setOnClickListener(this);
		
	}
	
	
	public void onClick(View view) {
		// TODO "DONE BUTTON": Go back to drawing screen with guess displayed at top
		if(view.getId()==R.id.save_btn){
			//save drawing
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("StartGame?");
			saveDialog.setMessage("Start Game? Fo Realz?");
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
		else if(view.getId()==R.id.textWordBank){
			wordBank.add(editText.getText().toString());
			editText.setText(" ");
		}
	}

	
	private void startDrawingActivity() {
		Intent i = new Intent(this, DrawingActivity.class);
		startActivity(i);
		this.finish();
	}
}
