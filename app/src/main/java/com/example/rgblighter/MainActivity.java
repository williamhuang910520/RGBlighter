package com.example.rgblighter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.graphics.Color;

import java.lang.reflect.Field;

import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;


public class MainActivity extends AppCompatActivity {

    SeekBar sbar_hue, sbar_sat, sbar_val, sbar_scrbri;
    float current_hue = 0f, current_sat = 1f, current_val = 1f;
    Context context;
    int brightness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context = getApplicationContext();
        if(!Settings.System.canWrite(context)){
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
        Settings.System.putInt(getContentResolver(), SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_MANUAL);

        sbar_hue = findViewById(R.id.seekBar_HUE);
        sbar_sat = findViewById(R.id.seekBar_SAT);
        sbar_val = findViewById(R.id.seekBar_VAL);
        sbar_scrbri = findViewById(R.id.seekBar_SCRBRI);
        final ConstraintLayout backround = findViewById(R.id.backround);
        backround.setBackgroundColor(Color.HSVToColor(new float[]{current_hue, current_sat, current_val}));

        sbar_scrbri.setMax(getMaxBrightness(context, 255));
        sbar_scrbri.setProgress(brightness);
        sbar_scrbri.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbar_hue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                backround.setBackgroundColor(Color.HSVToColor(new float[]{360*(i/1000f), current_sat, current_val}));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                current_hue = 360*(seekBar.getProgress()/1000f);
            }
        });

        sbar_sat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                backround.setBackgroundColor(Color.HSVToColor(new float[]{current_hue, (i/1000f), current_val}));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                current_sat = seekBar.getProgress()/1000f;
            }
        });

        sbar_val.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                backround.setBackgroundColor(Color.HSVToColor(new float[]{current_hue, current_sat, (i/1000f)}));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                current_val = seekBar.getProgress()/1000f;
            }
        });
    }

    @Override
    protected void onDestroy() {
        Settings.System.putInt(getContentResolver(), SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Settings.System.putInt(getContentResolver(), SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        super.onPause();
    }

    @Override
    protected void onResume() {
        context = getApplicationContext();
        brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
        Settings.System.putInt(getContentResolver(), SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_MANUAL);
        sbar_scrbri.setProgress(brightness);
        super.onResume();
    }

    public int getMaxBrightness(Context context, int defaultValue){

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if(powerManager != null) {
            Field[] fields = powerManager.getClass().getDeclaredFields();
            for (Field field: fields) {

                //https://android.googlesource.com/platform/frameworks/base/+/android-4.3_r2.1/core/java/android/os/PowerManager.java

                if(field.getName().equals("BRIGHTNESS_ON")) {
                    field.setAccessible(true);
                    try {
                        return (int) field.get(powerManager);
                    } catch (IllegalAccessException e) {
                        return defaultValue;
                    }
                }
            }
        }
        return defaultValue;
    }
}