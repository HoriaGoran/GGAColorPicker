package com.gizmogadgetsappz.colorpicker;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class CreateBitmapTask extends AsyncTask<Object, Object, Bitmap>
{
	private OnComplete onComplete;
	private Panel panel;
	private int [] location;
	
	public CreateBitmapTask(Panel panel, int [] location)
	{
		this.panel = panel;
		this.location = location;
	}
	
	@Override
	protected Bitmap doInBackground(Object... params)
	{
		panel.destroyDrawingCache();
		
		while(null != panel && null == panel.getDrawingCache(false) && !this.isCancelled())
		{
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		panel.getLocationInWindow(location);
		return Bitmap.createBitmap(panel.getDrawingCache(false));
	}

	@Override
	protected void onPostExecute(Bitmap result)
	{
		super.onPostExecute(result);
		
		if(null != onComplete)
		{
			onComplete.onComplete(result);
		}
	}


	public interface OnComplete
	{
		public abstract void onComplete(Object object);
	}
	public void setOnCompleteListener(OnComplete l)
	{
		onComplete = l;
	}
}