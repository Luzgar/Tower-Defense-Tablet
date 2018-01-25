package com.example.kevinduglue.towerdefensetablet.map;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by kevinduglue on 21/01/2018.
 */

public class MapDrawer {

    private int mapId;
    private int width;
    private int height;
    private Canvas canvas;

    public MapDrawer(int width, int height, int mapId, Canvas canvas) {
        this.mapId = mapId;
        this.width = width;
        this.height = height;
        this.canvas = canvas;
    }

    public void getDrawMap(int mapId, int baseId) {
        canvas.drawColor(Color.WHITE);
        if(baseId == -1) {
            getDrawMapNoBase();
        } else if (baseId == 0) {
            getDrawMapCastle();
        }
    }

    private void getDrawMapNoBase() {
        Paint paint = new Paint();
        paint.setColor(Color.rgb(0, 0, 0));
        paint.setStrokeWidth(10);
        int startx = 50;
        int starty = height/2;
        int endx = width;
        int endy = height/2;
        canvas.drawLine(startx, starty, width/2, height/2-200, paint);
        canvas.drawLine(width/2, height/2-200, endx-50, endy, paint);

        paint = new Paint();
        paint.setColor(Color.rgb(0, 0, 0));
        paint.setStrokeWidth(10);
        canvas.drawLine(startx, starty, width/2, height/2+200, paint);
        canvas.drawLine(width/2, height/2+200, endx-50, endy, paint);
    }

    private void getDrawMapCastle() {
        Paint paint = new Paint();
        paint.setColor(Color.rgb(3, 169, 244));
        paint.setStrokeWidth(10);
        int startx = 50;
        int starty = height/2;
        int endx = width;
        int endy = height/2;
        canvas.drawLine(startx, starty, width/2, height/2-200, paint);
        canvas.drawLine(width/2, height/2-200, endx-50, endy, paint);

        paint = new Paint();
        paint.setColor(Color.rgb(156, 39, 176));
        paint.setStrokeWidth(10);
        canvas.drawLine(startx, starty, width/2, height/2+200, paint);
        canvas.drawLine(width/2, height/2+200, endx-50, endy, paint);
    }

}
