package com.matthewschwegler.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Matt Schwegler on 1/12/2016.
 */
public class TopBorder extends GameObject {

    private Bitmap image;

    public TopBorder(Bitmap res, int x, int y, int h)
    {
        height = h;
        width = 20; //width of brick

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
        try{canvas.drawBitmap(image, x, y, null);}
        catch(Exception e){};
    }
}
