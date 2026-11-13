package lk.sanoj.helaclok.pro.HelaClockPro;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import top.defaults.colorpicker.ColorPickerView;


public class WidgetSetting extends Activity {

    private FrameLayout previewContainer;
    private Button btnHint;
    private Dialog dialog;
    private ColorPickerView colorPickerView;
    private EditText colorHex;
    private boolean hintStatus;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable previewUpdateTask = new Runnable() {
        @Override
        public void run() {
            refreshPreview();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.activity_widsetting);

        Button btnTextColor = findViewById(R.id.textcolour);
        previewContainer = findViewById(R.id.widgetPreviewContainer);
        Button clearSettings = findViewById(R.id.reset);
        btnHint = findViewById(R.id.clnoty);

        // Load the saved text color for the picker.
        SharedPreferences prefs = this.getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        int textColor = prefs.getInt("textc", 0);
        String initialHexColor = String.format("#%06X", (0xFFFFFF & textColor));

        handler.post(previewUpdateTask);

        dialog = new Dialog(WidgetSetting.this);
        dialog.setContentView(R.layout.color_picker);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        }
        dialog.setCancelable(true);

        Button btnDialogPick = dialog.findViewById(R.id.btnPick);
        colorPickerView = dialog.findViewById(R.id.colorPicker);
        colorHex = dialog.findViewById(R.id.colorHex);
        colorHex.setText(initialHexColor);
        colorPickerView.setInitialColor(textColor);

        btnDialogPick.setOnClickListener(view -> {
            int color = colorPickerView.getColor();
            String hexColor = String.format("#%06X", (0xFFFFFF & color));
            // codeview.setText(hexColor);
            SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
            editor.putString("colour", hexColor);
            editor.putInt("textc", color);
            editor.apply();
            System.out.println("Color: " + hexColor);
            colorHex.setText(hexColor);
            dialog.dismiss();
            refreshPreview();
            Snackbar.make(WidgetSetting.this.findViewById(R.id.linearLayout1), "Applied Color: " + hexColor, Snackbar.LENGTH_SHORT).show();
        });

        colorHex.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String hexColor = editable.toString().trim();

                // #66e6ff

                if (hexColor.length() == 6) {
                    // Add the '#' symbol if it's missing
                    hexColor = "#" + hexColor;
                    // Validate the hex color
                    validateHexColor(hexColor);
                }

                if (hexColor.length() == 7 && hexColor.charAt(0) == '#') {
                    // Validate the hex color
                    validateHexColor(hexColor);
                }

            }
        });

        try {
            //code view
            // int textc = prefs.getInt("textc", 0);//maintext colour

            //background colour code text
            int hint = prefs.getInt("hint", 0);
            if (hint == 0) {
                btnHint.setText(R.string.turn_off_hint);
                hintStatus = true;
            } else if (hint == 1) {
                btnHint.setText(R.string.turn_on_hint);
                hintStatus = false;
            }
        } catch (Exception ignored) {
        }

        btnTextColor.setOnClickListener(view -> dialog.show());

        clearSettings.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            try {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            } catch (Exception ignored) {

            }
        });

        btnHint.setOnClickListener(v -> {
            if (hintStatus) {
                btnHint.setText(R.string.turn_on_hint);

                hintStatus = false;

                SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                editor.putInt("hint", 1);
                editor.apply();
                refreshPreview();

                Snackbar.make(WidgetSetting.this.findViewById(R.id.linearLayout1), "Hint Turned Off", Snackbar.LENGTH_SHORT).show();
            } else {
                btnHint.setText(R.string.turn_off_hint);

                hintStatus = true;

                SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                editor.putInt("hint", 0);
                editor.apply();
                refreshPreview();

                Snackbar.make(WidgetSetting.this.findViewById(R.id.linearLayout1), "Hint Turned On", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void validateHexColor(String hexColor) {
        if (isValidHexColor(hexColor)) {
            // Convert the hex color to int
            int color = Color.parseColor(hexColor);

            // Set the initial color in the ColorPickerView
            colorPickerView.setInitialColor(color);
        } else {
            // Handle the case where the hex color is not valid
            // You can display an error message or take any other appropriate action
            Toast.makeText(WidgetSetting.this, "Invalid hex color: " + hexColor, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidHexColor(String hexColor) {
        return hexColor.matches("#([A-Fa-f0-9]{6})");
    }

    private void refreshPreview() {
        if (previewContainer != null) {
            ClockWidgetRenderer.renderPreview(this, previewContainer);
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(previewUpdateTask);
        super.onDestroy();
    }

}
