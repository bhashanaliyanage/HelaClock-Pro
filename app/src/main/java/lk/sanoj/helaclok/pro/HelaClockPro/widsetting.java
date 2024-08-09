package lk.sanoj.helaclok.pro.HelaClockPro;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import ahmadrosid.com.lib.CustomTextView;
import top.defaults.colorpicker.ColorPickerView;
import top.defaults.colorpickerr.ColorPickerPopup;


public class widsetting extends Activity {

    private Button colotchange;
    private TextView codeview;
    private ImageView show;
    private CustomTextView textView4;
    private CustomTextView textView5;
    private CustomTextView textView6;
    private Button backgraund, clearsetting, ynoty;
    private TextView backcode;
    private ImageView backimage;
    private Context context;
    private int bcolour;
    private int textc;
    private Switch onoff;
    private ImageView hintview;
    private int mColor = 0xFFFFFF;
    private boolean mHexValueEnable = true;
    private Dialog dialog;
    private Button btnDialogPick;
    private ColorPickerView colorPickerView;
    private EditText colorHex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_widsetting);

        colotchange = findViewById(R.id.textcolour);
        codeview = findViewById(R.id.codeview);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);
        show = findViewById(R.id.imageView);
        backgraund = findViewById(R.id.backgraund);
        backcode = findViewById(R.id.back);
        backimage = findViewById(R.id.backcolourimag);
        onoff = findViewById(R.id.switch1);
        hintview = findViewById(R.id.imageView5);
        clearsetting = findViewById(R.id.reset);
        ynoty = findViewById(R.id.clnoty);

        dialog = new Dialog(widsetting.this);
        dialog.setContentView(R.layout.color_picker);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        }
        dialog.setCancelable(true);

        btnDialogPick = dialog.findViewById(R.id.btnPick);
        colorPickerView = dialog.findViewById(R.id.colorPicker);
        colorHex = dialog.findViewById(R.id.colorHex);

        btnDialogPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int color = colorPickerView.getColor();
                String hexColor = String.format("#%06X", (0xFFFFFF & color));
                codeview.setText(hexColor);
                textView4.setTextColor(color);
                textView5.setTextColor(color);
                textView6.setTextColor(color);
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                editor.putString("colour", hexColor);
                editor.putInt("textc", color);
                editor.apply();
                System.out.println("Color: " + hexColor);
                colorHex.setText(hexColor);
                dialog.dismiss();
            }
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
                String hexColor = editable.toString().trim();;

                // Validate the hex color
                if (isValidHexColor(hexColor)) {
                    // Convert the hex color to int
                    int color = Color.parseColor(hexColor);

                    // Set the initial color in the ColorPickerView
                    colorPickerView.setInitialColor(color);
                } else {
                    // Handle the case where the hex color is not valid
                    // You can display an error message or take any other appropriate action
                    Toast.makeText(widsetting.this, "Invalid hex color: " + hexColor, Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            SharedPreferences prefs = this.getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
            String colour = prefs.getString("colour", null);//codeview
            textc = prefs.getInt("textc", 0);//maintext colour

            String colourb2 = prefs.getString("b2colour", null);//backgrund colou code text
            int hint = prefs.getInt("hint", 0);//hint
            bcolour = prefs.getInt("bcolour", 0);//backgraund colour


            if (colour == null) {
                codeview.setText("#FFFFFF");
            } else {
                codeview.setText(colour);
            }
            if (colourb2 == null) {
                backcode.setText("#74000000");
            } else {
                backcode.setText(colourb2);
            }
            if (bcolour == 0) {
                backimage.setBackgroundColor(Color.parseColor("#74000000"));
            } else {
                backimage.setBackgroundColor(bcolour);
            }
            if (textc == 0) {
                textView4.setTextColor(Color.WHITE);
                textView5.setTextColor(Color.WHITE);
                textView6.setTextColor(Color.WHITE);
            } else {
                textView4.setTextColor(textc);
                textView5.setTextColor(textc);
                textView6.setTextColor(textc);
            }
            if (hint == 0) {
                hintview.setVisibility(View.VISIBLE);
                onoff.setChecked(false);
                onoff.setText("Hint ON");
            } else if (hint == 1) {
                hintview.setVisibility(View.INVISIBLE);
                onoff.setChecked(true);
                onoff.setText("Hint OFF");

            }
        } catch (Exception ignored) {


        }


        backgraund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new ColorPickerPopup.Builder(widsetting.this)
                        .initialColor(Color.RED) // Set initial color
                        .enableBrightness(true) // Enable brightness slider or not
                        .enableAlpha(true) // Enable alpha slider or not
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {

                                String hexColor = String.format("#%06X", (0xFFFFFF & color));
                                backcode.setText(hexColor);
                                backimage.setBackgroundColor(color);
                                SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                                editor.putInt("bcolour", color);
                                editor.putString("b2colour", hexColor);
                                editor.apply();


                            }

                        });


            }
        });

        colotchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(widsetting.this, CPicker.class);
                widsetting.this.startActivity(intent);*/
                dialog.show();

                /*new ColorPickerPopup.Builder(widsetting.this)
                        .initialColor(Color.RED) // Set initial color
                        .enableBrightness(true) // Enable brightness slider or not
                        .enableAlpha(true) // Enable alpha slider or not
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show( new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                               String hexColor = String.format("#%06X", (0xFFFFFF & color));
                               codeview.setText(hexColor);
                               textView4.setTextColor(color);
                               textView5.setTextColor(color);
                               textView6.setTextColor(color);
                               SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                               editor.putString("colour", hexColor);
                               editor.putInt("textc", color);
                               editor.apply();
                            }

                        });*/


            }
        });


        clearsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                try {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } catch (Exception e) {

                }
            }
        });

        ynoty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

                try {

                    intent.putExtra("app_package", getPackageName());
                    intent.putExtra("app_uid", getApplicationInfo().uid);
                    startActivity(intent);

                } catch (Exception goo) {
                    intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
                    startActivity(intent);
                }


            }
        });


        onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (onoff.isChecked()) {
                    onoff.setText("Hint OFF");
                    hintview.setVisibility(View.INVISIBLE);
                    SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                    editor.putInt("hint", 1);
                    editor.apply();
                } else {
                    onoff.setText("Hint ON");
                    hintview.setVisibility(View.VISIBLE);
                    SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                    editor.putInt("hint", 0);
                    editor.apply();
                }
            }
        });


    }

    private boolean isValidHexColor(String hexColor) {
        return hexColor.matches("#([A-Fa-f0-9]{6})");
    }


}