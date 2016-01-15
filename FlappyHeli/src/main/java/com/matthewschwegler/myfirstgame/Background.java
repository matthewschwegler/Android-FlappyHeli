package com.matthewschwegler.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.BitSet;

/**
 * Created by Matt Schwegler on 1/11/2016.
 */
public class Background {
    private Bitmap image;
    private int x, y, dx;

    public Background(Bitmap res) {
        image = res;
        dx = GamePanel.BACKGROUND_MOVESPEED;
    }

    public void update(){
        x+=dx;
        //reset the image when it goes off the screen
        if(x < -GamePanel.WIDTH) {
            x = 0;
        }
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y, null);
        // If X is off the screen draw a second image to fill empty space
        if(x<0)
        {
            canvas.drawBitmap(image, x+GamePanel.WIDTH, y, null);
        }
    }


}
