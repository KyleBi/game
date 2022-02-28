package com.github.newapplication.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class ProgressButton extends View {

    private static final int BORDER = 2;
    private float progress = 0.5f;
    private State mState = State.NORMAL;
    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mRectF;
    private Path mPath;
    private int moveDistance;
    private int mWaveLength;

    public ProgressButton(Context context) {
        super(context);
        init(context, null);
    }

    public ProgressButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }



    private void init(Context context, @Nullable AttributeSet attrs) {
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.BLUE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(60);
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundPaint.setAntiAlias(true);

        mProgressPaint.setColor(Color.RED);
        PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        mProgressPaint.setXfermode(porterDuffXfermode);
        mProgressPaint.setAntiAlias(true);

        mStrokePaint.setColor(Color.BLUE);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(BORDER);
        mRectF = new RectF();
        mPath = new Path();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas backgroundCanvas = new Canvas(bitmap);
        switch (mState) {
            case NORMAL:
            case ERROR:
            case DETAIL:
                mRectF.set(0, 0, getWidth(), getHeight());
                drawBackground(backgroundCanvas, Color.GREEN);
                break;
            case DOWNLOADING:
            case PAUSE:
                mRectF.set(BORDER, BORDER, getWidth() - BORDER, getHeight() - BORDER);
                canvas.drawRoundRect(mRectF, getWidth() / 2f, getWidth() / 2f, mStrokePaint);
                drawBackground(backgroundCanvas, Color.WHITE);

                float right = getWidth() * progress;
                mWaveLength = getHeight() - BORDER * 2;
                int distance = mWaveLength / 2;
                mPath.reset();
                mPath.moveTo(right, -mWaveLength + moveDistance);
                for (int i = 0; i <= 2; i++) {
                    mPath.rQuadTo(-15, distance / 2f, 0, distance);
                    mPath.rQuadTo(15, distance / 2f, 0, distance);
                }
                mPath.lineTo(0, getHeight());
                mPath.lineTo(0, 0);
                mPath.close();
                backgroundCanvas.drawPath(mPath, mProgressPaint);
                break;
            case WAIT:
            case INSTALL:
                mRectF.set(0, 0, getWidth(), getHeight());
                drawBackground(backgroundCanvas, Color.GRAY);
                break;
            default:
                break;
        }
        canvas.drawBitmap(bitmap, 0, 0, null);
        drawText(canvas, mState.getText());
    }

    private void drawBackground(Canvas backgroundCanvas, int gray) {
        mBackgroundPaint.setColor(gray);
        backgroundCanvas.drawRoundRect(mRectF, getWidth() / 2f, getWidth() / 2f, mBackgroundPaint);
    }

    private void drawText(Canvas canvas, String text) {
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        float distance = (metrics.bottom - metrics.top) / 2 - metrics.bottom;
        canvas.drawText(text, getWidth() / 2f, getHeight() / 2f + distance, mTextPaint);
    }

    public void setState(State state) {
        this.mState = state;
        invalidate();
    }

    public void setProgress(float progress) {
        this.progress = progress;
        startAnim();
        invalidate();
    }

    public void startAnim() {
        ValueAnimator animator = ValueAnimator.ofInt(0, mWaveLength);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            moveDistance = (int) animation.getAnimatedValue();
            postInvalidate();
        });
        animator.start();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, 150);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, 150);
        }

    }

    public enum State {
        NORMAL("下载"),
        DOWNLOADING("暂停"),
        PAUSE("继续"),
        WAIT("等待中"),
        INSTALL("安装中"),
        ERROR("重新下载"),
        DETAIL("详情");
        private final String text;

        State(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

    }
}

