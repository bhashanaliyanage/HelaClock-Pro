package lk.sanoj.helaclok.pro.HelaClockPro;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerView;

public class CPicker extends Activity {
    ColorPickerView colorPickerView;
    ColorObserver colorObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_picker);

        colorPickerView = findViewById(R.id.colorPicker);

        colorPickerView.subscribe(colorObserver);
    }
}
