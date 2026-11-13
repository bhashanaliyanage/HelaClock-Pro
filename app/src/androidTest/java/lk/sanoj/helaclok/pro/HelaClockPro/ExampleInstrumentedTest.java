package lk.sanoj.helaclok.pro.HelaClockPro;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test public void useAppContext() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("lk.sanoj.helaclok.pro.HelaClockPro", appContext.getPackageName());
    }
}
