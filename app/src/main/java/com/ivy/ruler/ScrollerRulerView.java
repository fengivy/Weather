package com.ivy.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import java.text.DecimalFormat;


/**
 * Created by ivy on 2017/10/13.
 * Description：
 */

public class ScrollerRulerView extends View{
    private Paint mPaint;
    //刻度宽度，用于控制显示刻度的多少
    private int scaleWidth,scaleNum;
    //刻度高度
    private int minScaleHeight, maxScaleHeight ,selectScaleHeight;
    //颜色
    private int scaleColor,selectScaleColor,rulerColor;
    //刻度线的粗细
    private int scaleVerLineStroke, scaleHorLineStroke, ScaleHorLineEntireStroke, scaleHorLineSelectStroke;
    private int beginX=0;//滑动距离
    //刻度文字颜色
    private int scaleTextColor;
    //各个文字大小
    private float scaleTextSize,currentValueTextSize,currentValueUnitTextSize;
    //单位
    private String unit="KG";
    private GestureDetector mGestureDetector=new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            beginX-=distanceX;
            invalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityX)> ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity()){
                mScroller.fling(beginX,0, (int) velocityX,0,beginX-getMeasuredWidth()*10,beginX+getMeasuredWidth()*10,0,0);
                invalidate();
                return true;
            }else{
                return false;
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

    });

    public ScrollerRulerView(Context context) {
        super(context);
        init();
    }

    public ScrollerRulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollerRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScrollerRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int width=getMeasuredWidth();
        int height=getMeasuredHeight();
        int minWidth= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,120,getContext().getResources().getDisplayMetrics());
        int minHeight= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,80,getContext().getResources().getDisplayMetrics());
        setMeasuredDimension(Math.max(minWidth,width),Math.max(minHeight,height));
    }

    private void init() {
        mScroller=new Scroller(getContext(),new LinearInterpolator());
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        scaleTextSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,16,getContext().getResources().getDisplayMetrics());
        currentValueTextSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,30,getContext().getResources().getDisplayMetrics());
        currentValueUnitTextSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,12,getContext().getResources().getDisplayMetrics());
        scaleVerLineStroke =2;
        scaleHorLineStroke =4;
        ScaleHorLineEntireStroke =6;
        scaleHorLineSelectStroke=8;
        rulerColor= Color.parseColor("#eeeeee");
        scaleColor= Color.parseColor("#aaaaaa");
        selectScaleColor=Color.parseColor("#00bbff");
        scaleTextColor=Color.parseColor("#000000");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        scaleWidth =w/40;
        selectScaleHeight = getMeasuredHeight()/4;
        maxScaleHeight =selectScaleHeight/8*7;
        minScaleHeight = (int) (selectScaleHeight/8*3.5f);
        scaleNum = w/scaleWidth/2+1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
        drawRuler(canvas);
    }

    private void drawRuler(Canvas canvas) {
        mPaint.setTextSize(scaleTextSize);
        //背景
        mPaint.setColor(rulerColor);
        canvas.drawRect(0,getMeasuredHeight()/2,getMeasuredWidth(),getMeasuredHeight(),mPaint);
        //横线
        mPaint.setColor(scaleColor);
        mPaint.setStrokeWidth(scaleVerLineStroke);
        canvas.drawLine(0,getMeasuredHeight()/2,getMeasuredWidth(),getMeasuredHeight()/2,mPaint);
        //刻度和刻度文字
        int afterX=getMeasuredWidth()/2+scaleWidth+beginX%scaleWidth;
        int afterValue=-beginX/scaleWidth+1;
        int previousX=getMeasuredWidth()/2+beginX%scaleWidth;
        int previousValue=-beginX/scaleWidth;
        for(int i=0;i<scaleNum;i++){
            mPaint.setColor(scaleColor);
            if (afterValue%10==0){
                canvas.drawLine(afterX+i*scaleWidth,getMeasuredHeight()/2,afterX+i*scaleWidth,getMeasuredHeight()/2+maxScaleHeight,mPaint);
                mPaint.setColor(scaleTextColor);
                canvas.drawText(afterValue/10+"",afterX+i*scaleWidth-mPaint.measureText(afterValue/10+"")/2,getMeasuredHeight()/8*7-(mPaint.getFontMetrics().ascent+mPaint.getFontMetrics().descent)/2,mPaint);
            }else{
                canvas.drawLine(afterX+i*scaleWidth,getMeasuredHeight()/2,afterX+i*scaleWidth,getMeasuredHeight()/2+minScaleHeight,mPaint);
            }
            mPaint.setColor(scaleColor);
            if (previousValue%10==0){
                canvas.drawLine(previousX-i*scaleWidth,getMeasuredHeight()/2,previousX-i*scaleWidth,getMeasuredHeight()/2+maxScaleHeight,mPaint);
                mPaint.setColor(scaleTextColor);
                canvas.drawText(previousValue/10+"",previousX-i*scaleWidth-mPaint.measureText(previousValue/10+"")/2,getMeasuredHeight()/8*7-(mPaint.getFontMetrics().ascent+mPaint.getFontMetrics().descent)/2,mPaint);
            }else{
                canvas.drawLine(previousX-i*scaleWidth,getMeasuredHeight()/2,previousX-i*scaleWidth,getMeasuredHeight()/2+minScaleHeight,mPaint);
            }
            afterValue++;
            previousValue--;
        }
        //中间刻度
        mPaint.setStrokeWidth(scaleHorLineSelectStroke);
        mPaint.setColor(selectScaleColor);
        canvas.drawLine(getMeasuredWidth()/2,getMeasuredHeight()/2,getMeasuredWidth()/2,getMeasuredHeight()/2+ selectScaleHeight,mPaint);
    }

    private DecimalFormat mDecimalFormat=new DecimalFormat("0.0");
    private void drawText(Canvas canvas) {
        mPaint.setColor(selectScaleColor);
        String text=mDecimalFormat.format(-beginX*1.0f/scaleWidth/10);
        mPaint.setTextSize(currentValueTextSize);
        float width=mPaint.measureText(text);
        float ascent=mPaint.getFontMetrics().ascent;
        float top=getMeasuredHeight()/4-(mPaint.getFontMetrics().ascent+mPaint.getFontMetrics().descent)/2;
        canvas.drawText(text,getMeasuredWidth()/2-width/2,top,mPaint);
        mPaint.setTextSize(currentValueUnitTextSize);
        canvas.drawText(unit,getMeasuredWidth()/2+width/2+mPaint.measureText(unit)/2,top-(mPaint.getFontMetrics().ascent-ascent),mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getY()<getMeasuredHeight()/2) {
            if (mScroller.isFinished())
                adjustLocation();
            return false;
        }
        if (mGestureDetector.onTouchEvent(event)){
            return true;
        }
        if (event.getAction()==MotionEvent.ACTION_UP||event.getAction()==MotionEvent.ACTION_CANCEL){
            adjustLocation();
        }
        return true;
    }

    private Scroller mScroller;

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()){
            beginX=mScroller.getCurrX();
            invalidate();
            if (mScroller.getCurrX()==mScroller.getFinalX()){
                adjustLocation();
            }
        }
    }

    private void adjustLocation(){
        if (beginX % scaleWidth == 0)
            return;
        //判断是左移还是右移
        int dx = Math.abs(beginX) % scaleWidth > scaleWidth / 2 ? (scaleWidth - Math.abs(beginX) % scaleWidth) * (beginX / Math.abs(beginX))
                : Math.abs(beginX) % scaleWidth * (-beginX / Math.abs(beginX));
        mScroller.startScroll(beginX, 0, dx, 0, 200);
        invalidate();
    }
}
