package com.matthewschwegler.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Matt Schwegler on 1/14/2016.
 */

//Doesn't need to extend GameObject because no collision detection
public class Explosion {
    private int x;
    private int y;
    private int width;
    private int height;
    private int row;
    private Animation animation = new Animation();
    private Bitmap spritesheet;

    public Explosion(Bitmap res, int x, int y, int w, int h, int numFrames)
    {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;

        Bitmap[] image = new Bitmap[numFrames];

        spritesheet = res;

        //image has columns and rows to pull out explosion animation
        for(int i = 0; i < image.length; i++)
        {
            if(i%5 == 0 && i > 0){
                row++;
            }
            image[i] = Bitmap.createBitmap(spritesheet, (i-(5*row))*width, row*height, width, height);

            animation.setFrames(image);
            animation.setDelay(10);
        }
    }

    public void draw(Canvas canvas)
    {
        if(!animation.isPlayedOnce()){
            canvas.drawBitmap(animation.getImage(), x, y, null);
        }

    }

    public void update()
    {
        if(!animation.isPlayedOnce()){
            animation.update();
        }
    }

    public int getHeight(){return height;}


}
