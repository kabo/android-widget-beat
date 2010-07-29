package nu.kabo.android.beat;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

public class Beat extends AppWidgetProvider {
	
	private static final String TAG = "Beat";
	public static final int UPDATE = 1;
	
	public void onDisabled(Context context) {
		//context.unregisterReceiver(this);
		stopTimer(context);
	}
	
	public void onEnabled(Context context) {
		// sätt alarm-manager på att trigga onUpdate
		//context.registerReceiver(this, new IntentFilter("android.intent.action.TIME_TICK"));
		Log.d(TAG, "enabled");
		startTimer(context);
	}
	
	protected PendingIntent getPendingIntentUpdate(Context context) {
		Intent i = new Intent();
		i.setAction("nu.kabo.android.beat.UPDATE");
		i.setClassName(context.getPackageName(), Beat.class.getName());
		return PendingIntent.getBroadcast(
				context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT
		);
	}
	
	protected void startTimer(Context context) {
		Log.d(TAG, "startTimer");
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(
				AlarmManager.ELAPSED_REALTIME, 
				SystemClock.elapsedRealtime()+1000, 
				10000, 
				getPendingIntentUpdate(context)
		);
	}
	
	protected void stopTimer(Context context) {
		Log.d(TAG, "stopTimer");
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(getPendingIntentUpdate(context));
	}
	
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.d(TAG, intent.getAction());
		if (intent.getAction().equals("nu.kabo.android.beat.UPDATE")) {
			Log.d(TAG, "Update");
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
			ComponentName thisWidget = new ComponentName(context, Beat.class);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			
			remoteViews.setTextViewText(R.id.widget_textview,
					"@" + calculateITime(Calendar.getInstance(TimeZone.getTimeZone("GMT+0100"))));
			appWidgetManager.updateAppWidget(thisWidget, remoteViews);
		} else if (intent.getAction().equals("android.intent.action.SCREEN_ON")) {
			Log.d(TAG, "screen_on");
			startTimer(context);
		} else if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
			Log.d(TAG, "screen_off");
			stopTimer(context);
		}
	}
	
	public String calculateITime(Calendar t) {
		String ITime = "00" + (int)(( ( ( t.get(Calendar.HOUR_OF_DAY) + (t.get(Calendar.MINUTE)/60.0) + (t.get(Calendar.SECOND)/3600.0) ) ) / 24.0 ) * 1000);
		return (ITime.substring(ITime.length()-3));
	}
	
}
