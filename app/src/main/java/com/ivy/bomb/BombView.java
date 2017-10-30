package com.ivy.bomb;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
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
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import static android.animation.ValueAnimator.ofFloat;

/**
 * Created by ivy on 2017/10/24.
 * Description：
 */

public class BombView extends View {
    private Paint mPaint;
    private Path mPath, mHeadLinePath ,mHeadPath ,mBodyLightPath;
    private int bombColor = Color.parseColor("#88B0E8");
    private int bombLineColor = Color.parseColor("#181D82");
    private int bombShadowColor = Color.parseColor("#77609ee6");
    private int lightColor = Color.WHITE;
    private int bombLineWidth;
    //炸弹身体半径 高光半径
    private int bodyRadius, highLightRadius;
    //用于处理线条的分割点
    private DashPathEffect groundDashPathEffect,bodyDashPathEffect,highLightPathEffect,mHeadEffect;
    private RectF mRectF;
    private PathMeasure mPathMeasure=new PathMeasure();
    private float[] mPathPosition=new float[2];
    //动画控制相关
    private float faceLROffset , faceMaxLROffset;
    private float faceTBOffset =0, faceMaxTBOffset;
    private float bombLRRotate =15, bombMaxLRRotate =15 ,bombTBRotate=0, bombMaxTBRotate =5;
    private float eyeRadius,eyeMaxRadius,eyeMinRadius;
    private float headLinePercent=1,headLineLightRate=0;
    private float maxBlastCircleRadius, currentBlastCircleRadius,blastCircleRadiusPercent;
    //用于计算mouth第二阶段变化
    private float mouthMaxWidthOffset, mouthMaxHeightOffset, mouthWidthOffset=0, mouthHeightOffset =0,mouthOffsetPercent=0;
    //用于计算mouth第一阶段变化
    private float mouthFSMaxWidthOffset,mouthFSMaxHeightOffset,mouthFSWidthOffset=0,mouthFSHeightOffset=0;
    //炸弹的中心点
    private int bombCenterX,bombCenterY;
    //用于控制旋转
    private Camera mCamera=new Camera();
    private Matrix mMatrix=new Matrix();


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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=getMeasuredWidth();
        int height=getMeasuredHeight();
        int minLength= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,100,getContext().getResources().getDisplayMetrics());
        minLength=Math.max(Math.min(width,height),minLength);
        setMeasuredDimension(minLength,minLength);
    }

    private void init() {
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPath=new Path();
        mRectF=new RectF();
        mHeadLinePath =new Path();
        mHeadPath =new Path();
        mBodyLightPath =new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bombLineWidth =getMeasuredWidth()/35;
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

        faceMaxLROffset =bodyRadius/3;
        faceLROffset =-faceMaxLROffset;
        eyeRadius=eyeMaxRadius=bombLineWidth/2;
        eyeMinRadius=eyeMaxRadius/6;

        faceMaxTBOffset=bodyRadius/3;
        maxBlastCircleRadius = getMeasuredWidth()*(2);

        mouthMaxWidthOffset =bodyRadius/5-bodyRadius/5/10;
        mouthMaxHeightOffset =bodyRadius/5/2;

        mouthFSMaxWidthOffset= mouthMaxWidthOffset/3;
        mouthFSMaxHeightOffset = mouthMaxHeightOffset/2;

        bombCenterX=getMeasuredWidth()/2;
        bombCenterY=getMeasuredHeight()-bombLineWidth-bodyRadius;

        setHeadLinePath();
        setHeadPath();
        setBodyLightPath();
    }

    private void setBodyLightPath() {
        //160度坐标计算
        mBodyLightPath.reset();
        Point point=getPointInCircle(bombCenterX,bombCenterY,bodyRadius-bombLineWidth,160);
        mBodyLightPath.moveTo(point.x-bodyRadius,point.y);
        mBodyLightPath.lineTo(point.x,point.y);
        Point pointControl=getPointInCircle(bombCenterX,bombCenterY,bodyRadius-bombLineWidth+bombLineWidth*2.2f,210);
        point=getPointInCircle(bombCenterX,bombCenterY,bodyRadius-bombLineWidth,260);
        mBodyLightPath.quadTo(pointControl.x,pointControl.y,point.x,point.y);
        mBodyLightPath.lineTo(point.x-bodyRadius,point.y);
        mBodyLightPath.close();
    }

    private void setHeadPath() {
        mHeadPath.reset();
        mHeadPath.moveTo(bombCenterX-bodyRadius/5,getMeasuredHeight()/2);
        mHeadPath.lineTo(bombCenterX+bodyRadius/5,getMeasuredHeight()/2);
        mHeadPath.lineTo(bombCenterX+bodyRadius/5,bombCenterY-bodyRadius-bodyRadius/4);
        mHeadPath.lineTo(bombCenterX,bombCenterY-bodyRadius-bodyRadius/4-bodyRadius/4/4);
        mHeadPath.lineTo(bombCenterX-bodyRadius/5,bombCenterY-bodyRadius-bodyRadius/4);
        mHeadPath.close();
    }

    private void setHeadLinePath() {
        float beginY=bombCenterY-bodyRadius-bodyRadius/4-bodyRadius/4/4;
        mHeadLinePath.reset();
        mHeadLinePath.moveTo(bombCenterX,beginY);
        float controlY=beginY-bodyRadius/2.2f;
        float bottomPointY=bombCenterY-bodyRadius;//转折点高度
        mHeadLinePath.quadTo(bombCenterX+bodyRadius/2/5,controlY,bombCenterX+bodyRadius/2,controlY);
        mHeadLinePath.cubicTo(bombCenterX+bodyRadius/2+bodyRadius/2/5*4,controlY
                ,bombCenterX+bodyRadius/2*2f-bodyRadius/16,bottomPointY-bodyRadius/6
                ,bombCenterX+bodyRadius/2*2f,bottomPointY);
        mHeadLinePath.quadTo(bombCenterX+bodyRadius/2*2f+bodyRadius/16,bottomPointY+bodyRadius/6
                ,bombCenterX+bodyRadius/2*2f+bodyRadius/7,bottomPointY+bodyRadius/4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHead(canvas);//头部小房子
        drawGround(canvas);//地板
        drawBody(canvas);//身体
        drawBodyBorder(canvas);//身体边框
        drawFace(canvas);//脸
        drawFaceShadow(canvas);//脸阴影
        drawHeadLine(canvas);//头部的炸弹线
        drawBlast(canvas);//爆炸
    }

    private void drawFaceShadow(Canvas canvas) {
        //两个圆相交产生阴影
        int save=canvas.saveLayer(0,0,getMeasuredWidth(),getMeasuredHeight(),null,Canvas.ALL_SAVE_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(bombShadowColor);
        canvas.drawCircle(bombCenterX,bombCenterY,bodyRadius-bombLineWidth/2,mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.translate(-bodyRadius/5,-bodyRadius/5);
        mPaint.setColor(bombColor);
        canvas.drawCircle(bombCenterX,bombCenterY,bodyRadius-bombLineWidth/2,mPaint);
        canvas.restoreToCount(save);
        mPaint.setXfermode(null);
    }

    private void drawFace(Canvas canvas) {
        canvas.save();
        mCamera.save();
        mCamera.rotate(bombTBRotate,0,-bombLRRotate/3);
        mMatrix.reset();
        mCamera.getMatrix(mMatrix);
        mCamera.restore();
        mMatrix.preTranslate(-bombCenterX,-(bombCenterY));
        mMatrix.postTranslate(bombCenterX,bombCenterY);
        mMatrix.postTranslate(faceLROffset,faceTBOffset);
        canvas.concat(mMatrix);
        //眼睛 椭圆控制
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(bombLineColor);
        float eyeY=bombCenterY+bodyRadius/5;
        float eyeWidth=Math.max(eyeMaxRadius,eyeRadius);
        mRectF.set(bombCenterX-bodyRadius/3.5f-eyeWidth,eyeY-eyeRadius
        ,bombCenterX-bodyRadius/3.5f+eyeWidth,eyeY+eyeRadius);
        canvas.drawOval(mRectF,mPaint);
        mRectF.set(bombCenterX+bodyRadius/3.5f-eyeWidth,eyeY-eyeRadius
                ,bombCenterX+bodyRadius/3.5f+eyeWidth,eyeY+eyeRadius);
        canvas.drawOval(mRectF,mPaint);
        //画嘴巴 路径
        float mouthY=eyeY+bombLineWidth- mouthHeightOffset;//嘴巴起始高度
        float mouthMaxY=mouthY+bodyRadius/7+ mouthHeightOffset - mouthFSHeightOffset;//嘴巴最底部
        float mouthHalfDistance=bodyRadius/5-mouthWidthOffset*0.5f + mouthFSWidthOffset;//嘴巴顶部的拐角的一半宽度
        float mouthTopHalfDistance=(mouthHalfDistance-bodyRadius/5/10)-mouthWidthOffset; //嘴巴顶部的一半宽度
        float mouthHorDistanceHalf=(mouthMaxY-mouthY)/(6-4*mouthOffsetPercent);//嘴角控制点的距离嘴角点的竖直距离
        if (mouthTopHalfDistance<bodyRadius/5/10){//让过渡更加自然
            mouthTopHalfDistance=0;
        }
        mPath.reset();
        mPath.moveTo(bombCenterX-mouthTopHalfDistance,mouthY);
        mPath.lineTo(bombCenterX+mouthTopHalfDistance,mouthY);

        mPath.quadTo(bombCenterX+mouthHalfDistance,mouthY,
                bombCenterX+mouthHalfDistance,mouthY+mouthHorDistanceHalf);
        mPath.cubicTo(bombCenterX+mouthHalfDistance,mouthY+mouthHorDistanceHalf*2,
                bombCenterX+(mouthHalfDistance-bodyRadius/5/4)*(1-mouthOffsetPercent),mouthMaxY,
                bombCenterX,mouthMaxY);

        mPath.cubicTo(bombCenterX-(mouthHalfDistance-bodyRadius/5/4)*(1-mouthOffsetPercent),mouthMaxY,
                bombCenterX-mouthHalfDistance,mouthY+mouthHorDistanceHalf*2,
                bombCenterX-mouthHalfDistance,mouthY+mouthHorDistanceHalf);
        mPath.quadTo(bombCenterX-mouthHalfDistance,mouthY,bombCenterX-mouthTopHalfDistance,mouthY);
        mPath.close();
        canvas.drawPath(mPath,mPaint);
        //画舌头 圆和嘴巴的缩放相交
        int save=canvas.saveLayer(0,0,getMeasuredWidth(),getMeasuredHeight(),null,Canvas.ALL_SAVE_FLAG);
        canvas.drawPath(mPath,mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mPaint.setColor(Color.parseColor("#f34671"));
        canvas.drawCircle(bombCenterX,mouthY+(mouthMaxY-mouthY)/8+bodyRadius/(5-1.4f*mouthOffsetPercent),bodyRadius/5,mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.scale(0.8f,0.8f,bombCenterX,(mouthMaxY+mouthY)/2);
        canvas.drawPath(mPath,mPaint);
        canvas.restoreToCount(save);
        mPaint.setXfermode(null);
        //酒窝
        mPaint.setColor(Color.parseColor("#5689cc"));
        canvas.drawCircle(bombCenterX-bodyRadius/3.5f-bombLineWidth,(mouthMaxY+mouthY)/2,bombLineWidth/3,mPaint);
        canvas.drawCircle(bombCenterX+bodyRadius/3.5f+bombLineWidth,(mouthMaxY+mouthY)/2,bombLineWidth/3,mPaint);
        mPaint.setPathEffect(null);
        canvas.restore();
    }

    private void drawBlast(Canvas canvas) {
        if (blastCircleRadiusPercent==0){
            return;
        }
        float circleY=bombCenterY-bodyRadius-bodyRadius/4-bodyRadius/4/4;
        int save = canvas.saveLayer(0,0,getMeasuredWidth(),getMeasuredHeight(),null,Canvas.ALL_SAVE_FLAG);
        float distance = maxBlastCircleRadius/12;
        //画圆
        mPaint.setColor(lightColor);
        canvas.drawCircle(bombCenterX,circleY, currentBlastCircleRadius,mPaint);
        mPaint.setColor(bombColor);
        canvas.drawCircle(bombCenterX,circleY, currentBlastCircleRadius -distance,mPaint);
        mPaint.setColor(bombLineColor);
        canvas.drawCircle(bombCenterX,circleY, currentBlastCircleRadius -distance*2,mPaint);
        mPaint.setColor(lightColor);
        canvas.drawCircle(bombCenterX,circleY, currentBlastCircleRadius -distance*3,mPaint);
        //掏空
        if (blastCircleRadiusPercent >0.65) {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            canvas.drawCircle(bombCenterX, circleY, currentBlastCircleRadius - maxBlastCircleRadius * 0.65f, mPaint);
            mPaint.setXfermode(null);
        }
        canvas.restoreToCount(save);
    }

    private void drawHeadLine(Canvas canvas) {
        canvas.save();
        mCamera.save();
        mCamera.rotate(bombTBRotate,0,-bombLRRotate);
        mMatrix.reset();
        mCamera.getMatrix(mMatrix);
        mCamera.restore();
        mMatrix.preTranslate(-bombCenterX,-bombCenterY);
        mMatrix.postTranslate(bombCenterX,bombCenterY);
        canvas.concat(mMatrix);
        mPaint.setColor(bombLineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPathMeasure.setPath(mHeadLinePath,false);
        mPath.reset();
        mPathMeasure.getSegment(0,mPathMeasure.getLength()*headLinePercent,mPath,true);
        canvas.drawPath(mPath,mPaint);
        //火光
        mPathMeasure.setPath(mPath,false);
        float length=mPathMeasure.getLength();
        mPathMeasure.getPosTan(length,mPathPosition,null);
        mPaint.setColor(Color.parseColor("#fbb42d"));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mPathPosition[0],mPathPosition[1],Math.max(bombLineWidth/1.3f*headLineLightRate,bombLineWidth/2*1.2f),mPaint);
        mPaint.setColor(Color.parseColor("#f34671"));
        mPath.reset();
        mPath.addCircle(mPathPosition[0],mPathPosition[1],bombLineWidth/2.5f*headLineLightRate, Path.Direction.CCW);
        mPaint.setPathEffect(mHeadEffect);
        canvas.drawPath(mPath,mPaint);
        mPaint.setPathEffect(null);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mPathPosition[0],mPathPosition[1], bombLineWidth /6.5f * headLineLightRate,mPaint);
        canvas.restore();
    }

    private void drawHead(Canvas canvas) {
        canvas.save();
        mCamera.save();
        mCamera.rotate(bombTBRotate,0,-bombLRRotate);
        mMatrix.reset();
        mCamera.getMatrix(mMatrix);
        mCamera.restore();
        mMatrix.preTranslate(-bombCenterX,-(bombCenterY));
        mMatrix.postTranslate(bombCenterX,(bombCenterY));
        canvas.concat(mMatrix);

        mPaint.setStrokeWidth(bombLineWidth*0.8f);
        //内部
        mPaint.setColor(bombColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mHeadPath,mPaint);
        //边框
        mPaint.setColor(bombLineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mHeadPath,mPaint);
        mPaint.setStrokeWidth(bombLineWidth);
        canvas.restore();
    }

    private void drawBody(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(bombColor);
        canvas.drawCircle(bombCenterX,bombCenterY,bodyRadius-bombLineWidth/2,mPaint);
        //左上角光点
        mPaint.setPathEffect(highLightPathEffect);
        mPaint.setColor(lightColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPath.reset();
        mPath.addCircle(bombCenterX,bombCenterY, highLightRadius, Path.Direction.CCW);
        canvas.drawPath(mPath,mPaint);
        //左上角的光边
        mPaint.setPathEffect(null);
        mRectF.set(bombCenterX-bodyRadius+bombLineWidth/2,bombCenterY-bodyRadius+bombLineWidth/2
        ,bombCenterX+bodyRadius-bombLineWidth/2,getMeasuredHeight()-bombLineWidth-bombLineWidth/2);
        canvas.drawArc(mRectF,160,100,false,mPaint);
        //拼接光边
        mPath.reset();
        mPath.addCircle(bombCenterX,bombCenterY,bodyRadius-bombLineWidth/2, Path.Direction.CCW);
        canvas.save();
        canvas.clipPath(mPath);//裁剪圆内

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(lightColor);
        canvas.drawPath(mBodyLightPath,mPaint);
        canvas.restore();
    }


    private Point getPointInCircle(int circleX,int circleY,float radius,float angle){
        Point mPoint=new Point();
        mPoint.set((int)(circleX + radius * Math.cos(Math.toRadians(angle))),
                (int)(circleY+radius*Math.sin(Math.toRadians(angle))));
       return mPoint;
    }

    private void drawBodyBorder(Canvas canvas) {
        canvas.save();
        canvas.rotate(bombLRRotate,bombCenterX,bombCenterY);
        mPaint.setPathEffect(bodyDashPathEffect);
        mPaint.setColor(bombLineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPath.reset();
        mPath.addCircle(bombCenterX,bombCenterY,bodyRadius, Path.Direction.CW);
        canvas.drawPath(mPath,mPaint);
        canvas.restore();
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

    private AnimatorSet set=new AnimatorSet();
    public void startAnim(){
        stopAnim();
        ValueAnimator faceTBAnim=getFaceTopBottomAnim();
        ValueAnimator faceLRAnim=getFaceLeftRightAnim();
        AnimatorSet faceChangeAnim=getFaceChangeAnim();
        set.play(getBlastAnim()).after(faceChangeAnim);
        set.play(faceChangeAnim).after(faceTBAnim);
        set.play(faceTBAnim).after(faceLRAnim);
        set.play(faceLRAnim).with(getHeadLineAnim());
        set.start();
    }

    public void stopAnim(){
        set.cancel();
        initData();
        invalidate();
    }

    private void initData() {
        faceTBOffset=0;
        bombTBRotate=0;
        bombLRRotate=15;
        faceLROffset=-faceMaxLROffset;
        currentBlastCircleRadius =0;
        blastCircleRadiusPercent=0;
        eyeRadius=eyeMaxRadius;
        headLinePercent=1;
        mouthWidthOffset=0;
        mouthHeightOffset =0;
        mouthOffsetPercent=0;
        mouthFSWidthOffset=0;
        mouthFSHeightOffset=0;
        headLineLightRate=0;
    }

    private int getFaceChangeAnimTime(){
        return 450;
    }

    private AnimatorSet getFaceChangeAnim(){
        AnimatorSet animatorSet=new AnimatorSet();
        //眼睛
        ValueAnimator valueAnimator= ValueAnimator.ofFloat(1,0,1.4f)
                .setDuration(getFaceChangeAnimTime());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                eyeRadius=eyeMaxRadius*(float) animation.getAnimatedValue();
                if (eyeRadius<eyeMinRadius){
                    eyeRadius=eyeMinRadius;
                }
            }
        });

        ValueAnimator mouthAnimator=ValueAnimator.ofFloat(0,1,0,1)
                .setDuration(getFaceChangeAnimTime());
        mouthAnimator.setInterpolator(new DecelerateInterpolator());
        mouthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value= (float) animation.getAnimatedValue();
                if (animation.getAnimatedFraction()>0.75){//第一阶段还是第二阶段
                    mouthWidthOffset=mouthMaxWidthOffset*value;
                    mouthHeightOffset = mouthMaxHeightOffset *value;
                    mouthOffsetPercent=animation.getAnimatedFraction();
                }else{
                    mouthFSWidthOffset=mouthFSMaxWidthOffset*value;
                    mouthFSHeightOffset=mouthFSMaxHeightOffset*value;
                }
                invalidate();
            }
        });
        animatorSet.play(valueAnimator).with(mouthAnimator);
        return animatorSet;
    }

    private int getFaceTopBottomAnimTime(){
        return 300;
    }
    private int getFaceTopBottomAnimDelayTime(){
        return 200;
    }

    private ValueAnimator getFaceTopBottomAnim(){
        ValueAnimator objectAnimator= ofFloat(0,1)
                .setDuration(getFaceTopBottomAnimTime());
        objectAnimator.setStartDelay(getFaceTopBottomAnimDelayTime());
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value= (float) animation.getAnimatedValue();
                faceTBOffset= -faceMaxTBOffset*value;
                bombTBRotate= bombMaxTBRotate*value;
                invalidate();
            }
        });
        return objectAnimator;
    }

    private int getFaceLeftRightAnimTime(){
        return 1400;
    }
    private ValueAnimator getFaceLeftRightAnim(){
        ValueAnimator valueAnimator= ofFloat(-1,0,1,0)
                .setDuration(getFaceLeftRightAnimTime());
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value= (float) animation.getAnimatedValue();
                faceLROffset = faceMaxLROffset *value;
                bombLRRotate = -bombMaxLRRotate *value;
                if (Math.abs(value)<0.3&&animation.getAnimatedFraction()<0.6){
                    eyeRadius=Math.max(eyeMaxRadius*Math.abs(value),eyeMinRadius);
                }else{
                    eyeRadius=eyeMaxRadius;
                }
                invalidate();
            }
        });
       return valueAnimator;
    }

    private int getHeadLineAnimTime(){
        return getFaceLeftRightAnimTime()+getFaceTopBottomAnimTime()+getFaceTopBottomAnimDelayTime()+getFaceChangeAnimTime()+500;
    }
    private AnimatorSet getHeadLineAnim(){
        AnimatorSet animatorSet=new AnimatorSet();
        ValueAnimator valueAnimator= ofFloat(1f,0f)
                .setDuration(getHeadLineAnimTime());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                headLinePercent= (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        ValueAnimator valueAnimatorLight=ValueAnimator.ofFloat(0f,1.3f,0f)
                .setDuration(getHeadLineAnimTime()/5);
        valueAnimatorLight.setRepeatCount(5);
        valueAnimatorLight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                headLineLightRate= (float) animation.getAnimatedValue();
            }
        });
        animatorSet.play(valueAnimator).with(valueAnimatorLight);
        return animatorSet;
    }

    private ValueAnimator getBlastAnim(){
        ValueAnimator valueAnimator= ofFloat(0,maxBlastCircleRadius)
                .setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedFraction()>0.7f){
                    initData();
                }
                currentBlastCircleRadius = (float) animation.getAnimatedValue();
                blastCircleRadiusPercent=animation.getAnimatedFraction();
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                initData();
                invalidate();
            }
        });
        return valueAnimator;
    }

    private float getRadianLength(float angle,float radius){
        return (float) (angle*Math.PI*radius/180f);
    }
}
