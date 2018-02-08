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

    public MapView(Context context, AttributeSet t) {
        super(context, t);

        rectTutoPaint = new Paint();
        rectTutoPaint.setColor(getResources().getColor(R.color.black_half_transparent));

        polygonbottomLeft = Polygon.Builder()
                .addVertex(new Point(780, 1350))
                .addVertex(new Point(130, 1350))
                .addVertex(new Point(130, 950))
                .addVertex(new Point(780, 950))
                .build();

        polygonmidLeft = Polygon.Builder()
                .addVertex(new Point(800, 800))
                .addVertex(new Point(800, 1175))
                .addVertex(new Point(440, 1175))
                .addVertex(new Point(440, 800))
                .build();

        polygonTopRight = Polygon.Builder()
                .addVertex(new Point(800, 900))
                .addVertex(new Point(800, 210))
                .addVertex(new Point(1525, 210))
                .addVertex(new Point(1525, 900))
                .build();

        polygonMidRight = Polygon.Builder()
                .addVertex(new Point(800, 870))
                .addVertex(new Point(1410, 870))
                .addVertex(new Point(1410, 970))
                .addVertex(new Point(800, 970))
                .build();

        polygonBottomRight = Polygon.Builder()
                .addVertex(new Point(800, 1000))
                .addVertex(new Point(1410, 1000))
                .addVertex(new Point(1410, 1350))
                .addVertex(new Point(800, 1350))
                .build();

        mFinalbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(drawMap){
            System.out.println(canvas.getWidth()+"  "+ canvas.getHeight());
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

    public void generateChurchPaths(boolean draw) {
        holderList.clear();
        pathSelected = -1;
        baseSelected = (draw) ? 0 : -1;
        if(draw) {
            Path beginMutualPath = new Path();
            beginMutualPath.moveTo(800, 1400);
            beginMutualPath.lineTo(800, 1175);
            holderList.add(new Holder(Color.BLACK, beginMutualPath));

            Path path1Church = new Path();
            path1Church.moveTo(800, 1175);
            path1Church.lineTo(550, 1175);
            path1Church.lineTo(550, 1320);
            path1Church.lineTo(230, 1320);
            path1Church.lineTo(230, 1170);
            path1Church.lineTo(440, 1170);
            path1Church.lineTo(440, 850);
            holderList.add(new Holder(Color.RED, path1Church));

            Path path2Church = new Path();
            path2Church.moveTo(800, 1175);
            path2Church.lineTo(800, 920);
            path2Church.lineTo(550, 920);
            path2Church.lineTo(550, 850);
            path2Church.lineTo(440, 850);
            holderList.add(new Holder(Color.BLUE, path2Church));

            Path endMutualPath = new Path();
            endMutualPath.moveTo(440, 850);
            endMutualPath.lineTo(440, 760);
            endMutualPath.lineTo(300, 760);
            endMutualPath.lineTo(300, 920);
            endMutualPath.lineTo(130, 920);
            endMutualPath.lineTo(130, 850);
            holderList.add(new Holder(Color.BLACK, endMutualPath));
        }
    }

    public void generateWindMillPath(boolean draw) {
        holderList.clear();
        baseSelected = (draw) ? 1 : -1;
        if(draw) {
            Path windMillPath = new Path();
            windMillPath.moveTo(800, 1400);
            windMillPath.lineTo(800, 920);
            windMillPath.lineTo(550, 920);
            windMillPath.lineTo(550, 690);
            windMillPath.lineTo(695, 690);
            windMillPath.lineTo(695, 540);
            windMillPath.lineTo(185, 540);
            windMillPath.lineTo(185, 210);
            windMillPath.lineTo(585, 210);
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
            hostelPath.moveTo(800, 1400);
            hostelPath.lineTo(800, 540);
            hostelPath.lineTo(1000, 540);
            hostelPath.lineTo(1000, 760);
            hostelPath.lineTo(1200, 760);
            hostelPath.lineTo(1200, 210);
            hostelPath.lineTo(1050, 210);
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
            beginMutualPath.moveTo(800, 1400);
            beginMutualPath.lineTo(800, 920);
            holderList.add(new Holder(Color.BLACK, beginMutualPath));

            Path casltePath = new Path();
            casltePath.moveTo(800, 920);
            casltePath.lineTo(800, 540);
            casltePath.lineTo(1000, 540);
            casltePath.lineTo(1000, 760);
            casltePath.lineTo(1200, 760);
            casltePath.lineTo(1200, 210);
            casltePath.lineTo(1565, 210);
            casltePath.lineTo(1565, 850);
            holderList.add(new Holder(Color.RED, casltePath));

            Path castlePath2 = new Path();
            castlePath2.moveTo(800, 920);
            castlePath2.lineTo(1410, 920);
            holderList.add(new Holder(Color.BLUE, castlePath2));

            Path castlePath3 = new Path();
            castlePath3.moveTo(800, 1175);
            castlePath3.lineTo(1000, 1175);
            castlePath3.lineTo(1000, 1330);
            castlePath3.lineTo(1150, 1330);
            castlePath3.lineTo(1150, 1090);
            castlePath3.lineTo(1260, 1090);
            castlePath3.lineTo(1260, 1330);
            castlePath3.lineTo(1370, 1330);
            castlePath3.lineTo(1370, 1090);
            castlePath3.lineTo(1450, 1090);
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