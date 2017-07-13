package com.xyp.tiange.loadingview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

/**
 * User: xyp
 * Date: 2017/7/12
 * Time: 18:06
 */

public class LoadingView extends View {
    private Paint mPaint;
    private int bRadius;//大圆
    private int[] outSideRadius;//外圈圆半径数组
    private int constantRadius;//用来保存小圆的半径不变,不然会影响view的大小
    private float bX, bY;
    private float[] outSideX, outSideY;
    private float startRadius;//小圆对于大圆圆心角度
    private int outSideAniDuration;
    private int outSideCircleColor;
    private ValueAnimator outsideAni;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray t = context.obtainStyledAttributes(attrs,R.styleable.LoadingView);
        outSideCircleColor = t.getColor(R.styleable.LoadingView_outSideColor,Color.BLUE);
        bRadius = t.getInt(R.styleable.LoadingView_bigCircleRadius,60);
        outSideAniDuration = t.getInt(R.styleable.LoadingView_animationDuration,2000);
        t.recycle();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        outSideRadius = new int[]{5, 6, 7, 8};
        constantRadius = outSideRadius[3];
        bX = getMeasuredWidth() / 2;
        bY = getMeasuredHeight() / 2;
        resetOutSideXY();

        outsideAni = ValueAnimator.ofFloat(0, 360);
        outsideAni.setDuration(outSideAniDuration);
        outsideAni.setRepeatCount(-1);//循环
        outsideAni.setRepeatMode(ValueAnimator.RESTART);
        //插值器
        outsideAni.setInterpolator(new AccelerateDecelerateInterpolator());
        outsideAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                for (int i = 0; i < outSideX.length; i++) {
                    startRadius = (float) animation.getAnimatedValue() + i*14;
                    if(startRadius > 360)
                        startRadius = 360;
                    outSideX[i] = (float) Math.cos(startRadius * Math.PI / 180) * bRadius + bX;
                    outSideY[i] = (float) Math.sin(startRadius * Math.PI / 180) * bRadius + bY;
                }
                invalidate();
            }
        });
        outsideAni.start();
    }

    private void resetOutSideXY(){
        //从圆心右边同Y轴开始
        outSideX = new float[]{bX+bRadius,bX+bRadius,bX+bRadius,bX+bRadius};
        outSideY = new float[]{bY,bY,bY,bY};
    }

    public void cancelAni(){
        if(outsideAni != null){
            outsideAni.cancel();
            outsideAni = null;
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? measureWidth : 2 * (bRadius + constantRadius),
                heightMode == MeasureSpec.EXACTLY ? measureHeight : 2 * (bRadius + constantRadius));
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        canvas.drawCircle(bX, bY, bRadius, mPaint);

        mPaint.setStyle(Paint.Style.FILL);

        mPaint.setColor(outSideCircleColor);

        for (int i = 0; i < outSideRadius.length; i++) {
            canvas.drawCircle(outSideX[i], outSideY[i], outSideRadius[i], mPaint);
        }
    }
}
