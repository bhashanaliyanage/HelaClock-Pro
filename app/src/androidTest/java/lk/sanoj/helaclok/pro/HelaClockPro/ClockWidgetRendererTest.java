package lk.sanoj.helaclok.pro.HelaClockPro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import lk.sanoj.helaclok.pro.HelaClockPro.Models.Time;

@RunWith(AndroidJUnit4.class)
public class ClockWidgetRendererTest {
    @Test
    public void choosesHintAtTimeBoundaries() {
        assertEquals(R.drawable.night, ClockWidgetRenderer.hintResourceFor(new Time("05", "59", "AM")));
        assertEquals(R.drawable.morningimg, ClockWidgetRenderer.hintResourceFor(new Time("06", "00", "AM")));
        assertEquals(R.drawable.after, ClockWidgetRenderer.hintResourceFor(new Time("12", "00", "PM")));
        assertEquals(R.drawable.after, ClockWidgetRenderer.hintResourceFor(new Time("07", "59", "PM")));
        assertEquals(R.drawable.night, ClockWidgetRenderer.hintResourceFor(new Time("08", "00", "PM")));
        assertEquals(R.drawable.night, ClockWidgetRenderer.hintResourceFor(new Time("12", "00", "AM")));
    }

    @Test
    public void systemModeUsesAccent3Tone50WhenAvailable() {
        Context context = InstrumentationRegistry.getTargetContext();
        assertEquals(Color.WHITE, ClockWidgetRenderer.resolveColor(context,
                ClockWidgetRenderer.COLOR_MODE_WHITE, Color.RED));
        assertEquals(Color.MAGENTA, ClockWidgetRenderer.resolveColor(context,
                ClockWidgetRenderer.COLOR_MODE_CUSTOM, Color.MAGENTA));
        if (Build.VERSION.SDK_INT >= 31) {
            int colorId = context.getResources().getIdentifier(
                    "system_accent3_50", "color", "android");
            assertTrue("Android system accent resource should exist", colorId != 0);
            assertEquals(context.getResources().getColor(colorId),
                    ClockWidgetRenderer.resolveColor(context,
                            ClockWidgetRenderer.COLOR_MODE_SYSTEM, Color.RED));
        } else {
            assertEquals(Color.WHITE, ClockWidgetRenderer.resolveColor(context,
                    ClockWidgetRenderer.COLOR_MODE_SYSTEM, Color.RED));
        }
    }

    @Test
    public void existingCustomColorIsPreservedUnlessModeWasExplicitlyChosen() {
        Context base = InstrumentationRegistry.getTargetContext();
        SharedPreferences isolated = base.getSharedPreferences("widget_migration_test", Context.MODE_PRIVATE);
        Context context = new ContextWrapper(base) {
            @Override public SharedPreferences getSharedPreferences(String name, int mode) {
                return isolated;
            }
        };
        try {
            isolated.edit().clear().putInt("textc", Color.RED).commit();
            assertEquals(ClockWidgetRenderer.COLOR_MODE_CUSTOM,
                    ClockWidgetRenderer.getConfiguredColorMode(context));
            assertEquals(Color.RED, ClockWidgetRenderer.resolveConfiguredColor(context));
            isolated.edit().putInt("clock_color_mode", ClockWidgetRenderer.COLOR_MODE_WHITE).commit();
            assertEquals(Color.WHITE, ClockWidgetRenderer.resolveConfiguredColor(context));
            assertEquals(Color.RED, isolated.getInt("textc", 0));
        } finally {
            isolated.edit().clear().commit();
        }
    }

    @Test
    public void clockWordsFollowTheExistingWidgetWording() {
        String[] morning = ClockWidgetRenderer.clockWords(new Time("06", "15", "AM"));
        assertEquals("වේලාව පෙරවරු", morning[0]);
        assertEquals("හය පසුවී", morning[1]);
        assertEquals("විනාඩි පහ ලොවයි", morning[2]);
        String[] noon = ClockWidgetRenderer.clockWords(new Time("12", "00", "PM"));
        assertEquals("වේලාව පස්වරු", noon[0]);
        assertEquals("දොළහ පසුවී", noon[1]);
        assertEquals("තත්පර කීපයක් පමනයි", noon[2]);
    }

    @Test
    public void rendersBoundedCompositionsAcrossDensitiesAndSettings() {
        AtomicReference<Throwable> failure = new AtomicReference<>();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            try {
                verifyCompositions();
            } catch (Throwable error) {
                failure.set(error);
            }
        });
        if (failure.get() != null) {
            throw new AssertionError("Composition verification failed", failure.get());
        }
    }

    private void verifyCompositions() {
        Context base = InstrumentationRegistry.getTargetContext();
        Time[] times = {new Time("08", "15", "AM"), new Time("01", "30", "PM"),
                new Time("09", "45", "PM")};
        for (int dpi : new int[]{160, 320, 640}) {
            Configuration configuration = new Configuration(base.getResources().getConfiguration());
            configuration.densityDpi = dpi;
            Context context = base.createConfigurationContext(configuration);
            for (Time time : times) {
                Bitmap hint = new ClockWidgetRenderer(context, time, true, Color.RED).renderComposition();
                Bitmap noHint = new ClockWidgetRenderer(context, time, false, Color.RED).renderComposition();
                long displayPixels = (long) context.getResources().getDisplayMetrics().widthPixels
                        * context.getResources().getDisplayMetrics().heightPixels;
                long limit = Math.max(1L, Math.min(1_000_000L, displayPixels / 4));
                assertTrue((long) hint.getWidth() * hint.getHeight() <= limit);
                assertEquals((float) ClockWidgetRenderer.REFERENCE_WIDTH_DP
                                / ClockWidgetRenderer.REFERENCE_HEIGHT_DP,
                        (float) hint.getWidth() / hint.getHeight(), 0.02f);
                assertFalse(hint.sameAs(noHint));
                assertTrue(containsRed(hint));
                Bitmap white = new ClockWidgetRenderer(context, time, true, Color.WHITE).renderComposition();
                assertFalse(hint.sameAs(white));

                for (int[] size : new int[][]{{220, 100}, {600, 300}, {500, 700}}) {
                    ImageView image = new ImageView(context);
                    image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    image.setImageBitmap(hint);
                    image.measure(View.MeasureSpec.makeMeasureSpec(size[0], View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(size[1], View.MeasureSpec.EXACTLY));
                    image.layout(0, 0, size[0], size[1]);
                    RectF displayed = new RectF(0, 0, image.getDrawable().getIntrinsicWidth(),
                            image.getDrawable().getIntrinsicHeight());
                    image.getImageMatrix().mapRect(displayed);
                    assertTrue(displayed.left >= -0.5f && displayed.top >= -0.5f);
                    assertTrue(displayed.right <= size[0] + 0.5f && displayed.bottom <= size[1] + 0.5f);
                    assertEquals((float) hint.getWidth() / hint.getHeight(),
                            displayed.width() / displayed.height(), 0.02f);
                }
                hint.recycle();
                noHint.recycle();
                white.recycle();
            }
        }
    }

    private boolean containsRed(Bitmap bitmap) {
        for (int y = 0; y < bitmap.getHeight(); y += 4) {
            for (int x = 0; x < bitmap.getWidth(); x += 4) {
                int pixel = bitmap.getPixel(x, y);
                if (Color.alpha(pixel) > 200 && Color.red(pixel) > 200
                        && Color.green(pixel) < 30 && Color.blue(pixel) < 30) {
                    return true;
                }
            }
        }
        return false;
    }
}
