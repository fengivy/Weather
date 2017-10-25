package com.ivy.main;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ivy on 2017/9/27.
 * Description：
 */

public abstract class IvyOnTouchListener extends RecyclerView.SimpleOnItemTouchListener {
    private RecyclerView mRecyclerView;
    private Context mContext;

    public IvyOnTouchListener(Context context){
        this.mContext=context;
    }
    private GestureDetector mGestureDetector=new GestureDetector(mContext,new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View childView = findChildViewUnder(e.getX(), e.getY());
            if (childView != null) {
                int position = mRecyclerView.getChildLayoutPosition(childView);
                onItemClick(position, childView);
                return true;
            }

            if (onEmptyClick(e)){
                return true;
            }
            return super.onSingleTapUp(e);
        }
    });

    public View findChildViewUnder(float x, float y) {
        final int count = mRecyclerView.getChildCount();
        Rect rect=new Rect();
        for (int i = count - 1; i >= 0; i--) {
            final View child = mRecyclerView.getChildAt(i);
            child.getHitRect(rect);
            if (rect.contains((int) x,(int) y)){
                return child;
            }
        }
        return null;
    }

    protected abstract boolean onEmptyClick(MotionEvent e);


    public abstract void onItemClick(int position, View childView);

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mRecyclerView=rv;
        return mGestureDetector.onTouchEvent(e);
    }


}
