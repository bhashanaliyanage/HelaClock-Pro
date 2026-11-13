package lk.sanoj.helaclok.pro.HelaClockPro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import lk.sanoj.helaclok.pro.HelaClockPro.Models.Clock;
import lk.sanoj.helaclok.pro.HelaClockPro.Models.Time;

/** Shared content renderer for the home-screen widget and its in-app previews. */
final class ClockWidgetRenderer {
    static final int REFERENCE_WIDTH_DP = 358;
    static final int REFERENCE_HEIGHT_DP = 196;
    private static final int LAYOUT_HEIGHT_DP = 220;
    private static final int HINT_WIDTH_DP = 76;
    private static final int MAX_BITMAP_PIXELS = 1_000_000;
    private static String cachedKey;
    private static Bitmap cachedComposition;
    private static final String[] HOUR_TEXTS = {
            "දොළහ පසුවී", "එක පසුවී", "දෙක පසුවී", "තුන පසුවී", "හතර පසුවී",
            "පහ පසුවී", "හය පසුවී", "හත පසුවී", "අට පසුවී", "නවය පසුවී",
            "දහය පසුවී", "එකොළහ පසුවී", "දොළහ පසුවී"
    };

    private static final String[] MINUTE_TEXTS = {
            "තත්පර කීපයක් පමනයි", "විනාඩි එකයි", "විනාඩි දෙකයි", "විනාඩි තුනයි", "විනාඩි හතරයි",
            "විනාඩි පහයි", "විනාඩි හයයි", "විනාඩි හතයි", "විනාඩි අටයි", "විනාඩි නවයයි",
            "විනාඩි දහයයි", "විනාඩි එකොළහයි", "විනාඩි දොළහයි", "විනාඩි දහ තුනයි", "විනාඩි දහ හතරයි",
            "විනාඩි පහ ලොවයි", "විනාඩි දහ සයයි", "විනාඩි දහ හතයි", "විනාඩි දහ අටයි", "විනාඩි දහ නවයයි",
            "විනාඩි විස්සයි", "විනාඩි විසීඑකයි", "විනාඩි විසිදෙකයි", "විනාඩි විසිතුනයී", "විනාඩි විසිහතරයි",
            "විනාඩි විසිපහයි", "විනාඩි විසිහයයි", "විනාඩි විසිහතයි", "විනාඩි විසිඅටයි", "විනාඩි විසිනවයයි",
            "විනාඩි තිහයි", "විනාඩි තිස්එකයි", "විනාඩි තිස්දෙකයි", "විනාඩි තිස්තුනයි", "විනාඩි තිස්හතරයි",
            "විනාඩි තිස්පහයි", "විනාඩි තිස්හයයි", "විනාඩි තිස්හතයි", "විනාඩි තිස්අටයි", "විනාඩි තිස්නවයයි",
            "විනාඩි හතලිහයි", "විනාඩි හතලිස් එකයි", "විනාඩි හතලිස් දෙකයි", "විනාඩි හතලිස් තුනයි", "විනාඩි හතලිස් හතරයි",
            "විනාඩි හතලිස් පහයි", "විනාඩි හතලිස් හයයි", "විනාඩි හතලිස් හතයි", "විනාඩි හතලිස් අටයි", "විනාඩි හතලිස් නවයය",
            "විනාඩි පනහයි", "විනාඩි පනස් එකයි", "විනාඩි පනස් දෙකයි", "විනාඩි පනස් තුනයි", "විනාඩි පනස් හතරයි",
            "විනාඩි පනස් පහයි", "විනාඩි පනස් හයයි", "විනාඩි පනස් හතයි", "විනාඩි පනස් අටයි", "විනාඩි පනස් නවයයි"
    };

    private final Context context;
    private final int layoutId;
    private Bitmap meridiemBitmap;
    private Bitmap hourBitmap;
    private Bitmap minuteBitmap;
    private Bitmap statusBitmap;
    private final Time time;
    private final int color;
    private final String meridiemText;
    private final String hourText;
    private final String minuteText;
    private final boolean showHint;
    private final String contentDescription;

