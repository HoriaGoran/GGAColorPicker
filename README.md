GGAColorPicker
==============

Color Picker Library for Android.
Can be used with Android 2.2 and above.

Setup
==============
- reference the GGAColorPicker library in your project propreties.
- add in your project manifest:
```
<activity android:name="com.gizmogadgetsappz.colorpicker.ColorPickerActivity" /> 
```
- start the ColorPickerActivity:
```
btn_grab.setOnClickListener(new OnClickListener()
{
	@Override
	public void onClick(View arg0)
	{
		Intent i = new Intent(MainActivity.this, ColorPickerActivity.class);
		startActivityForResult(i, GRAB_COLOR_CODE);
	}
});
```		
- catch the result:
```
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
	}
}
```

![GGAColorPicker1](https://raw2.github.com/HoriaGoran/GGAColorPicker/master/GGAColorPicker-lib/images/Screenshot_2014-02-09-15-13-05.png)
![GGAColorPicker1](https://raw2.github.com/HoriaGoran/GGAColorPicker/master/GGAColorPicker-lib/images/Screenshot_2014-02-09-15-13-35.png)
