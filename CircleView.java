package com.github.newapplication.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

//todo
public class CircleView extends View {

    private int mTemperature = 17;
    private static final int TEXT_SIZE = 38;
    private static final int CIRCLE_WIDTH = 186;
    private static final int STROKE_WIDTH = 40;
    private static final float CIRCLE_RADIUS = 15;
    public static final int DISTANCE = 20;
    private static final String RED = "#EA5582";
    private static final String BLUE = "#0D90F0";
    private final DecimalFormat mDecimalFormat = new DecimalFormat("0.0");
    private final List<String> LINE_COLORS = Arrays.asList("#3AA9EE", "#3AA9EE", "#886FB2", "#D45B8D", "#E35785", "#E35785", "#E35785");
    private final int[] SWEEP_COLORS = {Color.parseColor(BLUE), Color.parseColor(RED)};
    public static final double RADIAN = 40 * Math.PI / 360;
    private float mTextSize;
    private float mCircleWidth;
    private float mStrokeWidth;
    private float mCircleRadius;
    private float x, y, moveX, moveY, downX, downY;
    private int mRadius;

    public CircleView(Context context) {
        super(context);
        init(context, null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

//TODO
    private void init(Context context, @Nullable AttributeSet attrs) {
        float density = context.getResources().getDisplayMetrics().density;
        mTextSize = TEXT_SIZE * density;
        mCircleWidth = CIRCLE_WIDTH * density;
        mStrokeWidth = STROKE_WIDTH * density;
        mCircleRadius = CIRCLE_RADIUS * density;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        int paddingBottom = getPaddingBottom();
        int paddingRight = getPaddingRight();
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;
        mRadius = Math.min(width, height) / 2;
        drawLine(canvas);
        drawBackGround(canvas);
        drawProgress(canvas);
        drawText(canvas);
    }

    private void drawLine(Canvas canvas) {
        int num = (mTemperature - 17) / 2;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(8);
        canvas.save();
        canvas.rotate(-135, mRadius, mRadius);
        for (int i = 0; i < 7; i++) {
            if (num <= i) {
                paint.setColor(Color.parseColor("#D9D9D9"));
            } else {
                paint.setColor(Color.parseColor(LINE_COLORS.get(i)));
            }
            canvas.drawLine(mRadius, 30, mRadius, 70, paint);
            canvas.rotate(45, mRadius, mRadius);
        }
        canvas.restore();
    }

    private void drawProgress(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mStrokeWidth);
        RectF rectF = new RectF(mRadius - mCircleWidth / 2f,
                mRadius - mCircleWidth / 2f,
                mRadius + mCircleWidth / 2f,
                mRadius + mCircleWidth / 2f);
        float progress = (mTemperature - 17) * 20f;
        int startAngle = 110;
        if (mTemperature == 17) {
            paint.setColor(Color.parseColor(BLUE));
            canvas.drawArc(rectF, startAngle, 1, false, paint);
        } else {
            SweepGradient sweepGradient = new SweepGradient(mRadius, mRadius, SWEEP_COLORS, new float[]{0.2f, 0.7f});
            Matrix matrix = new Matrix();
            matrix.setRotate(90, mRadius, mRadius);
            sweepGradient.setLocalMatrix(matrix);
            paint.setShader(sweepGradient);
            canvas.drawArc(rectF, startAngle, progress, false, paint);
        }
        drawWhitCircle(canvas);

    }

    private void drawWhitCircle(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        int degree = (mTemperature - 16) * 20;
        if (degree < 90) {
            double d = degree * 2 * Math.PI / 360;
            x = (float) (mRadius - (Math.sin(d) * mCircleWidth / 2));
            y = (float) (mRadius + (Math.cos(d) * mCircleWidth / 2));
        } else if (degree < 270) {
            double d = (degree - 180) * 2 * Math.PI / 360;
            x = (float) (mRadius + Math.sin(d) * mCircleWidth / 2f);
            y = (float) (mRadius - Math.cos(d) * mCircleWidth / 2f);
        } else {
            double d = (360 - degree) * 2 * Math.PI / 360;
            x = (float) (mRadius + Math.sin(d) * mCircleWidth / 2f);
            y = (float) (mRadius + Math.cos(d) * mCircleWidth / 2f);
        }
        canvas.drawCircle(x, y, mCircleRadius, paint);
    }

    private void drawBackGround(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#D9D9D9"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mStrokeWidth);
        canvas.drawCircle(mRadius, mRadius, mCircleWidth / 2f, paint);
    }

    private void drawText(@NonNull Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#5D607B"));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(mTextSize);
        String text = mDecimalFormat.format(mTemperature) + "\u00B0";
        Paint.FontMetrics metrics = paint.getFontMetrics();
        float distance = (metrics.bottom - metrics.top) / 2 - metrics.bottom;
        canvas.drawText(text, mRadius, mRadius + distance, paint);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                return isInRange((int) downX, (int) x - 50, (int) x + 50) && isInRange((int) downY, (int) y - 50, (int) y + 50);
            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                moveY = event.getY();
                float lenX = moveX - downX;
                float lenY = moveY - downY;
                float lenXY = (float) Math.sqrt((double) (lenX * lenX + lenY * lenY));
                double radian = Math.acos(lenX / lenXY);
                if (mTemperature < 21) {
                    if (moveX - downX < -DISTANCE && moveY - downY < -DISTANCE) {
                        slide(true);
                    } else if (moveX - downX > DISTANCE && moveY - downY > DISTANCE) {
                        slide(false);
                    }
                } else if (mTemperature < 25) {
                    if (moveX - downX > DISTANCE && moveY - downY < -DISTANCE) {
                        slide(true);
                    } else if (moveX - downX < -DISTANCE && moveY - downY > DISTANCE) {
                        slide(false);
                    }
                }  else if (mTemperature < 30) {
                    if (moveX - downX > DISTANCE && moveY - downY > DISTANCE) {
                        slide(true);
                    } else if (moveX - downX < -DISTANCE && moveY - downY < -DISTANCE) {
                        slide(false);
                    }
                } else {
                    if (moveX - downX < -DISTANCE && moveY - downY > DISTANCE) {
                        slide(true);
                    } else if (moveX - downX > DISTANCE && moveY - downY < -DISTANCE) {
                        slide(false);
                    }
                }

                return true;
        }
        return true;
    }

    private boolean isInRange(int current, int min, int max) {
        return Math.max(min, current) == Math.min(current, max);
    }

    public void add() {
        if (mTemperature >= 33) return;
        mTemperature++;
        invalidate();
    }

    public void minus() {
        if (mTemperature <= 17) return;
        mTemperature--;
        invalidate();
    }

    private void slide(boolean isAdd) {
        downX = moveX;
        downY = moveY;
        if (isAdd) {
            add();
        } else {
            minus();
        }
    }

    public void setTemperature(int temperature) {
        if (temperature >= 33 || temperature <= 17) return;
        this.mTemperature = temperature;
        invalidate();
    }

    public int getTemperature() {
        return mTemperature;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, 300);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, 300);
        }

    }
}
