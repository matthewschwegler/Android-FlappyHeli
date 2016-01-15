package com.matthewschwegler.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Matt Schwegler on 1/12/2016.
 */
public class BottomBorder extends GameObject{
    private Bitmap image;

    public BottomBorder(Bitmap res, int x, int y)
    {
        height = 200;
        width = 20;

        super.x = x;
        super.y = y;
        dx = GamePanel.BACKGROUND_MOVESPEED;

        image = Bitmap.createBitmap(res, 0, 0, width, height);
    }

    public void update()
    {
        x += dx;
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y, null);
    }
}
