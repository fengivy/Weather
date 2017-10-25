package com.ivy.bomb;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import static android.R.attr.y;

/**
 * Created by ivy on 2017/10/24.
 * Description：
 */

public class BombView extends View {
    private Paint mPaint;
    private Path mPath;
    private int bombColor = Color.parseColor("#88B0E8");
    private int bombLineColor = Color.parseColor("#181D82");
    private int bombShadowColor = Color.parseColor("#77609ee6");
    private int lightColor = Color.WHITE;
    private int bombLineWidth;
    private int bodyRadius, highLightRadius;
    private DashPathEffect groundDashPathEffect,bodyDashPathEffect,highLightPathEffect,mHeadEffect;
    private RectF mRectF;
    private PathMeasure mPathMeasure=new PathMeasure();
    private float[] mPathPosition=new float[2];

    public BombView(Context context) {
        super(context);
        init();
    }

    public BombView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BombView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BombView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPath=new Path();
        mRectF=new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bombLineWidth =getMeasuredWidth()/30;
        mPaint.setStrokeWidth(bombLineWidth);
        float[] groundEffectFloat=new float[]{bombLineWidth/4,bombLineWidth/2+bombLineWidth,bombLineWidth*2,bombLineWidth/3*2+bombLineWidth,getMeasuredWidth(),0};
        groundDashPathEffect=new DashPathEffect(groundEffectFloat,0);
        bodyRadius= (int) (getMeasuredHeight()/3.4f);
        float[] bodyEffectFloat=new float[]{getRadianLength(56,bodyRadius)
                ,getRadianLength(4,bodyRadius)+bombLineWidth
                ,getRadianLength(2.5f,bodyRadius)
                ,getRadianLength(4,bodyRadius)+bombLineWidth
                ,getRadianLength(220,bodyRadius)
                ,getRadianLength(12,bodyRadius)+bombLineWidth
                ,getRadianLength(90,bodyRadius)
                ,0};
        bodyDashPathEffect=new DashPathEffect(bodyEffectFloat,0);

        highLightRadius =bodyRadius/3*2;
        float[] highLightFloat=new float[]{0,getRadianLength(95, highLightRadius)
                ,getRadianLength(0.5f, highLightRadius)
                ,getRadianLength(5, highLightRadius)+bombLineWidth
                ,getRadianLength(12, highLightRadius)
                ,getRadianLength(5, highLightRadius)+bombLineWidth
                ,getRadianLength(24, highLightRadius)
                ,getRadianLength(270, highLightRadius)};
        highLightPathEffect=new DashPathEffect(highLightFloat,0);

        float padding= (float) (2*bombLineWidth*Math.PI/2/72);
        mHeadEffect=new DashPathEffect(new float[]{padding*2,padding},0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHead(canvas);
        drawGround(canvas);
        drawBody(canvas);
        drawBodyBorder(canvas);
        drawFace(canvas);
        drawFaceShadow(canvas);
        drawHeadLine(canvas);
        drawBlast(canvas);
    }

    private void drawFaceShadow(Canvas canvas) {
        int save=canvas.saveLayer(0,0,getMeasuredWidth(),getMeasuredHeight(),null,Canvas.ALL_SAVE_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(bombShadowColor);
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()-bombLineWidth-bodyRadius,bodyRadius-bombLineWidth/2,mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.translate(-bodyRadius/5,-bodyRadius/5);
        mPaint.setColor(bombColor);
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()-bombLineWidth-bodyRadius,bodyRadius-bombLineWidth/2,mPaint);
        canvas.restoreToCount(save);
        mPaint.setXfermode(null);
    }

