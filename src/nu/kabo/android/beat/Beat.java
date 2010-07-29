package nu.kabo.android.beat;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

public class Beat extends AppWidgetProvider {
	
	Timer timer = null;
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// android delivers max 2 onUpdates per hour, so we need to make our own timer
		if (timer != null) {
			return;
		}
		timer = new Timer();
		timer.scheduleAtFixedRate(new MyTime(context, appWidgetManager), 1, 1000);
	}
	
	private class MyTime extends TimerTask {

		RemoteViews remoteViews;
		AppWidgetManager appWidgetManager;
		ComponentName thisWidget;
		
		public MyTime(Context context, AppWidgetManager appWidgetManager) {
			this.appWidgetManager = appWidgetManager;
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
			thisWidget = new ComponentName(context,	Beat.class);
		}
		
		public String calculateITime(Calendar t) {
			String ITime = "00" + (int)(( ( ( t.get(Calendar.HOUR_OF_DAY) + (t.get(Calendar.MINUTE)/60.0) + (t.get(Calendar.SECOND)/3600.0) ) ) / 24.0 ) * 1000);
			return (ITime.substring(ITime.length()-3));
		}
		
		public void run() {
			remoteViews.setTextViewText(R.id.widget_textview,
					"@" + calculateITime(Calendar.getInstance()));
			appWidgetManager.updateAppWidget(thisWidget, remoteViews);
		}
		
	}
	
}
