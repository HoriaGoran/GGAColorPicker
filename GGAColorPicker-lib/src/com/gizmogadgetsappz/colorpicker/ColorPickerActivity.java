package com.gizmogadgetsappz.colorpicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ColorPickerActivity extends Activity
{
	public static final String COLOR_KEY = "color_key";
	public static final String CODE_KEY = "code_key";
	private Context context = ColorPickerActivity.this;
	private Panel panel_left;
	private Panel panel_right;
	private Panel panel_transparency;
	private Panel circle;
//	private View panel_circle;
	private Panel rectangle_right;
	private Panel rectangle_transparency;
	private RelativeLayout layout_left;
	private RelativeLayout layout_right;
	private RelativeLayout layout_transparency;
	private View result;
	private TextView color_text;
	private Bitmap bitmap_left;
	private Bitmap bitmap_right;
	private Bitmap bitmap_transparency;
	private boolean created = false;
	private CreateBitmapTask createBitmapLeftTask;
	private CreateBitmapTask createBitmapRightTask;
	private CreateBitmapTask createBitmapTransparency;
	private Helper circleHelper;
	private Helper rectangleRightHelper;
	private Helper rectangleTransparencyHelper;
	private int [] panel_left_location = new int[2];
	private int [] panel_right_location = new int[2];
	private int [] panel_transparency_location = new int[2];
	private Button bnt_ok;
	private Button bnt_cancel;
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);

		if(true == created)
		{
			created = false;
			
			circleHelper = new Helper();
			rectangleRightHelper = new Helper();
			rectangleTransparencyHelper = new Helper();
			
			panel_left = new Panel(context, Panel.PANNEL_LEFT, layout_left.getWidth(), layout_left.getHeight());
			panel_right = new Panel(context, Panel.PANNEL_RIGHT, layout_right.getWidth(), layout_right.getHeight());
			panel_transparency = new Panel(context, Panel.PANNEL_TRANSPARENCY, layout_transparency.getWidth(), layout_transparency.getHeight());
			circle = new Panel(context, Panel.CIRCLE, layout_left.getWidth(), layout_left.getHeight());
//			panel_circle = new View(context);
//			panel_circle.setBackgroundResource(R.drawable.circle);
			rectangle_right = new Panel(context, Panel.RECTANGLE_RIGHT, layout_right.getWidth(), 20);
			rectangle_transparency = new Panel(context, Panel.RECTANGLE_RIGHT, 20, layout_transparency.getHeight());
			
			circle.setOnTouchListener(circle_touch);
			rectangle_right.setOnTouchListener(rectangle_right_touch);
			rectangle_transparency.setOnTouchListener(rectangle_transparency_touch);
			
			layout_left.addView(panel_left);
			layout_left.addView(circle);
			layout_right.addView(panel_right);
			layout_right.addView(rectangle_right);
			layout_transparency.addView(panel_transparency);
			layout_transparency.addView(rectangle_transparency);
			
			panel_left.setDrawingCacheEnabled(true);
			panel_left.buildDrawingCache(false);
			panel_right.setDrawingCacheEnabled(true);
			panel_right.buildDrawingCache(false);
			panel_transparency.setDrawingCacheEnabled(true);
			panel_transparency.buildDrawingCache(false);
			
			
			createBitmapLeftTask = new CreateBitmapTask(panel_left, panel_left_location);
			createBitmapLeftTask.setOnCompleteListener(new CreateBitmapTask.OnComplete()
			{
				@Override
				public void onComplete(Object object)
				{
					cleanBitmap(bitmap_left);
					bitmap_left = (Bitmap) object;
					
					color = bitmap_left.getPixel(Panel.CIRCLE_RADIUS, Panel.CIRCLE_RADIUS);
					result.setBackgroundColor(color);
					color_text.setText("#" + Integer.toHexString(color).toUpperCase());
				}
			});
			createBitmapLeftTask.execute();

			
			createBitmapRightTask = new CreateBitmapTask(panel_right, panel_right_location);
			createBitmapRightTask.setOnCompleteListener(new CreateBitmapTask.OnComplete()
			{
				@Override
				public void onComplete(Object object)
				{
					cleanBitmap(bitmap_right);
					bitmap_right = (Bitmap) object;
					
					color = bitmap_right.getPixel(51, 0);
					panel_left.setPanelLeftColor(color);
				}
			});
			createBitmapRightTask.execute();
			
			
			createBitmapTransparency = new CreateBitmapTask(panel_transparency, panel_transparency_location);
			createBitmapTransparency.setOnCompleteListener(new CreateBitmapTask.OnComplete()
			{
				@Override
				public void onComplete(Object object)
				{
					cleanBitmap(bitmap_transparency);
					bitmap_transparency = (Bitmap) object;
				}
			});
			createBitmapTransparency.execute();
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.color_picker_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		layout_left = (RelativeLayout) findViewById(R.id.layout_left);
		layout_right = (RelativeLayout) findViewById(R.id.layout_right);
		layout_transparency = (RelativeLayout) findViewById(R.id.layout_transparency);
		result = findViewById(R.id.result);
		color_text = (TextView) findViewById(R.id.color_text);
		bnt_ok = (Button) findViewById(R.id.bnt_ok);
		bnt_cancel = (Button) findViewById(R.id.bnt_cancel);
		
		bnt_ok.setOnClickListener(btns_click);
		bnt_cancel.setOnClickListener(btns_click);

		created = true;
	}
	
	
	
	private OnClickListener btns_click = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			int id = v.getId();
			if (id == R.id.bnt_ok)
			{
				Bundle b = new Bundle();
				b.putInt(COLOR_KEY, color);
				b.putString(CODE_KEY, color_text.getText().toString());
				
				Intent returnIntent = new Intent();
				returnIntent.putExtras(b);
				setResult(RESULT_OK, returnIntent);
				finish();
			}
			else if (id == R.id.bnt_cancel)
			{
				finish();
			}
		}
	};
	

	
	private OnTouchListener circle_touch = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View view, MotionEvent event)
		{
			final int X = (int) event.getRawX();
			final int Y = (int) event.getRawY();
			int [] location = new int[2];
			
			switch (event.getAction() & MotionEvent.ACTION_MASK)
			{
				case MotionEvent.ACTION_DOWN:
				{
					RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
					circleHelper.set_xDelta(X - lParams.leftMargin);
					circleHelper.set_yDelta(Y - lParams.topMargin);
					break;
				}
				case MotionEvent.ACTION_MOVE:
				{
					view.getLocationInWindow(location);
					
					if(null != bitmap_left)
					{
						if(location[0] - panel_left_location[0] < bitmap_left.getWidth() || location[0] >= panel_left_location[0])
						{
							RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
							lp.leftMargin = X - circleHelper.get_xDelta();
							lp.topMargin = Y - circleHelper.get_yDelta();
							view.setLayoutParams(lp);
							
							try
							{
								color = bitmap_left.getPixel(location[0] - panel_left_location[0], location[1] - panel_left_location[1]);
								panel_transparency.setPanelTransparencyColor(color);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
						else if(location[1] - panel_left_location[1] < bitmap_left.getHeight() || location[1] >= panel_left_location[1])
						{
							RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
							lp.leftMargin = X - circleHelper.get_xDelta();
							lp.topMargin = Y - circleHelper.get_yDelta();
							view.setLayoutParams(lp);
							
							try
							{
								color = bitmap_left.getPixel(location[0] - panel_left_location[0], location[1] - panel_left_location[1]);
								panel_transparency.setPanelTransparencyColor(color);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
					}
					break;
				}
			    case MotionEvent.ACTION_UP:
			    {
					view.getLocationInWindow(location);
					
			    	if(location[0] > bitmap_left.getWidth() - Panel.CIRCLE_RADIUS * 2)
					{
						RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
						lp.leftMargin = bitmap_left.getWidth() - Panel.CIRCLE_RADIUS * 2 - 2;
						view.setLayoutParams(lp);
					}
			    	else if(location[0] <= panel_left_location[0])
			    	{
						RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
						lp.leftMargin = 0;
						view.setLayoutParams(lp);
					}
			    	else if(location[1] - panel_left_location[1] > bitmap_left.getHeight() - Panel.CIRCLE_RADIUS * 2)
					{
						RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
						lp.topMargin = bitmap_left.getHeight() - Panel.CIRCLE_RADIUS * 2 - 2;
						view.setLayoutParams(lp);
					}
			    	else if(location[1] <= panel_left_location[1])
			    	{
						RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
						lp.topMargin = 0;
						view.setLayoutParams(lp);
			    	}
			    	
					createBitmapTransparency = new CreateBitmapTask(panel_transparency, panel_transparency_location);
					createBitmapTransparency.setOnCompleteListener(new CreateBitmapTask.OnComplete()
					{
						@Override
						public void onComplete(Object object)
						{
							cleanBitmap(bitmap_transparency);
							bitmap_transparency = (Bitmap) object;
							
							color = getColorFromTransparent(color);
							String color_with_alpha = "#" + Integer.toHexString(Color.alpha(color)) + 
															Integer.toHexString(Color.red(color)) + 
															Integer.toHexString(Color.green(color)) + 
															Integer.toHexString(Color.blue(color));
							
							result.setBackgroundColor(color);
							color_text.setText(color_with_alpha.toUpperCase());
						}
					});
					createBitmapTransparency.execute();
		            break;
			    }
			}
			
			return true;
		}
	};
	
	
	
	private int color = 0;
	private OnTouchListener rectangle_right_touch = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View view, MotionEvent event)
		{
			final int Y = (int) event.getRawY();
			int [] location = new int[2];
			
			switch (event.getAction() & MotionEvent.ACTION_MASK)
			{
				case MotionEvent.ACTION_DOWN:
				{
					RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
					rectangleRightHelper.set_yDelta(Y - lParams.topMargin);
					break;
				}
				case MotionEvent.ACTION_MOVE:
				{
					view.getLocationInWindow(location);
					
					if(null != bitmap_right)
					{ 
						if(location[1] < bitmap_right.getHeight() - 20 || location[1] >= panel_right_location[1])
						{
							RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
							lp.topMargin = Y - rectangleRightHelper.get_yDelta();
							view.setLayoutParams(lp);
							
							try
							{
								color = bitmap_right.getPixel(51, location[1] - panel_right_location[1]);
								panel_left.setPanelLeftColor(color);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
					}
					
					break;
				}
			    case MotionEvent.ACTION_UP:
			    {
					view.getLocationInWindow(location);
				
			    	if(location[1] - panel_right_location[1] > bitmap_right.getHeight() - 20)
					{
						RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
						lp.topMargin = bitmap_right.getHeight() - 20;
						view.setLayoutParams(lp);
					}
			    	else if(location[1] <= panel_right_location[1])
			    	{
						RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
						lp.topMargin = 0;
						view.setLayoutParams(lp);
			    	}
			    	
			    	
					createBitmapLeftTask = new CreateBitmapTask(panel_left, panel_left_location);
					createBitmapLeftTask.setOnCompleteListener(new CreateBitmapTask.OnComplete()
					{
						@Override
						public void onComplete(Object object)
						{
							cleanBitmap(bitmap_left);
							bitmap_left = (Bitmap) object;
							
							color = getColorFromCircle(color);
							panel_transparency.setPanelTransparencyColor(color);
							
							createBitmapTransparency = new CreateBitmapTask(panel_transparency, panel_transparency_location);
							createBitmapTransparency.setOnCompleteListener(new CreateBitmapTask.OnComplete()
							{
								@Override
								public void onComplete(Object object)
								{
									cleanBitmap(bitmap_transparency);
									bitmap_transparency = (Bitmap) object;
									
									color = getColorFromTransparent(color);
									
									String color_with_alpha = "#" + Integer.toHexString(Color.alpha(color)) + 
																	Integer.toHexString(Color.red(color)) + 
																	Integer.toHexString(Color.green(color)) + 
																	Integer.toHexString(Color.blue(color));
									
									result.setBackgroundColor(color);
									color_text.setText(color_with_alpha.toUpperCase());
								}
							});
							createBitmapTransparency.execute();
						}
					});
					createBitmapLeftTask.execute();
					

		            break;
			    }
			}
			
			return true;
		}
	};
	
	
	
	private OnTouchListener rectangle_transparency_touch = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View view, MotionEvent event)
		{
			final int X = (int) event.getRawX();
			int [] location = new int[2];
			
			switch (event.getAction() & MotionEvent.ACTION_MASK)
			{
				case MotionEvent.ACTION_DOWN:
				{
					RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
					rectangleTransparencyHelper.set_xDelta(X - lParams.leftMargin);
					break;
				}
				case MotionEvent.ACTION_MOVE:
				{
					view.getLocationInWindow(location);
					
					if(null != bitmap_transparency)
					{
						if(location[0] - panel_left_location[0] < bitmap_transparency.getWidth() || location[0] >= panel_transparency_location[0])
						{
							RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
							lp.leftMargin = X - rectangleTransparencyHelper.get_xDelta();
							view.setLayoutParams(lp);
							
							try
							{
								color = bitmap_transparency.getPixel(location[0] - panel_transparency_location[0], location[1] - panel_transparency_location[1]);
								String color_with_alpha = "#" + Integer.toHexString(Color.alpha(color)) + 
																Integer.toHexString(Color.red(color)) + 
																Integer.toHexString(Color.green(color)) + 
																Integer.toHexString(Color.blue(color));
								
								result.setBackgroundColor(color);
								color_text.setText(color_with_alpha.toUpperCase());
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
					}
					
					break;
				}
			    case MotionEvent.ACTION_UP:
			    {
					view.getLocationInWindow(location);
				
			    	if(location[0] - panel_transparency_location[0] > bitmap_transparency.getWidth() - 20)
					{
						RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
						lp.leftMargin = bitmap_transparency.getWidth() - 20;
						view.setLayoutParams(lp);
					}
			    	else if(location[0] <= panel_transparency_location[0])
			    	{
						RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
						lp.leftMargin = 0;
						view.setLayoutParams(lp);
			    	}
		            break;
			    }
			}
			
			return true;
		}
	};
	
	

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		
		if(null != createBitmapLeftTask)
		{
			createBitmapLeftTask.cancel(true);
			createBitmapLeftTask = null;
		}
		
		if(null != createBitmapRightTask)
		{
			createBitmapRightTask.cancel(true);
			createBitmapRightTask = null;
		}
		
		if(null != createBitmapTransparency)
		{
			createBitmapTransparency.cancel(true);
			createBitmapTransparency = null;
		}
	}
	
	
	private void cleanBitmap(Bitmap b)
	{
		if(null != b)
		{
			b.recycle();
			b = null;
		}
	}
	
	
	private class Helper
	{
		private int _xDelta;
		private int _yDelta;
		
		public Helper()
		{
			_xDelta = 0;
			_yDelta = 0;
		}
		
		public int get_xDelta()
		{
			return _xDelta;
		}
		public void set_xDelta(int _xDelta)
		{
			this._xDelta = _xDelta;
		}
		
		
		public int get_yDelta()
		{
			return _yDelta;
		}
		public void set_yDelta(int _yDelta)
		{
			this._yDelta = _yDelta;
		}
	}
	
	
	private int getColorFromCircle(int old_color)
	{
		try
		{
			int [] location = new int[2];
			circle.getLocationOnScreen(location);
			return bitmap_left.getPixel(location[0] - panel_left_location[0], location[1] - panel_left_location[1]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return old_color;
		}
	}
	
	
	private int getColorFromTransparent(int old_color)
	{
		try
		{
			int [] location = new int[2];
			rectangle_transparency.getLocationOnScreen(location);
			return bitmap_transparency.getPixel(location[0] - panel_transparency_location[0], location[1] - panel_transparency_location[1]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return old_color;
		}
	}
}