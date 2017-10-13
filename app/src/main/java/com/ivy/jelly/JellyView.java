package com.ivy.jelly;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ivy on 2017/9/19.
 * Description：
 */

public class JellyView extends View {
    private Paint mPaint=new Paint();
    private Path mPath=new Path();
    private float centerX,centerY;
    private float width, height;
    public JellyView(Context context) {
        super(context);
        init();
    }

    public JellyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JellyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public JellyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        centerX=getMeasuredWidth()/2;
        centerY=getMeasuredHeight()/2;
        width=getMeasuredWidth();
        height =getMeasuredHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX=w/2;
        centerY=h/2;
        width=w;
        height=h;
    }

    public void init(){
        setBackgroundColor(Color.parseColor("#12092c"));
        mPaint.setColor(Color.parseColor("#00bbff"));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHand(canvas);
    }

    private void drawHand(Canvas canvas) {
        mPath.reset();
        /*mPath.moveTo(centerX-getMeasuredWidth()/10,centerY);
        mPath.quadTo(centerX,centerY-getMeasuredWidth()/10,centerX+getMeasuredWidth()/10,centerY);*/
        mPath.moveTo(centerX,centerY);
        mPath.quadTo(centerX-width/20,centerY-width/20,centerX-width/18,centerY-width/20);
        mPath.close();
        canvas.drawPath(mPath,mPaint);
    }
}
