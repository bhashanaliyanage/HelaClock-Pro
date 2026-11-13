package lk.sanoj.helaclok.pro.HelaClockPro;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.widget.RemoteViews;

/** Updates the existing widget composition without a continuously running service. */
public class ClockWidget extends AppWidgetProvider {
    private static final String ACTION_TICK = "lk.sanoj.helaclok.pro.HelaClockPro.WIDGET_TICK";

    @Override public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (ACTION_TICK.equals(action) || Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)
                || Intent.ACTION_TIME_CHANGED.equals(action)
                || Intent.ACTION_TIMEZONE_CHANGED.equals(action)
                || "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED".equals(action)) {
            refreshAll(context);
        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            int[] ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            if (ids == null || ids.length == 0) refreshAll(context);
        }
    }

    @Override public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        if (ids.length == 0) return;
        Intent openClock = new Intent(AlarmClock.ACTION_SHOW_ALARMS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent click = PendingIntent.getActivity(context, 0, openClock,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        RemoteViews views = ClockWidgetRenderer.createRemoteViews(context);
        views.setOnClickPendingIntent(R.id.widget_canvas, click);
        views.setOnClickPendingIntent(R.id.widget_image, click);
        for (int id : ids) manager.updateAppWidget(id, views);
        scheduleNext(context);
    }

    @Override public void onAppWidgetOptionsChanged(Context context, AppWidgetManager manager,
                                                     int id, Bundle options) {
        super.onAppWidgetOptionsChanged(context, manager, id, options);
        onUpdate(context, manager, new int[]{id});
    }

    @Override public void onDisabled(Context context) {
        super.onDisabled(context);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarms.cancel(tickIntent(context));
    }

    static boolean hasWidgets(Context context) {
        return AppWidgetManager.getInstance(context).getAppWidgetIds(
                new ComponentName(context, ClockWidget.class)).length != 0;
    }

    static boolean canScheduleExact(Context context) {
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        return Build.VERSION.SDK_INT < 31 || alarms.canScheduleExactAlarms();
    }

    static void refreshAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, ClockWidget.class));
        if (ids.length != 0) new ClockWidget().onUpdate(context, manager, ids);
    }

    private static PendingIntent tickIntent(Context context) {
        Intent intent = new Intent(context, ClockWidget.class).setAction(ACTION_TICK);
        return PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private static void scheduleNext(Context context) {
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long nextMinute = (System.currentTimeMillis() / 60000L + 1L) * 60000L;
        try {
            if (canScheduleExact(context)) {
                alarms.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextMinute, tickIntent(context));
            } else {
                alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextMinute, tickIntent(context));
            }
        } catch (SecurityException denied) {
            alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextMinute, tickIntent(context));
        }
    }
}
