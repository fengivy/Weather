package com.ivy.weather;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WeatherView weatherView = (WeatherView) this.findViewById(R.id.weather_view);
        Button button= (Button) this.findViewById(R.id.btn_start);
        weatherView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherView.startAnim();
            }
        });
    }
}
