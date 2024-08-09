package lk.sanoj.helaclok.pro.HelaClockPro;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class ClockWidget extends AppWidgetProvider {

    AppWidgetManager appWidgetManager;
    private RemoteViews views;
    ComponentName widget;
    private int textColor01;
    private int sexy;
    private int amPM_Track;
    private int textColor02;


    public void onEnabled(Context context) {
        Log.d("ClockWithHandle", "onEnabled");

    }

    public void onReceive(final Context context, Intent intent) {


        String str = "ClockWithHandle";
        Log.d(str, "OnReceive");
        super.onReceive(context, intent);
        String action = intent.getAction();
        String sb = "Action = " +
                action;
        Log.d(str, sb);


        if (VERSION.SDK_INT >= 26) {
            context.startForegroundService(new Intent(context, TickService.class));
        } else {
            context.startService(new Intent(context, TickService.class));
        }

        assert action != null;
        if (action.equals("android.appwidget.action.APPWIDGET_UPDATE")) {

            int hintcontorl;
            try {
                SharedPreferences prefs = context.getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
                hintcontorl = prefs.getInt("hint", 0);
            } catch (Exception hint) {
                hintcontorl = 0;
            }


            if (hintcontorl == 1) {
                this.views = new RemoteViews(context.getPackageName(), R.layout.withouthint);
            } else if (hintcontorl == 0) {
                this.views = new RemoteViews(context.getPackageName(), R.layout.hellowidget_layout);
            }


            //////////////////////////////////////////////////


            String hours = new SimpleDateFormat("hh").format(Calendar.getInstance().getTime());
            String minutes = new SimpleDateFormat("mm").format(Calendar.getInstance().getTime());
            String meridiem = new SimpleDateFormat("a").format(Calendar.getInstance().getTime());

            // String clockMeridiem = new SimpleDateFormat("a").format(Calendar.getInstance().getTime());
            switch (meridiem) {
                case "AM":
                case "am":
                    amPM_Track = 1;
                    break;
                case "PM":
                case "pm":
                    amPM_Track = 2;
                    break;
            }

            String imageHours = new SimpleDateFormat("hh").format(Calendar.getInstance().getTime());

            try {

                SharedPreferences prefs = context.getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
                sexy = prefs.getInt("bcolour", 0);
            } catch (Exception ignored) {

            }

            try {
                //views.setInt(R.id.back, "setBackgroundColor", android.graphics.Color.BLACK); //(Color.parseColor(cccc));
                views.setInt(R.id.back, "setBackgroundColor", sexy);
            } catch (Exception ignored) {


            }


            ///////////////////////////////////////////////////////////////////////////////////////


            if (hintcontorl == 1) {

                //
                views.setViewVisibility(R.id.status, INVISIBLE);

            } else if (hintcontorl == 0) {
                views.setViewVisibility(R.id.status, VISIBLE);
                if (amPM_Track == 1) {
                    //views.setImageViewResource(R.id.status, R.drawable.after);

                    if (imageHours.equals("00")) {
                        views.setImageViewResource(R.id.status, R.drawable.night);
                    } else if (imageHours.equals("12")) {
                        views.setImageViewResource(R.id.status, R.drawable.night);
                    } else if (imageHours.equals("01")) {
                        views.setImageViewResource(R.id.status, R.drawable.night);
                    } else if (imageHours.equals("02")) {
                        views.setImageViewResource(R.id.status, R.drawable.night);
                    } else if (imageHours.equals("03")) {
                        views.setImageViewResource(R.id.status, R.drawable.night);
                    } else if (imageHours.equals("04")) {
                        views.setImageViewResource(R.id.status, R.drawable.night);
                    } else if (imageHours.equals("05")) {
                        views.setImageViewResource(R.id.status, R.drawable.night);
                    } else if (imageHours.equals("06")) {
                        views.setImageViewResource(R.id.status, R.drawable.morningimg);
                    } else if (imageHours.equals("07")) {
                        views.setImageViewResource(R.id.status, R.drawable.morningimg);
                    } else if (imageHours.equals("08")) {
                        views.setImageViewResource(R.id.status, R.drawable.morningimg);
                    } else if (imageHours.equals("09")) {
                        views.setImageViewResource(R.id.status, R.drawable.morningimg);
                    } else if (imageHours.equals("10")) {
                        views.setImageViewResource(R.id.status, R.drawable.morningimg);
                    } else if (imageHours.equals("11")) {
                        views.setImageViewResource(R.id.status, R.drawable.morningimg);
                    }

                }

                if (amPM_Track == 2) {
                    switch (imageHours) {
                        case "00":
                        case "12":
                        case "01":
                        case "02":
                        case "03":
                        case "04":
                        case "05":
                        case "06":
                        case "07":
                            views.setImageViewResource(R.id.status, R.drawable.after);
                            break;
                        case "08":
                        case "09":
                        case "10":
                        case "11":
                            views.setImageViewResource(R.id.status, R.drawable.night);
                            break;
                    }
                }
            }


            ////////////////////////////////////////////////////////////////////////////////////////

            ///////////////////////////////////////////////////////////////////////////////////////

            switch (meridiem) {
                case "am":
                case "AM":
                    views.setImageViewBitmap(R.id.AMPM, textAsBitmap(context, "වේලාව පෙරවරු", 220));
                    break;
                case "pm":
                case "PM":
                    views.setImageViewBitmap(R.id.AMPM, textAsBitmap(context, "වේලාව පස්වරු", 220));
                    break;
            }


            ///////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////////////////
            switch (hours) {
                case "01":
                case "13":
                    views.setImageViewBitmap(R.id.hou, textAsBitmap2(context, "එක පසුවී"));
                    break;
                case "02":
                case "14":
                    views.setImageViewBitmap(R.id.hou, textAsBitmap2(context, "දෙක පසුවී"));
                    break;
                case "03":
                case "15":
                    views.setImageViewBitmap(R.id.hou, textAsBitmap2(context, "තුන පසුවී"));
                    break;
                case "04":
                case "16":
                    views.setImageViewBitmap(R.id.hou, textAsBitmap2(context, "හතර පසුවී"));
                    break;
                case "05":
                case "17":
                    views.setImageViewBitmap(R.id.hou, textAsBitmap2(context, "පහ පසුවී"));
                    break;
                case "06":
                case "18":
                    views.setImageViewBitmap(R.id.hou, textAsBitmap2(context, "හය පසුවී"));
                    break;
                case "07":
                case "19":
                    views.setImageViewBitmap(R.id.hou, textAsBitmap2(context, "හත පසුවී"));
                    break;
                case "08":
                case "20":
                    views.setImageViewBitmap(R.id.hou, textAsBitmap2(context, "අට පසුවී"));
                    break;
                case "09":
                case "21":
                case "22":
                    views.setImageViewBitmap(R.id.hou, textAsBitmap2(context, "නවය පසුවී"));
                    break;
                case "10":
                    views.setImageViewBitmap(R.id.hou, textAsBitmap2(context, "දහය පසුවී"));
                    break;
                case "11":
                case "23":
                    views.setImageViewBitmap(R.id.hou, textAsBitmap2(context, "එකොළහ පසුවී"));
                    break;
                case "12":
                case "24":
                case "0":
                case "00":
                    views.setImageViewBitmap(R.id.hou, textAsBitmap2(context, "දොළහ පසුවී"));
                    break;
            }

            switch (minutes) {
                case "0":
                case "00":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "තත්පර කීපයක් පමනයි"));
                    break;
                case "01":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි එකයි"));
                    break;
                case "02":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි දෙකයි"));
                    break;
                case "03":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි තුනයි"));
                    break;
                case "04":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි හතරයි"));
                    break;
                case "05":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි පහයි"));
                    break;
                case "06":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි හයයි"));
                    break;
                case "07":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි හතයි"));
                    break;
                case "08":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි අටයි"));
                    break;
                case "09":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි නවයයි"));
                    break;
                case "10":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි දහයයි"));
                    break;
                case "11":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි එකොළහයි"));
                    break;
                case "12":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි දොළහයි"));
                    break;
                case "13":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි දහ තුනයි"));
                    break;
                case "14":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි දහ හතරයි"));
                    break;
                case "15":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි පහ ලොවයි"));
                    break;
                case "16":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි දහ සයයි"));
                    break;
                case "17":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි දහ හතයි"));
                    break;
                case "18":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි දහ අටයි"));
                    break;
                case "19":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි දහ නවයයි"));
                    break;
                case "20":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි විස්සයි"));
                    break;
                case "21":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි විසීඑකයි"));
                    break;
                case "22":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි විසිදෙකයි"));
                    break;
                case "23":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි විසිතුනයී"));
                    break;
                case "24":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි විසිහතරයි"));
                    break;
                case "25":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි විසිපහයි"));
                    break;
                case "26":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි විසිහයයි"));
                    break;
                case "27":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි විසිහතයි"));
                    break;
                case "28":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි විසිඅටයි"));
                    break;
                case "29":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි විසිනවයයි"));
                    break;
                case "30":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි තිහයි"));
                    break;
                case "31":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි තිස්එකයි"));
                    break;
                case "32":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි තිස්දෙකයි"));
                    break;
                case "33":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි තිස්තුනයි"));
                    break;
                case "34":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි තිස්හතරයි"));
                    break;
                case "35":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි තිස්පහයි"));
                    break;
                case "36":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි තිස්හයයි"));
                    break;
                case "37":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි තිස්හතයි"));
                    break;
                case "38":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි තිස්අටයි"));
                    break;
                case "39":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි තිස්නවයයි"));
                    break;
                case "40":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි හතලිහයි"));
                    break;
                case "41":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි හතලිස් එකයි"));
                    break;
                case "42":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි හතලිස් දෙකයි"));
                    break;
                case "43":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි හතලිස් තුනයි"));
                    break;
                case "44":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි හතලිස් හතරයි"));
                    break;
                case "45":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි හතලිස් පහයි"));
                    break;
                case "46":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි හතලිස් හයයි"));
                    break;
                case "47":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි හතලිස් හතයි"));
                    break;
                case "48":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි හතලිස් අටයි"));
                    break;
                case "49":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි හතලිස් නවයය"));
                    break;
                case "50":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි පනහයි"));
                    break;
                case "51":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි පනස් එකයි"));
                    break;
                case "52":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි පනස් දෙකයි"));
                    break;
                case "53":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි පනස් තුනයි"));
                    break;
                case "54":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි පනස් හතරයි"));
                    break;
                case "55":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි පනස් පහයි"));
                    break;
                case "56":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි පනස් හයයි"));
                    break;
                case "57":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි පනස් හතයි"));
                    break;
                case "58":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි පනස් අටයි"));
                    break;
                case "59":
                    views.setImageViewBitmap(R.id.mini, textAsBitmap2(context, "විනාඩි පනස් නවයයි"));
                    break;
            }


            Intent intents = new Intent(context, widsetting.class);
            intent.setAction("Click");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intents, 0);
            views.setOnClickPendingIntent(R.id.myRelativeLayout, pendingIntent);

            this.widget = new ComponentName(context, ClockWidget.class);
            this.appWidgetManager = AppWidgetManager.getInstance(context);
            this.appWidgetManager.updateAppWidget(this.widget, this.views);

        }
    }

    public void onDeleted(Context context, int[] iArr) {
        super.onDeleted(context, iArr);
        String sb = "onDeleted " +
                Arrays.toString(iArr);
        Log.d("ClockWithHandle", sb);
    }

    public Bitmap textAsBitmap(Context context, String time, int fontSize) {
        try {

            SharedPreferences prefs = context.getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
            textColor02 = prefs.getInt("textc", 0);

            if (textColor02 == 0) {
                textColor01 = Color.WHITE;
            } else {
                textColor01 = textColor02;
            }

        } catch (Exception e) {
            textColor01 = Color.WHITE;
        }


        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(fontSize);
        Typeface clock = Typeface.createFromAsset(context.getAssets(), "emanee.ttf");
        paint.setColor(textColor01);
        paint.setTypeface(clock);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(time) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(time, 0, baseline, paint);
        return image;
    }

    public Bitmap textAsBitmap2(Context context, String time) {

        try {

            SharedPreferences prefs = context.getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
            textColor02 = prefs.getInt("textc", 0);

            if (textColor02 == 0) {
                textColor01 = Color.WHITE;
            } else {
                textColor01 = textColor02;
            }


        } catch (Exception e) {
            textColor01 = Color.WHITE;
        }
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(195);
        Typeface clock = Typeface.createFromAsset(context.getAssets(), "emanee.ttf");
        paint.setColor(textColor01);
        paint.setTypeface(clock);
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(time) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(time, 0, baseline, paint);
        return image;
    }


    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d("ClockWithHandle", "onDisabled");
    }

}