    private void drawFace(Canvas canvas) {
        //眼睛
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(bombLineColor);
        float eyeY=getMeasuredHeight()-bombLineWidth-bodyRadius+bodyRadius/5;
        canvas.drawCircle(getMeasuredWidth()/2-bodyRadius/3.5f,eyeY,bombLineWidth/2,mPaint);
        canvas.drawCircle(getMeasuredWidth()/2+bodyRadius/3.5f,eyeY,bombLineWidth/2,mPaint);
        //画嘴巴
        float mouthY=eyeY+bombLineWidth;
        float mouthMaxY=mouthY+bodyRadius/7;
        mPath.reset();
        mPath.moveTo(getMeasuredWidth()/2-bodyRadius/5,mouthY);
        mPath.lineTo(getMeasuredWidth()/2+bodyRadius/5,mouthY);
        mPath.quadTo(getMeasuredWidth()/2+bodyRadius/5-bodyRadius/5/4,mouthMaxY,getMeasuredWidth()/2,mouthMaxY);
        mPath.quadTo(getMeasuredWidth()/2-bodyRadius/5+bodyRadius/5/4,mouthMaxY,getMeasuredWidth()/2-bodyRadius/5,mouthY);
        mPath.close();
        canvas.drawPath(mPath,mPaint);
        //画舌头
        int save=canvas.saveLayer(0,0,getMeasuredWidth(),getMeasuredHeight(),null,Canvas.ALL_SAVE_FLAG);
        canvas.drawPath(mPath,mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mPaint.setColor(Color.parseColor("#f34671"));
        canvas.drawCircle(getMeasuredWidth()/2,mouthY+(mouthMaxY-mouthY)/8+bodyRadius/5,bodyRadius/5,mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.scale(0.8f,0.8f,getMeasuredWidth()/2,(mouthMaxY+mouthY)/2);
        canvas.drawPath(mPath,mPaint);
        canvas.restoreToCount(save);
        mPaint.setXfermode(null);
        //酒窝
        mPaint.setColor(Color.parseColor("#5689cc"));
        canvas.drawCircle(getMeasuredWidth()/2-bodyRadius/3.5f-bombLineWidth,(mouthMaxY+mouthY)/2,bombLineWidth/3,mPaint);
        canvas.drawCircle(getMeasuredWidth()/2+bodyRadius/3.5f+bombLineWidth,(mouthMaxY+mouthY)/2,bombLineWidth/3,mPaint);

        mPaint.setPathEffect(null);
    }

    private void drawBlast(Canvas canvas) {

    }

    private void drawHeadLine(Canvas canvas) {
        float beginY=getMeasuredHeight()-bombLineWidth-bodyRadius*2-bodyRadius/4-bodyRadius/4/4;
        mPath.reset();
        mPath.moveTo(getMeasuredWidth()/2,beginY);
        float controlY=beginY-bodyRadius/2;
        mPath.quadTo(getMeasuredWidth()/2+bodyRadius/2/5,controlY,getMeasuredWidth()/2+bodyRadius/2,controlY);
        mPath.quadTo(getMeasuredWidth()/2+bodyRadius/2+bodyRadius/2/5*4,controlY,getMeasuredWidth()/2+bodyRadius/2*2f,getMeasuredHeight()-bombLineWidth-bodyRadius*2);
        mPaint.setColor(bombLineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath,mPaint);
        mPathMeasure.setPath(mPath,false);
        float length=mPathMeasure.getLength();
        mPathMeasure.getPosTan(length,mPathPosition,null);
        mPaint.setColor(Color.parseColor("#fbb42d"));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mPathPosition[0],mPathPosition[1],bombLineWidth/1.8f,mPaint);
        mPaint.setColor(Color.parseColor("#f34671"));
        mPath.reset();
        mPath.addCircle(mPathPosition[0],mPathPosition[1],bombLineWidth/4, Path.Direction.CCW);
        mPaint.setPathEffect(mHeadEffect);
        canvas.drawPath(mPath,mPaint);
        mPaint.setPathEffect(null);
    }

    private void drawHead(Canvas canvas) {
        mPath.reset();
        mPath.moveTo(getMeasuredWidth()/2-bodyRadius/5,getMeasuredHeight()/2);
        mPath.lineTo(getMeasuredWidth()/2+bodyRadius/5,getMeasuredHeight()/2);
        mPath.lineTo(getMeasuredWidth()/2+bodyRadius/5,getMeasuredHeight()-bombLineWidth-bodyRadius*2-bodyRadius/4);
        mPath.lineTo(getMeasuredWidth()/2,getMeasuredHeight()-bombLineWidth-bodyRadius*2-bodyRadius/4-bodyRadius/4/4);
        mPath.lineTo(getMeasuredWidth()/2-bodyRadius/5,getMeasuredHeight()-bombLineWidth-bodyRadius*2-bodyRadius/4);
        mPath.close();
        mPaint.setStrokeWidth(bombLineWidth*0.8f);
        //内部
        mPaint.setColor(bombColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mPath,mPaint);
        //边框
        mPaint.setColor(bombLineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath,mPaint);
        mPaint.setStrokeWidth(bombLineWidth);
    }

    private void drawBody(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(bombColor);
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()-bombLineWidth-bodyRadius,bodyRadius-bombLineWidth/2,mPaint);
        //左上角光点
        mPaint.setPathEffect(highLightPathEffect);
        mPaint.setColor(lightColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPath.reset();
        mPath.addCircle(getMeasuredWidth()/2,getMeasuredHeight()-bombLineWidth-bodyRadius, highLightRadius, Path.Direction.CCW);
        canvas.drawPath(mPath,mPaint);
        //左上角的光边
        mPaint.setPathEffect(null);
        mRectF.set(getMeasuredWidth()/2-bodyRadius+bombLineWidth/2,getMeasuredHeight()-bombLineWidth-bodyRadius-bodyRadius+bombLineWidth/2
        ,getMeasuredWidth()/2+bodyRadius-bombLineWidth/2,getMeasuredHeight()-bombLineWidth-bombLineWidth/2);
        canvas.drawArc(mRectF,160,100,false,mPaint);
        //拼接光边
        mPath.reset();
        mPath.addCircle(getMeasuredWidth()/2,getMeasuredHeight()-bombLineWidth-bodyRadius,bodyRadius-bombLineWidth/2, Path.Direction.CCW);
        canvas.save();
        canvas.clipPath(mPath);
        //160度坐标计算
        mPath.reset();
        Point point=getPointInCircle(getMeasuredWidth()/2,getMeasuredHeight()-bombLineWidth-bodyRadius,bodyRadius-bombLineWidth,160);
        mPath.moveTo(point.x-bodyRadius,point.y);
        mPath.lineTo(point.x,point.y);
        Point pointControl=getPointInCircle(getMeasuredWidth()/2,getMeasuredHeight()-bombLineWidth-bodyRadius,bodyRadius-bombLineWidth+bombLineWidth*2.2f,210);
        point=getPointInCircle(getMeasuredWidth()/2,getMeasuredHeight()-bombLineWidth-bodyRadius,bodyRadius-bombLineWidth,260);
        mPath.quadTo(pointControl.x,pointControl.y,point.x,point.y);
        mPath.lineTo(point.x-bodyRadius,point.y);
        mPath.close();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(lightColor);
        canvas.drawPath(mPath,mPaint);
        canvas.restore();
    }


    private Point getPointInCircle(int circleX,int circleY,float radius,float angle){
        Point mPoint=new Point();
        mPoint.set((int)(circleX + radius * Math.cos(Math.toRadians(angle))),
                (int)(circleY+radius*Math.sin(Math.toRadians(angle))));
       return mPoint;
    }

    private void drawBodyBorder(Canvas canvas) {
        mPaint.setPathEffect(bodyDashPathEffect);
        mPaint.setColor(bombLineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPath.reset();
        mPath.addCircle(getMeasuredWidth()/2,getMeasuredHeight()-bombLineWidth-bodyRadius,bodyRadius, Path.Direction.CW);
        canvas.drawPath(mPath,mPaint);
    }

    private void drawGround(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(bombLineColor);
        mPaint.setPathEffect(groundDashPathEffect);
        mPath.reset();
        mPath.moveTo(bombLineWidth/2,getMeasuredHeight()-bombLineWidth/2);
        mPath.lineTo(getMeasuredWidth()-bombLineWidth/2,getMeasuredHeight()-bombLineWidth/2);
        canvas.drawPath(mPath,mPaint);
    }

    private float getRadianLength(float angle,float radius){
        return (float) (angle*Math.PI*radius/180f);
    }
}
