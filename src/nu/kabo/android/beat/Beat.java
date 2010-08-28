package nu.kabo.android.beat;

import java.util.Calendar;
import java.util.TimeZone;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

public class Beat extends AppWidgetProvider {
	
	protected static final String TAG = "Beat";
	protected Thread notifyingThread;
	protected ComponentName thisWidget;
	protected RemoteViews remoteViews;
	protected AppWidgetManager appWidgetManager;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			UpdateTime();
		}
	};
	
	private Runnable mTask = new Runnable() {
        public void run() {
        	try {
        		handler.sendEmptyMessage(0);
        		double cur_time = GetTime();
        		double cur_time_dec = cur_time-(int)cur_time;
				Thread.sleep((int)Math.ceil(87000*(1-cur_time_dec))); // some extra time here
			} catch (InterruptedException e) {
				return;
			} catch (Exception e) {
				Log.e(TAG, "Error: " + e.getMessage(), e);
			}
        	while(true) {
        		try {
            		handler.sendEmptyMessage(0);
            		Thread.sleep(86400); // sleep one beat
				} catch (InterruptedException e) {
					Log.d(TAG, "Caught interrupt, exiting thread");
					return;
				} catch (Exception e) {
					Log.e(TAG, "Error: " + e.getMessage(), e);
				}
        	}
        }
    };
    
	public void onEnabled(Context context) {
		thisWidget = new ComponentName(context, Beat.class);
		remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
		appWidgetManager = AppWidgetManager.getInstance(context);
		notifyingThread = new Thread(mTask, "NotifyingService");
		notifyingThread.start();
	}
	
	public void onDisabled(Context context) {
		// exit, causing the notifyingThread to die as well
		System.exit(0);
	}
	
	private void UpdateTime() {
		Log.d(TAG, "Updating time");
		remoteViews.setTextViewText(R.id.widget_textview,
				"@" + GetTimeString());
		appWidgetManager.updateAppWidget(thisWidget, remoteViews);
	}
	
	public double GetTime() {
		Calendar t = Calendar.getInstance(TimeZone.getTimeZone("GMT+0100"));
		return (double)(( ( ( t.get(Calendar.HOUR_OF_DAY) + (t.get(Calendar.MINUTE)/60.0) + (t.get(Calendar.SECOND)/3600.0) ) ) / 24.0 ) * 1000.0);
	}
	public String GetTimeString() {
		String ITime = "00" + (int)GetTime();
		return (ITime.substring(ITime.length()-3));
	}
	
}
