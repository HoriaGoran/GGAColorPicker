package com.example.colorpickertest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gizmogadgetsappz.colorpicker.ColorPickerActivity;

public class MainActivity extends Activity
{
	private final int GRAB_COLOR_CODE = 100;
	private View result;
	private TextView text_color;
	private Button btn_grab;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		result = findViewById(R.id.test_to_color);
		btn_grab = (Button) findViewById(R.id.test_get_color);
		text_color = (TextView) findViewById(R.id.test_to_text); 
		
		btn_grab.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent i = new Intent(MainActivity.this, ColorPickerActivity.class);
				startActivityForResult(i, GRAB_COLOR_CODE);
			}
		});
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == GRAB_COLOR_CODE)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				Bundle b = data.getExtras();
				if(null != b)
				{
					int color = b.getInt(ColorPickerActivity.COLOR_KEY);
					String text = b.getString(ColorPickerActivity.CODE_KEY);
					
					result.setBackgroundColor(color);
					text_color.setText(text);
				}
			}
			else if (resultCode == Activity.RESULT_CANCELED)
			{
				
			}
		}
	}
}