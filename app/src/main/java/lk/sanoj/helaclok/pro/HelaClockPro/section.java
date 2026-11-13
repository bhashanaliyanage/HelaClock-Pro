package lk.sanoj.helaclok.pro.HelaClockPro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;


public class section extends AppCompatActivity {
    // private RemoteViews views;
    private Dialog dialog;
    private CheckBox cbBlank;
    private CheckBox cbPattern;
    private CheckBox cbGrunge;
    private CheckBox cbRock;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private FrameLayout widgetPreviewContainer;
    private final Runnable previewUpdateTask = new Runnable() {
        @Override
        public void run() {
            try {
                ClockWidgetRenderer.renderPreview(section.this, widgetPreviewContainer);
            } catch (Exception e) {
                Log.e("section", "Unable to render widget preview", e);
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);

        Button btn2 = findViewById(R.id.button3);

        widgetPreviewContainer = findViewById(R.id.widgetPreviewContainer);
        handler.post(previewUpdateTask);

        btn2.setOnClickListener(v -> {
            Intent mainIntent = new Intent(section.this, WidgetSetting.class);
            section.this.startActivity(mainIntent);
        });

        createDialog();
    }

    private void createDialog() {
        dialog = new Dialog(section.this);
        dialog.setContentView(R.layout.clock_settings);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        }
        dialog.setCancelable(true);

        // Set CheckBoxes
        cbBlank = dialog.findViewById(R.id.checkBoxBlank);
        cbPattern = dialog.findViewById(R.id.checkBoxPattern);
        cbGrunge = dialog.findViewById(R.id.checkBoxGrunge);
        cbRock = dialog.findViewById(R.id.checkBoxRock);

        // Set User Settings
        try {
            SharedPreferences prefs = this.getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
            String themes = prefs.getString("ThemeName", null);

            if (themes != null) {
                switch (themes) {
                    case "AssXSDrrfgssdbh":
                        cbBlank.setChecked(true);
                        break;
                    case "WdhbgfhghhdwaSSDfgdfg":
                        cbPattern.setChecked(true);
                        break;
                    case "HlknsdakjKJHfdskljhs":
                        cbGrunge.setChecked(true);
                        break;
                    case "dAsdsQWdsfdSDfdsfS":
                        cbRock.setChecked(true);
                        break;
                }
            }
        } catch (Exception e) {
            SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
            editor.putString("ThemeName", "AssXSDrrfgssdbh");
            editor.apply();
            cbBlank.setChecked(true);
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(previewUpdateTask);
        super.onDestroy();
    }

    public void clockSettings(View view) {
        Log.d("Clock Settings", "Clock Settings");
        dialog.show();
    }

    public void clockSettingBlank(View v) {
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
        editor.putString("ThemeName", "AssXSDrrfgssdbh");
        editor.apply();
        cbPattern.setChecked(false);
        cbGrunge.setChecked(false);
        cbRock.setChecked(false);
    }

    public void clockSettingPattern(View v) {
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
        editor.putString("ThemeName", "WdhbgfhghhdwaSSDfgdfg");
        editor.apply();
        cbBlank.setChecked(false);
        cbGrunge.setChecked(false);
        cbRock.setChecked(false);
    }

    public void clockSettingGrunge(View v) {
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
        editor.putString("ThemeName", "HlknsdakjKJHfdskljhs");
        editor.apply();
        cbBlank.setChecked(false);
        cbPattern.setChecked(false);
        cbRock.setChecked(false);
    }

    public void clockSettingRock(View v) {
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
        editor.putString("ThemeName", "dAsdsQWdsfdSDfdsfS");
        editor.apply();
        cbBlank.setChecked(false);
        cbPattern.setChecked(false);
        cbGrunge.setChecked(false);
    }

    public void onSaveChanges(View view) {
        if (cbBlank.isChecked()) {
            SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
            editor.putString("ThemeName", "AssXSDrrfgssdbh");
            editor.apply();
            cbPattern.setChecked(false);
            cbGrunge.setChecked(false);
            cbRock.setChecked(false);
            Snackbar.make(section.this.findViewById(R.id.linearLayout), "Applied Setting: Blank", Snackbar.LENGTH_SHORT).show();
        } else if (cbPattern.isChecked()) {
            SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
            editor.putString("ThemeName", "WdhbgfhghhdwaSSDfgdfg");
            editor.apply();
            cbBlank.setChecked(false);
            cbGrunge.setChecked(false);
            cbRock.setChecked(false);
            Snackbar.make(section.this.findViewById(R.id.linearLayout), "Applied Setting: Pattern", Snackbar.LENGTH_SHORT).show();
        } else if (cbGrunge.isChecked()) {
            SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
            editor.putString("ThemeName", "HlknsdakjKJHfdskljhs");
            editor.apply();
            cbBlank.setChecked(false);
            cbPattern.setChecked(false);
            cbRock.setChecked(false);
            Snackbar.make(section.this.findViewById(R.id.linearLayout), "Applied Setting: Grunge", Snackbar.LENGTH_SHORT).show();
        } else if (cbRock.isChecked()) {
            SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
            editor.putString("ThemeName", "dAsdsQWdsfdSDfdsfS");
            editor.apply();
            cbBlank.setChecked(false);
            cbPattern.setChecked(false);
            cbGrunge.setChecked(false);
            Snackbar.make(section.this.findViewById(R.id.linearLayout), "Applied Setting: Rock Wall", Snackbar.LENGTH_SHORT).show();
        }

        dialog.dismiss();
    }

    public void checkBoxBlank(View view) {
        cbPattern.setChecked(false);
        cbGrunge.setChecked(false);
        cbRock.setChecked(false);
    }

    public void checkBoxPattern(View view) {
        cbBlank.setChecked(false);
        cbGrunge.setChecked(false);
        cbRock.setChecked(false);
    }

    public void checkBoxGrunge(View view) {
        cbBlank.setChecked(false);
        cbPattern.setChecked(false);
        cbRock.setChecked(false);
    }

    public void checkBoxRock(View view) {
        cbBlank.setChecked(false);
        cbPattern.setChecked(false);
        cbGrunge.setChecked(false);
    }

    public void startClock(View view) {
        startActivity(new Intent(section.this, clock.class));
    }
}
