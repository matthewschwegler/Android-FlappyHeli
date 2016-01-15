package com.matthewschwegler.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;

/**
 * Created by Matt Schwegler on 1/11/2016.
 */
public class Player extends GameObject{
    private Bitmap spritesheet;
    private int score;
    private boolean up;
    private boolean playing;
    private Animation animation = new Animation();
    private long startTime;

    public Player(Bitmap res, int w, int h, int numFrames)
    {
        x = 100;
        y = GamePanel.HEIGHT/2;
        dy = 0;
        score = 0;
        height = h;
        width = w;

        //Number of frames that the player consistes of (3 helicopter frames)
        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;

        // This will take the helicopter image and chop it into 3 images that the animation
        // object will scroll through making it look like motion.
        for (int i = 0; i < image.length; i++)
        {
            image[i] = Bitmap.createBitmap(spritesheet, i*width,0,width,height);
        }

        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();
    }

    public void setUp(boolean b)
    {
        up = b;
    }

    public void update()
    {
        long elapsed = (System.nanoTime()-startTime)/1000000;
        if(elapsed > 100)
        {
            score++;
            startTime = System.nanoTime();
        }
        animation.update();

        if(up) {
            //acceleration
            dy -= 1;
        }else {
            dy += 1;
        }

        if(dy > 14) dy = 14;
        if(dy < -14) dy = -14;

        y += dy*2;
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(animation.getImage(),x,y,null);
    }

    public int getScore(){return score;}

    public boolean getPlaying(){return playing;}

    public void setPlaying(boolean b){playing = b;};

    public void resetDYA(){dy = 0;}

    public void resetScore(){score = 0;}
}
