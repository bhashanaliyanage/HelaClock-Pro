package lk.sanoj.helaclok.pro.HelaClockPro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import lk.sanoj.helaclok.pro.HelaClockPro.Controller.SinhalaTimeConverter;
import lk.sanoj.helaclok.pro.HelaClockPro.Models.Clock;

public class section extends AppCompatActivity {
    // private RemoteViews views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);

        Button btn1 = findViewById(R.id.button);
        Button btn2 = findViewById(R.id.button3);

        final TextView textMeridiem = findViewById(R.id.textAM_PM);
        final TextView textHour = findViewById(R.id.textHour);
        final TextView textMinutes = findViewById(R.id.textMinutes);
        final ImageView hintImage = findViewById(R.id.hintImage);

        final Clock clock = new Clock();
        SinhalaTimeConverter converter = new SinhalaTimeConverter(clock.getTime());

        textHour.setText(converter.getHour());
        textMinutes.setText(converter.getMinutes());
        textMeridiem.setText(converter.getMeridiem());

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    clock.updateTime();
                                    // clock.setTime("11:30:AM");
                                    SinhalaTimeConverter converter = new SinhalaTimeConverter(clock.getTime());

                                    Log.d("Meridiem", clock.getTime().getMeridiem());

                                    textHour.setText(converter.getHour());
                                    textMinutes.setText(converter.getMinutes());
                                    textMeridiem.setText(converter.getMeridiem());
                                    if (clock.getTime().getMeridiem().equals("PM")) {
                                        hintImage.setImageResource(R.drawable.night);
                                    } else {
                                        hintImage.setImageResource(R.drawable.morningimg);
                                    }
                                } catch (Exception e) {
                                    Log.e("Error in section.java", Objects.requireNonNull(e.getMessage()));
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.e("Error in section.java", Objects.requireNonNull(e.getMessage()));
                }
            }
        };

        t.start();

        // TODO: Make the hint image changeable.
        // TODO: Enable disable the hint image according to user preferences

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(section.this, MainActivity.class);
                section.this.startActivity(mainIntent);

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(section.this, widsetting.class);
                section.this.startActivity(mainIntent);
            }
        });


    }
}
