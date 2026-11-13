package lk.sanoj.helaclok.pro.HelaClockPro

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals

@RunWith(AndroidJUnit4::class)
class AppUiTest {
    @get:Rule val compose = createAndroidComposeRule<Welcom>()

    @Test fun opensWidgetSettings() {
        compose.onNodeWithText("Settings").performClick()
        compose.onNodeWithText("Clock color").assertExists()
        compose.onNodeWithText("Show hint image").assertExists()
    }

    @Test fun opensDeskAppearanceCarousel() {
        compose.onNodeWithTag("background-selector-toggle").performClick()
        compose.onNodeWithText("Choose background").assertExists()
        compose.onNodeWithText("Cancel").assertExists()
        compose.onNodeWithText("Apply").assertExists()
    }

    @Test fun carouselOnlySavesSelectionAfterApply() {
        val prefs = compose.activity.getSharedPreferences("MyPrefsFile", android.content.Context.MODE_PRIVATE)
        val original = prefs.getString("ThemeName", null)
        val blank = "AssXSDrrfgssdbh"
        try {
            compose.runOnUiThread { prefs.edit().putString("ThemeName", blank).commit() }
            compose.waitForIdle()
            compose.onNodeWithTag("background-selector-toggle").performClick()
            compose.onNodeWithTag("desk-background-carousel").performScrollTo().performTouchInput { swipeLeft() }
            compose.waitForIdle()
            assertEquals(blank, prefs.getString("ThemeName", null))
            compose.onNodeWithText("Cancel").performScrollTo().performClick()
            assertEquals(blank, prefs.getString("ThemeName", null))

            compose.onNodeWithTag("background-selector-toggle").performClick()
            compose.onNodeWithTag("desk-background-carousel").performScrollTo().performTouchInput { swipeLeft() }
            compose.onNodeWithText("Apply").performScrollTo().performClick()
            assertNotEquals(blank, prefs.getString("ThemeName", null))
        } finally {
            compose.runOnUiThread {
                val editor = prefs.edit()
                if (original == null) editor.remove("ThemeName") else editor.putString("ThemeName", original)
                editor.commit()
            }
        }
    }
}
