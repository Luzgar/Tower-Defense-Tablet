package com.example.kevinduglue.towerdefensetablet.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.kevinduglue.towerdefensetablet.R;
import com.snatik.polygon.Point;
import com.snatik.polygon.Polygon;

import java.util.ArrayList;
import java.util.List;


public class MapView extends View {


    List<Holder> holderList = new ArrayList<>();
    Polygon polygonbottomLeft;
    Polygon polygonmidLeft;
    Polygon polygonTopRight;
    Polygon polygonMidRight;
    Polygon polygonBottomRight;

    int pathSelected = -1;
    int baseSelected = -1;

    int phase = 0;
    boolean drawMap = true;
    Bitmap mFinalbitmap;

    Paint rectTutoPaint;

    boolean isTutorial = false;

    int width;
    int height;

    public MapView(Context context, AttributeSet t) {
        //1648  1456
        super(context, t);

        rectTutoPaint = new Paint();
        rectTutoPaint.setColor(getResources().getColor(R.color.fog_tutorial));


        mFinalbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(drawMap){
            this.width = canvas.getWidth();
            this.height = canvas.getHeight();
            generateArea();
            mFinalbitmap = Bitmap.createScaledBitmap(mFinalbitmap, canvas.getWidth(), canvas.getHeight(),true);
            drawMap = false;
        }

        phase--;
        phase = phase%100;

        canvas.drawBitmap(mFinalbitmap, 0, 0, null);

        for (Holder holder : holderList) {
            holder.addPathEffect(phase);
            canvas.drawPath(holder.path, holder.paint);
        }

        if(isTutorial)
            canvas.drawRect(0, 0, (canvas.getWidth()/2)-58, canvas.getHeight(), rectTutoPaint);

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            if (baseSelected == 0) {
                resetPathEffect();
                if(polygonbottomLeft.contains(new Point((int) event.getX(),(int) event.getY()))) {
                    holderList.get(1).isSelected();
                    pathSelected = 0;
                } else if (polygonmidLeft.contains(new Point((int) event.getX(),(int) event.getY()))) {
                    holderList.get(2).isSelected();
                    pathSelected = 1;
                }
            } else if (baseSelected == 3) {
                resetPathEffect();
                if(polygonTopRight.contains(new Point((int) event.getX(),(int) event.getY()))) {
                    holderList.get(1).isSelected();
                    pathSelected = 0;
                } else if (polygonMidRight.contains(new Point((int) event.getX(),(int) event.getY()))) {
                    holderList.get(2).isSelected();
                    pathSelected = 1;
                } else if (polygonBottomRight.contains(new Point((int) event.getX(),(int) event.getY()))) {
                    holderList.get(3).isSelected();
                    pathSelected = 2;
                }
            }
        }
        return true;
    }

    private void resetPathEffect() {
        for(Holder holder: holderList) {
            holder.resetSelect();
        }
    }

    private void generateArea() {
        polygonbottomLeft = Polygon.Builder()
                .addVertex(new Point(width/2.11, height/1.07))
                .addVertex(new Point(width/12.67, height/1.07))
                .addVertex(new Point(width/12.67, height/1.53))
                .addVertex(new Point(width/2.11, height/1.53))
                .build();

        polygonmidLeft = Polygon.Builder()
                .addVertex(new Point(width/2.06, height/1.82))
                .addVertex(new Point(width/2.06, height/1.23))
                .addVertex(new Point(width/3.74, height/1.23))
                .addVertex(new Point(width/3.74, height/1.82))
                .build();

        polygonTopRight = Polygon.Builder()
                .addVertex(new Point(width/2.06, height/1.61))
                .addVertex(new Point(width/2.06, height/6.93))
                .addVertex(new Point(width/1.08, height/6.93))
                .addVertex(new Point(width/1.08, height/1.61))
                .build();

        polygonMidRight = Polygon.Builder()
                .addVertex(new Point(width/2.06, height/1.67))
                .addVertex(new Point(width/1.16, height/1.67))
                .addVertex(new Point(width/1.16, height/1.50))
                .addVertex(new Point(width/2.06, height/1.50))
                .build();

        polygonBottomRight = Polygon.Builder()
                .addVertex(new Point(width/2.06, height/1.4))
                .addVertex(new Point(width/1.16, height/1.4))
                .addVertex(new Point(width/1.16, height/1.07))
                .addVertex(new Point(width/2.06, height/1.07))
                .build();

    }

    public void generateChurchPaths(boolean draw) {
        holderList.clear();
        pathSelected = -1;
        baseSelected = (draw) ? 0 : -1;
        if(draw) {
            Path beginMutualPath = new Path();
            beginMutualPath.moveTo((float) (width/2.06), (float) (height/1.04));
            beginMutualPath.lineTo((float) (width/2.06), (float) (height/1.23));
            holderList.add(new Holder(Color.BLACK, beginMutualPath));

            Path path1Church = new Path();
            path1Church.moveTo((float) (width/2.06), (float) (height/1.23));
            path1Church.lineTo((float) (width/2.99), (float) (height/1.23));
            path1Church.lineTo((float) (width/2.99), (float) (height/1.10));
            path1Church.lineTo((float) (width/7.16), (float) (height/1.10));
            path1Church.lineTo((float) (width/7.16), (float) (height/1.24));
            path1Church.lineTo((float) (width/3.74), (float) (height/1.24));
            path1Church.lineTo((float) (width/3.74), (float) (height/1.71));
            holderList.add(new Holder(Color.RED, path1Church));

            Path path2Church = new Path();
            path2Church.moveTo((float) (width/2.06), (float) (height/1.23));
            path2Church.lineTo((float) (width/2.06), (float) (height/1.58));
            path2Church.lineTo((float) (width/2.99), (float) (height/1.58));
            path2Church.lineTo((float) (width/2.99), (float) (height/1.71));
            path2Church.lineTo((float) (width/3.74), (float) (height/1.71));
            holderList.add(new Holder(Color.BLUE, path2Church));

            Path endMutualPath = new Path();
            endMutualPath.moveTo((float) (width/3.74), (float) (height/1.71));
            endMutualPath.lineTo((float) (width/3.74), (float) (height/1.91));
            endMutualPath.lineTo((float) (width/5.49), (float) (height/1.91));
            endMutualPath.lineTo((float) (width/5.49), (float) (height/1.58));
            endMutualPath.lineTo((float) (width/12.67), (float) (height/1.58));
            endMutualPath.lineTo((float) (width/12.67), (float) (height/1.71));
            holderList.add(new Holder(Color.BLACK, endMutualPath));
        }
    }

    public void generateWindMillPath(boolean draw) {
        holderList.clear();
        baseSelected = (draw) ? 1 : -1;
        if(draw) {
            Path windMillPath = new Path();
            windMillPath.moveTo((float) (width/2.06), (float) (height/1.04));
            windMillPath.lineTo((float) (width/2.06), (float) (height/1.58));
            windMillPath.lineTo((float) (width/2.99), (float) (height/1.58));
            windMillPath.lineTo((float) (width/2.99), (float) (height/2.11));
            windMillPath.lineTo((float) (width/2.37), (float) (height/2.11));
            windMillPath.lineTo((float) (width/2.37), (float) (height/2.69));
            windMillPath.lineTo((float) (width/8.90), (float) (height/2.69));
            windMillPath.lineTo((float) (width/8.90), (float) (height/6.93));
            windMillPath.lineTo((float) (width/2.81), (float) (height/6.93));
            Holder h = new Holder(Color.RED, windMillPath);
            h.isSelected();
            holderList.add(h);
        }
    }

    public void generateHostelPath(boolean draw) {
        holderList.clear();
        baseSelected = (draw) ? 2 : -1;
        if(draw) {
            Path hostelPath = new Path();
            hostelPath.moveTo((float) (width/2.06), (float) (height/1.04));
            hostelPath.lineTo((float) (width/2.06), (float) (height/2.69));
            hostelPath.lineTo((float) (width/1.64), (float) (height/2.69));
            hostelPath.lineTo((float) (width/1.64), (float) (height/1.91));
            hostelPath.lineTo((float) (width/1.37), (float) (height/1.91));
            hostelPath.lineTo((float) (width/1.37), (float) (height/6.93));
            hostelPath.lineTo((float) (width/1.56), (float) (height/6.93));
            Holder h = new Holder(Color.RED, hostelPath);
            h.isSelected();
            holderList.add(h);
        }
    }

    public void generateCastlePath(boolean draw) {
        holderList.clear();
        pathSelected = -1;
        baseSelected = (draw) ? 3 : -1;
        if(draw) {
            Path beginMutualPath = new Path();
            beginMutualPath.moveTo((float) (width/2.06), (float) (height/1.04));
            beginMutualPath.lineTo((float) (width/2.06), (float) (height/1.58));
            holderList.add(new Holder(Color.BLACK, beginMutualPath));

            Path casltePath = new Path();
            casltePath.moveTo((float) (width/2.06), (float) (height/1.58));
            casltePath.lineTo((float) (width/2.06), (float) (height/2.69));
            casltePath.lineTo((float) (width/1.64), (float) (height/2.69));
            casltePath.lineTo((float) (width/1.64), (float) (height/1.91));
            casltePath.lineTo((float) (width/1.37), (float) (height/1.91));
            casltePath.lineTo((float) (width/1.37), (float) (height/6.93));
            casltePath.lineTo((float) (width/1.05), (float) (height/6.93));
            casltePath.lineTo((float) (width/1.05), (float) (height/1.71));
            holderList.add(new Holder(Color.RED, casltePath));

            Path castlePath2 = new Path();
            castlePath2.moveTo((float) (width/2.06), (float) (height/1.58));
            castlePath2.lineTo((float) (width/1.16), (float) (height/1.58));
            holderList.add(new Holder(Color.BLUE, castlePath2));

            Path castlePath3 = new Path();
            castlePath3.moveTo((float) (width/2.06), (float) (height/1.23));
            castlePath3.lineTo((float) (width/1.64), (float) (height/1.23));
            castlePath3.lineTo((float) (width/1.64), (float) (height/1.09));
            castlePath3.lineTo((float) (width/1.43), (float) (height/1.09));
            castlePath3.lineTo((float) (width/1.43), (float) (height/1.33));
            castlePath3.lineTo((float) (width/1.30), (float) (height/1.33));
            castlePath3.lineTo((float) (width/1.30), (float) (height/1.09));
            castlePath3.lineTo((float) (width/1.20), (float) (height/1.09));
            castlePath3.lineTo((float) (width/1.20), (float) (height/1.33));
            castlePath3.lineTo((float) (width/1.13), (float) (height/1.33));
            holderList.add(new Holder(Color.YELLOW, castlePath3));
        }
    }

    private class Holder {
        Path path;
        Paint paint;
        boolean isSelected = false;

        Holder(int color, Path path) {
            this.path = path;

            paint = new Paint();
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(8);
        }

        void addPathEffect(int phase) {
            if(isSelected)
                paint.setPathEffect(new DashPathEffect(new float[] {10, 10}, phase));
            else
                paint.setPathEffect(new DashPathEffect(new float[] {0, 0}, phase));
        }

        void isSelected() {
            this.isSelected = !this.isSelected;
        }

        void resetSelect() {
            this.isSelected = false;
        }
    }

    public int getPathSelected() {
        return this.pathSelected;
    }

    public void setPathSelected(int pathSelected) {
        this.pathSelected = pathSelected;
    }

    public void isTutorial(boolean isTutorial) {
        this.isTutorial = isTutorial;
    }
}