    private ClockWidgetRenderer(Context context) {
        this(context, new Clock().getTime(),
                context.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE).getInt("hint", 0) != 1,
                context.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE).getInt("textc", 0));
    }

    ClockWidgetRenderer(Context context, Time time, boolean showHint, int configuredColor) {
        this.context = context;
        this.time = time;
        this.showHint = showHint;
        layoutId = showHint ? R.layout.hellowidget_layout : R.layout.withouthint;
        color = configuredColor != 0 ? configuredColor : Color.WHITE;

        String meridiem = time.getMeridiem();
        meridiemText = "AM".equalsIgnoreCase(meridiem) ? "වේලාව පෙරවරු" : "වේලාව පස්වරු";
        int hour = Integer.parseInt(time.getHours()) % 12;
        int minute = Integer.parseInt(time.getMinutes());
        hourText = HOUR_TEXTS[hour];
        minuteText = MINUTE_TEXTS[minute];
        contentDescription = meridiemText + " " + hourText + " " + minuteText;
    }

    static RemoteViews createRemoteViews(Context context) {
        ClockWidgetRenderer renderer = new ClockWidgetRenderer(context);
        RemoteViews views = new RemoteViews(renderer.context.getPackageName(), R.layout.widget_canvas);
        views.setImageViewBitmap(R.id.widget_image, renderer.renderComposition());
        views.setContentDescription(R.id.widget_image, renderer.contentDescription);
        return views;
    }

    static void renderPreview(Context context, FrameLayout container) {
        ClockWidgetRenderer renderer = new ClockWidgetRenderer(context);
        if (container.getChildCount() == 0 || !(container.getChildAt(0) instanceof ImageView)) {
            container.removeAllViews();
            ImageView image = new ImageView(context);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            container.addView(image, new FrameLayout.LayoutParams(1, 1, Gravity.CENTER));
        }
        ImageView image = (ImageView) container.getChildAt(0);
        float density = context.getResources().getDisplayMetrics().density;
        int referenceWidth = Math.round(REFERENCE_WIDTH_DP * density);
        int referenceHeight = Math.round(REFERENCE_HEIGHT_DP * density);
        int availableWidth = container.getWidth() > 0 ? container.getWidth()
                : context.getResources().getDisplayMetrics().widthPixels;
        int availableHeight = container.getHeight() > 0 ? container.getHeight()
                : Math.round(LAYOUT_HEIGHT_DP * density);
        float scale = Math.min(1f, Math.min((float) availableWidth / referenceWidth,
                (float) availableHeight / referenceHeight));
        int width = Math.max(1, Math.round(referenceWidth * scale));
        int height = Math.max(1, Math.round(referenceHeight * scale));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) image.getLayoutParams();
        if (params.width != width || params.height != height) {
            params.width = width;
            params.height = height;
            image.setLayoutParams(params);
        }
        image.setImageBitmap(renderer.renderComposition());
        image.setContentDescription(renderer.contentDescription);
    }

    Bitmap renderComposition() {
        String key = time.getHours() + ":" + time.getMinutes() + ":" + time.getMeridiem()
                + ":" + showHint + ":" + color + ":" + context.getResources().getDisplayMetrics().densityDpi
                + ":" + context.getResources().getDisplayMetrics().widthPixels
                + ":" + context.getResources().getDisplayMetrics().heightPixels
                + ":" + context.getResources().getConfiguration().getLayoutDirection();
        synchronized (ClockWidgetRenderer.class) {
            if (key.equals(cachedKey) && cachedComposition != null && !cachedComposition.isRecycled()) {
                return cachedComposition;
            }
            Bitmap bitmap = drawComposition();
            cachedKey = key;
            cachedComposition = bitmap;
            return bitmap;
        }
    }

    private Bitmap drawComposition() {
        float density = context.getResources().getDisplayMetrics().density;
        int referenceWidth = Math.max(1, Math.round(REFERENCE_WIDTH_DP * density));
        int referenceHeight = Math.max(1, Math.round(REFERENCE_HEIGHT_DP * density));
        meridiemBitmap = textAsBitmap(meridiemText, 220, false, color);
        hourBitmap = textAsBitmap(hourText, 195, true, color);
        minuteBitmap = textAsBitmap(minuteText, 195, true, color);
        statusBitmap = showHint ? hintBitmap(time, color) : null;

        FrameLayout host = new FrameLayout(context);
        host.setClipChildren(true);
        View widgetLayout = LayoutInflater.from(context).inflate(layoutId, host, false);
        if (showHint) {
            ImageView status = widgetLayout.findViewById(R.id.status);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) status.getLayoutParams();
            params.width = Math.round(HINT_WIDTH_DP * density);
            status.setLayoutParams(params);
        }
        apply(widgetLayout);
        host.addView(widgetLayout, new FrameLayout.LayoutParams(referenceWidth,
                Math.round(LAYOUT_HEIGHT_DP * density), Gravity.CENTER));
        host.measure(View.MeasureSpec.makeMeasureSpec(referenceWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(referenceHeight, View.MeasureSpec.EXACTLY));
        host.layout(0, 0, referenceWidth, referenceHeight);

        long displayPixels = (long) context.getResources().getDisplayMetrics().widthPixels
                * context.getResources().getDisplayMetrics().heightPixels;
        long maxPixels = Math.max(1L, Math.min(MAX_BITMAP_PIXELS, displayPixels / 4));
        float scale = (float) Math.min(1d, Math.sqrt((double) maxPixels
                / ((long) referenceWidth * referenceHeight)));
        int width = Math.max(1, (int) (referenceWidth * scale));
        int height = Math.max(1, (int) (referenceHeight * scale));
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale((float) width / referenceWidth, (float) height / referenceHeight);
        host.draw(canvas);
        return bitmap;
    }

    private void apply(View root) {
        ((ImageView) root.findViewById(R.id.AMPM)).setImageBitmap(meridiemBitmap);
        ((ImageView) root.findViewById(R.id.hou)).setImageBitmap(hourBitmap);
        ((ImageView) root.findViewById(R.id.mini)).setImageBitmap(minuteBitmap);
        ImageView status = root.findViewById(R.id.status);
        status.setVisibility(showHint ? View.VISIBLE : View.INVISIBLE);
        if (showHint && statusBitmap != null) {
            status.setImageBitmap(statusBitmap);
        }
    }

    static int hintResourceFor(Time time) {
        String hours = time.getHours();
        int hour = Integer.parseInt(hours);
        boolean isPm = "PM".equalsIgnoreCase(time.getMeridiem());
        if (!isPm) {
            return hour <= 5 || hour == 12 ? R.drawable.night : R.drawable.morningimg;
        }
        return hour <= 7 || hour == 12 ? R.drawable.after : R.drawable.night;
    }

    private Bitmap hintBitmap(Time time, int color) {
        Drawable drawable = ContextCompat.getDrawable(context, hintResourceFor(time));
        if (drawable == null) {
            return null;
        }

        float density = context.getResources().getDisplayMetrics().density;
        int width = Math.round(129 * density);
        int height = Math.round(220 * density);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        float scale = Math.min((float) width / drawable.getIntrinsicWidth(),
                (float) height / drawable.getIntrinsicHeight());
        int imageWidth = Math.round(drawable.getIntrinsicWidth() * scale);
        int imageHeight = Math.round(drawable.getIntrinsicHeight() * scale);
        int left = (width - imageWidth) / 2;
        int top = (height - imageHeight) / 2;

        drawable.setBounds(left, top, left + imageWidth, top + imageHeight);
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        drawable.draw(new Canvas(bitmap));
        return bitmap;
    }

    private Bitmap textAsBitmap(String text, float fontSize, boolean antiAlias, int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(fontSize);
        paint.setTypeface(Typeface.createFromAsset(context.getAssets(), "emanee.ttf"));
        paint.setColor(color);
        paint.setTextAlign(Paint.Align.LEFT);
        if (antiAlias) {
            paint.setAntiAlias(true);
            paint.setSubpixelText(true);
        }

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        Bitmap bitmap = Bitmap.createBitmap(Math.max(1, bounds.width()),
                Math.max(1, bounds.height()), Bitmap.Config.ARGB_8888);
        new Canvas(bitmap).drawText(text, -bounds.left, -bounds.top, paint);
        return bitmap;
    }

}
