package com.king.drawboard.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;


public class DrawBitmap extends Draw {

    Bitmap bitmap;

    private float lastX;
    private float lastY;

    private boolean isAnchorCenter;

    public DrawBitmap() {
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isAnchorCenter() {
        return isAnchorCenter;
    }

    public void setAnchorCenter(boolean anchorCenter) {
        isAnchorCenter = anchorCenter;
    }

    public void setPoint(PointF point) {
        this.lastX = point.x;
        this.lastY = point.y;
    }

    @Override
    public void actionDown(Canvas canvas, float x, float y,MotionEvent event) {
        super.actionDown(canvas, x, y,event);
        lastX = x;
        lastY = y;
    }

    @Override
    public void actionMove(Canvas canvas, float x, float y, MotionEvent event) {
        super.actionMove(canvas, x, y,event);
        if(bitmap != null){
            if(isAnchorCenter){
                canvas.drawBitmap(bitmap, bitmap.getWidth() / 2 + x, bitmap.getHeight() / 2 + y, null);
            }else{
                canvas.drawBitmap(bitmap, x,  y, null);
            }
        }
        lastX = x;
        lastY = y;
    }

    @Override
    public void draw(Canvas canvas) {
        if(bitmap != null){
            if(isAnchorCenter){
                canvas.drawBitmap(bitmap, bitmap.getWidth() / 2 + lastX, bitmap.getHeight() / 2 + lastY, null);
            }else{
                canvas.drawBitmap(bitmap, lastX,  lastY, null);
            }
        }
    }
}
