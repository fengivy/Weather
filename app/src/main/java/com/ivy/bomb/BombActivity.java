package com.ivy.bomb;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ivy.weather.R;

/**
 * Created by ivy on 2017/10/25.
 * Description：
 */

public class BombActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bomb);
        final BombView bombView= (BombView) this.findViewById(R.id.bomb_view);
        bombView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bombView.startAnim();
            }
        });

    }
}
