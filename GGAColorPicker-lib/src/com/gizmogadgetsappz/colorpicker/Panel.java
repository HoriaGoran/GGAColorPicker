package com.gizmogadgetsappz.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

public class Panel extends View
{
	public static final int CIRCLE_RADIUS = 14;
	public static final int PANNEL_LEFT = 0;
	public static final int PANNEL_RIGHT = 1;
	public static final int PANNEL_TRANSPARENCY = 2;
	public static final int CIRCLE = 3;
	public static final int RECTANGLE_RIGHT = 4;
	public static final int RECTANGLE_TRANSPARENCY = 5;
	private Paint paint;
	private RectF rect;
	private int what;

	public Panel(Context context, AttributeSet attrs, int defStyleAttr, int what)
	{
		super(context, attrs, defStyleAttr);
		this.what = what;
	}
	
	public Panel(Context context, AttributeSet attrs, int what)
	{
		super(context, attrs);
		this.what = what;
	}
	
	public Panel(Context context, int what)
	{
		super(context);
		this.what = what; 
	}
	
	public Panel(Context context, int what, int width, int height)
	{
		super(context);
		this.what = what;
		
		init(getContext(), what, width, height);
	}
	
	
	private void init(Context context, int what, int width, int height)
	{
		if(what != PANNEL_RIGHT && what != PANNEL_LEFT  && what != PANNEL_TRANSPARENCY && what != CIRCLE && what != RECTANGLE_RIGHT)
		{
			throw new IllegalArgumentException("what has to be one LEFT_PANNEL or RIGHT_PANNEL or PANNEL_TRANSPARENCY or WHITE_CIRCLE or BLACK_RECTANGLE");
		}
		
		paint = new Paint();
		rect = new RectF();
		
		rect.left = 0;
		rect.right = width;
		rect.top = 0;
		rect.bottom = height;
		
		switch(what)
		{
			case PANNEL_LEFT:
			{
				Shader shader1 = new LinearGradient(rect.left, rect.top, rect.left, rect.bottom, 0xffffffff, 0xff000000, TileMode.CLAMP);
				Shader shader2 = new LinearGradient(rect.left, rect.top, rect.right, rect.top, 0xffffffff, 0xffff0000, TileMode.CLAMP);
				
				ComposeShader shader = new ComposeShader(shader1, shader2, PorterDuff.Mode.MULTIPLY);
				paint.setShader(shader);
				break;
			}
			case PANNEL_RIGHT:
			{
				Shader shader = new LinearGradient(rect.left, rect.top, rect.left, rect.bottom, doRainbow(), null, TileMode.CLAMP);
				paint.setShader(shader);
				break;
			}
			case PANNEL_TRANSPARENCY:
			{
				Shader shader = new LinearGradient(rect.left, rect.top, rect.right, rect.top, 0xffffffff, 0x00ffffff, TileMode.CLAMP);
				paint.setShader(shader);
				break;
			}
			case RECTANGLE_TRANSPARENCY:
			case RECTANGLE_RIGHT:
			case CIRCLE:
			{
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(3);
				paint.setColor(Color.WHITE);
				
				break;
			}
		}
	}
	
	
	public void setPanelLeftColor(int color)
	{
		Shader shader1 = new LinearGradient(rect.left, rect.top, rect.left, rect.bottom, 0xffffffff, 0xff000000, TileMode.CLAMP);
		Shader shader2 = new LinearGradient(rect.left, rect.top, rect.right, rect.top, 0xffffffff, color, TileMode.CLAMP);
		
		ComposeShader shader = new ComposeShader(shader1, shader2, PorterDuff.Mode.MULTIPLY);
		paint.setShader(shader);
		
		invalidate();
	}
	
	
	public void setPanelTransparencyColor(int color)
	{
		Shader shader = new LinearGradient(rect.left, rect.top, rect.right, rect.top, color, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		
		invalidate();
	}
	

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		switch(what)
		{
			case PANNEL_LEFT:
			case PANNEL_RIGHT:
			case PANNEL_TRANSPARENCY:
			case RECTANGLE_TRANSPARENCY:
			case RECTANGLE_RIGHT:
			{
				canvas.drawRect(rect, paint);
				break;
			}
			case CIRCLE:
			{
				canvas.drawCircle(15, 15, CIRCLE_RADIUS, paint);
				break;
			}
		}
	}
	
	
	public Point getXY()
	{
		return new Point((int) rect.right / 2, (int) rect.bottom / 2);
	}


	private int [] doRainbow()
	{
		int[] color = new int[350];

		int count = 0;
		for (int i = color.length - 1; i >= 0; i--, count++)
		{
			color[count] = Color.HSVToColor(new float[] { i, 1f, 1f });
		}

		return color;
	}
}