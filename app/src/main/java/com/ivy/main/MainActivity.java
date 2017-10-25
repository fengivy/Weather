package com.ivy.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivy.bomb.BombActivity;
import com.ivy.ruler.RulerActivity;
import com.ivy.weather.R;
import com.ivy.weather.WeatherActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivy on 2017/10/25.
 * Description：
 */

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv;
    private List<ViewItem> mData=new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addActivity();
        rv= (RecyclerView) this.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new MyAdapter());
        rv.addOnItemTouchListener(new IvyOnTouchListener(this) {
            @Override
            protected boolean onEmptyClick(MotionEvent e) {
                return false;
            }

            @Override
            public void onItemClick(int position, View childView) {
                Intent intent=new Intent(MainActivity.this,mData.get(position).getClazz());
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void addActivity() {
        mData.clear();
        mData.add(new ViewItem("天气小太阳", WeatherActivity.class));
        mData.add(new ViewItem("薄荷卷尺", RulerActivity.class));
        mData.add(new ViewItem("萌萌炸弹", BombActivity.class));
    }

    private class MyAdapter extends RecyclerView.Adapter<ViewHolder>{
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.adapter_main,parent,false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tv.setText(mData.get(position).getText());
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv;
        public ViewHolder(View itemView) {
            super(itemView);
            tv= (TextView) itemView.findViewById(R.id.tv);
        }
    }
}
