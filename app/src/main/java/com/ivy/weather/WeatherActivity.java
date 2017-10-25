package com.ivy.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WeatherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
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
