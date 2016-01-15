package com.matthewschwegler.myfirstgame;

import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Matt Schwegler on 1/11/2016.
 */
public abstract class GameObject {
    protected int x;
    protected int y;
    protected int dy;
    protected int dx;
    protected int width;
    protected int height;


    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    // Will help to detect object collision
    public Rect getRectangle()
    {
        return new Rect(x,y, x+width, y+height);
    }
}
