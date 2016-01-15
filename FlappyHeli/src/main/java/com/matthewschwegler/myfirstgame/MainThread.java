package com.matthewschwegler.myfirstgame;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Matt Schwegler on 1/11/2016.
 */
public class MainThread extends Thread
{
    private int FPS = 30;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamepanel;
    private boolean running;
    public static Canvas canvas;

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel)
    {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamepanel = gamePanel;
    }

    @Override
    public void run()
    {
        long startTime;
        long timeMillies;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000/FPS; // in Milliseconds you want it to run through the game loop

        while(running) {
            startTime = System.nanoTime();
            canvas = null;

            //try locking the canvas for pixel editing
            try{
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder)
                {
                    this.gamepanel.update();
                    this.gamepanel.draw(canvas);
                }
            }catch(Exception e){}
            finally {
                if(canvas != null)
                {
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }catch (Exception e){e.printStackTrace();}
                }
            }
            timeMillies = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillies;

            try{
                this.sleep(waitTime);
            }catch(Exception e){}

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if(frameCount == FPS)
            {
                averageFPS = 1000/((totalTime/frameCount)/1000000);
                frameCount = 0;
                totalTime = 0;
                System.out.println(averageFPS);
            }
        }
    }

    public void setRunning(boolean b)
    {
        running = b;
    }
}
