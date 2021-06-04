package com.example.foodrescue.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.foodrescue.model.Box;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphicOverlay extends View {
    private Object lock;
    private List<Box> graphics;
    private Paint rectPaint;
    private Paint textBackgroundPaint;
    private Paint textPaint;
    private RectF rect;
    private float offset;
    private float round;
    private float scale;
    private float xOffset;
    private float yOffset;

    public GraphicOverlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.lock = new Object();
        graphics = new ArrayList<>();

        rect = new RectF();

        this.rectPaint = new Paint();
        this.rectPaint.setColor(Color.WHITE);
        this.rectPaint.setStyle(Paint.Style.STROKE);
        this.rectPaint.setStrokeWidth(2 * getResources().getDisplayMetrics().density);

        this.textBackgroundPaint = new Paint();
        this.textBackgroundPaint.setColor(Color.parseColor("#66000000"));
        this.textBackgroundPaint.setStyle(Paint.Style.FILL);

        this.textPaint = new Paint();
        this.textPaint.setColor(Color.WHITE);
        this.textPaint.setTextSize(20 * getResources().getDisplayMetrics().density);

        this.offset = 8 * getResources().getDisplayMetrics().density;
        this.round = 4 * getResources().getDisplayMetrics().density;

        this.scale = 1f;

        xOffset = 0f;
        yOffset = 0f;
    }

    public void setSize(int imageWidth, int imageHeight) {
        float overlayRatio = (float)this.getWidth() / (float)this.getHeight();
        float imageRatio = (float)imageWidth / (float)imageHeight;

        if (overlayRatio < imageRatio) {
            scale = (float)this.getHeight() / (float)imageHeight;
            xOffset = ((float)imageWidth * scale - (float)getWidth()) * 0.5F;
            yOffset = 0.0F;
        } else {
            scale = (float)this.getWidth() / (float)imageWidth;
            xOffset = 0.0F;
            yOffset = ((float)imageHeight * scale - (float)this.getHeight()) * 0.5F;
        }
    }

    public void set(List<Box> boxes) {
        synchronized(lock) {
            graphics.clear();
            graphics.addAll(boxes);
        }
        this.postInvalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (lock) {
            for (Box graphic : graphics) {
                rect.set(
                    graphic.getRect().left * scale,
                    graphic.getRect().top * scale,
                    graphic.getRect().right * scale,
                    graphic.getRect().bottom * scale
                );

                rect.offset(-xOffset, -yOffset);

                canvas.drawRect(rect, rectPaint);

                if (!TextUtils.isEmpty(graphic.getText())) {
                    canvas.drawRoundRect(
                        rect.left,
                        rect.bottom - offset,
                        rect.left + offset + textPaint.measureText(graphic.getText()) + offset,
                        rect.bottom + textPaint.getTextSize() + offset,
                        round,
                        round,
                        textBackgroundPaint
                    );

                    canvas.drawText(
                        graphic.getText(),
                        rect.left + offset,
                        rect.bottom + textPaint.getTextSize(),
                        textPaint
                    );
                }
            }
        }
    }
}
