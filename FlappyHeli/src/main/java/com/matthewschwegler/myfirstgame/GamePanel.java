package com.matthewschwegler.myfirstgame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Matt Schwegler on 1/11/2016.
 */


public class GamePanel extends SurfaceView implements SurfaceHolder.Callback
{

    // Demensions of background therefore game.
    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    // background movespeed
    public static final int BACKGROUND_MOVESPEED = -5;

    private MainThread thread;
    private Background background;

    private Player player;

    private ArrayList<Smokepuff> smoke;
    private long smokeStartTimer;

    private ArrayList<Missle> missles;
    private long missleStartTime;
    private long missleElapsed;

    private ArrayList<TopBorder> topBorder;
    private ArrayList<BottomBorder> bottomBorder;
    private int maxBorderHeight;
    private int minBorderHeight;
    private boolean topDown = true;
    private boolean bottomDown = true;

    private boolean newGameCreated;

    //increases difficulty of the game, decrease to speed up difficulty progression
    private int progressDenom = 20;

    private Explosion explosion;
    private long starReset;
    private boolean reset;
    private boolean disapear;
    private boolean started;
    private int best;

    private Random rand = new Random();


    public GamePanel(Context context)
    {
        super(context);

        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);



        //make gamePanel focusable so it can handle events
        setFocusable(true);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int counter = 0;
        while (retry && counter < 1000) {
            counter++;
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        background = new Background(BitmapFactory.decodeResource(getResources(),R.drawable.grassbg1));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter),65,25,3);
        topBorder = new ArrayList<TopBorder>();
        bottomBorder = new ArrayList<BottomBorder>();
        smoke = new ArrayList<Smokepuff>();
        missles = new ArrayList<Missle>();
        smokeStartTimer = System.nanoTime();
        missleStartTime = System.nanoTime();

        thread = new MainThread(getHolder(), this);
        //we can safely start the game loop
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            //If first time touching the screen start game
            if(!player.getPlaying() && newGameCreated && reset){
                player.setPlaying(true);
                player.setUp(true);
            }
            if (player.getPlaying()){

                if(!started){
                    started = true;
                }
                reset = false;
                player.setUp(true);
            }
            return true;
        }

        //Player takes his hand off the screen
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            player.setUp(false);
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void update()
    {
        if(player.getPlaying()) {

            if(bottomBorder.isEmpty()){
                player.setPlaying(false);
                return;
            }
            if(topBorder.isEmpty()){
                player.setPlaying(false);
                return;
            }

            background.update();
            player.update();

            // calculate the threshold of height the border can have based on the score
            //max and min border height are update, and the border switched direction at max or min
            maxBorderHeight = 30+player.getScore()/progressDenom;
            //cap max border height so that borders can only take up a total of 1/2 screen
            if(maxBorderHeight > HEIGHT/4) {
                maxBorderHeight = HEIGHT / 4;
            }
            minBorderHeight = 5 + player.getScore()/progressDenom;

            //check  top collision
            for(int i = 0; i < topBorder.size(); i++) {
                if(collision(topBorder.get(i),player)){
                    player.setPlaying(false);
                }
            }

            //check for bottom collision
            for(int i = 0; i < topBorder.size(); i++){
                if(collision(bottomBorder.get(i),player)){
                    player.setPlaying(false);
                }
            }

            //update top border.
            this.updateTopBorder();

            //update bottom border.
            this.updateBottomBorder();

            //add missles on timer
            long missleElapsed = (System.nanoTime() - missleStartTime)/1000000;
            if(missleElapsed > (2000 - player.getScore()/4)) {

                //First missle strait down the middle
                if(missles.size() == 0)
                {
                    missles.add(new Missle(BitmapFactory.decodeResource(getResources(), R.drawable.
                            missile), WIDTH + 10, HEIGHT/2, 45, 15, player.getScore(), 13));
                } else {
                    missles.add(new Missle(BitmapFactory.decodeResource(getResources(), R.drawable.
                            missile), WIDTH + 10, (int)(rand.nextDouble()*(HEIGHT -
                            (maxBorderHeight*2)+maxBorderHeight)), 45, 15, player.getScore(), 13));
                }
                //reset time
                missleStartTime = System.nanoTime();
            }

            //Loop through missles and detect collisions and missles off the screen.
            for (int i = 0; i < missles.size(); i++)
            {
                missles.get(i).update();

                if(collision(missles.get(i),player))
                {
                    missles.remove(i);
                    player.setPlaying(false);
                    break;
                }

                if(missles.get(i).getX() < -100)
                {
                    missles.remove(i);
                    break;
                }
            }

            //Timer for smoke puffs
            long elapsed = (System.nanoTime() - smokeStartTimer)/1000000;
            if(elapsed > 120)
            {
                smoke.add(new Smokepuff(player.getX(), player.getY() + 10));
                smokeStartTimer = System.nanoTime();
            }

            //Remove smoke puffs no longer on the screen
            for (int i = 0; i <smoke.size(); i++)
            {
                smoke.get(i).update();
                if(smoke.get(i).getX() < -10)
                {
                    smoke.remove(i);
                }
            }
        }
        // Player is no longer playing (died)
        else {
            player.resetDYA();
            if(!reset){
                newGameCreated = false;
                starReset = System.nanoTime();
                reset = true;
                disapear = true;
                explosion = new Explosion(BitmapFactory.decodeResource(getResources(),
                        R.drawable.explosion), player.getX(), player.getY() -30, 100, 100, 25);
            }

            explosion.update();
            long resetElapsed = (System.nanoTime() - starReset)/1000000;

            if(resetElapsed > 2500 && !newGameCreated){
                newGame();
            }

            newGameCreated = false;
            if(!newGameCreated) {
                newGame();
            }
        }
    }

    // Detect Collisions between GameObjects
    public boolean collision(GameObject a, GameObject b)
    {
        if(Rect.intersects(a.getRectangle(), b.getRectangle()))
        {
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas)
    {
        //Scale the game to the phone
        final float scaleFactorX = getWidth() / (WIDTH*1.f);
        final float scaleFactorY = getHeight() / (HEIGHT*1.f);
        if(canvas != null) {
            //save state of canvas before scaling
            final int savedState = canvas.save();


            canvas.scale(scaleFactorX, scaleFactorY);
            background.draw(canvas);

            // Only draw when helicopter isnt exploding!
            if(!disapear) {
                player.draw(canvas);
            }

            // Draw every smoke puff in array
            for(Smokepuff sp: smoke)
            {
                sp.draw(canvas);
            }

            for(Missle m: missles)
            {
                m.draw(canvas);
            }

            //draw topborder
            for(TopBorder tb: topBorder)
            {
                tb.draw(canvas);
            }

            //draw bottomborder
            for(BottomBorder bb: bottomBorder)
            {
                bb.draw(canvas);
            }

            if(started){
                explosion.draw(canvas);
            }
            drawText(canvas);

            //after draw return to saved state, otherwise each call to draw would continually scale.
            canvas.restoreToCount(savedState);
        }
    }

    public void updateTopBorder()
    {
        //every 50 points, insert randomly placed top blocks that break the pattern
        if(player.getScore()%50 == 0){
            topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable
                    .brick),topBorder.get(topBorder.size()-1).getX() + 20, 0, (int)(rand.
                    nextDouble()*maxBorderHeight)+1));
        }

        for(int i = 0; i < topBorder.size(); i++){
            topBorder.get(i).update();
            //If Border off screen remove
            if(topBorder.get(i).getX() < -20){
                topBorder.remove(i);

                //remove element of arraylist, replace it by adding a new one
                //Calc topdown which dtermines the directino of moving border
                if(topBorder.get(topBorder.size()-1).getHeight() >= maxBorderHeight){
                    topDown = false;
                }
                if(topBorder.get(topBorder.size()-1).getHeight() <= minBorderHeight){
                    topDown = true;
                }
                // New border with smaller height
                if(topDown){
                    topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.
                            drawable.brick),topBorder.get(topBorder.size()-1).getX()+20, 0,
                            topBorder.get(topBorder.size()-1).getHeight()+1));
                } else {
                    topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.
                            drawable.brick),topBorder.get(topBorder.size()-1).getX()+20, 0,
                            topBorder.get(topBorder.size()-1).getHeight()-1));
                }

            }
        }
    }

    public void updateBottomBorder()
    {
        //Every 40 points, insert randomly placed bottom blocks that break pattern
        if(player.getScore() % 40 == 0){
            bottomBorder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(),R.drawable
                    .brick),topBorder.get(topBorder.size()-1).getX() + 20, (int)(rand.
                    nextDouble()*maxBorderHeight)));
        }

        //update bottom border
        for(int i = 0; i < bottomBorder.size(); i++)
        {
            bottomBorder.get(i).update();

            //if border is moving off screen, remove it and add a corresponding new one
            if(bottomBorder.get(i).getX() < -20){
                bottomBorder.remove(i);

                //determine if border will be moving up or down
                if(bottomBorder.get(bottomBorder.size()-1).getY() <= HEIGHT - maxBorderHeight ){
                    bottomDown = true;
                }
                if(bottomBorder.get(bottomBorder.size()-1).getY() >= HEIGHT - maxBorderHeight){
                    bottomDown = false;
                }

                if (bottomDown) {
                    bottomBorder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                    ), bottomBorder.get(bottomBorder.size() - 1).getX() + 20, bottomBorder.get(bottomBorder.size() - 1
                    ).getY() + 1));
                } else {
                    bottomBorder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                    ), bottomBorder.get(bottomBorder.size() - 1).getX() + 20, bottomBorder.get(bottomBorder.size() - 1
                    ).getY() - 1));
                }
            }
        }
    }

    public void drawText(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("DISTANCE: " + (player.getScore()*3), 10, HEIGHT - 10, paint);
        canvas.drawText("BEST: " + best, WIDTH-215, HEIGHT - 10, paint );

        if(!player.getPlaying() && newGameCreated && reset){
            Paint paint1 = new Paint();
            paint1.setTextSize(40);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PRESS TO START", WIDTH / 2 - 50, HEIGHT / 2, paint1);

            paint1.setTextSize(20);
            canvas.drawText("PRESS AND HOLD TO GO UP", WIDTH / 2 - 50, HEIGHT / 2 + 20, paint1);
            canvas.drawText("RELEASE TO GO DOWN", WIDTH/2-50, HEIGHT/2 + 40, paint1);
        }
    }

    public void newGame()
    {
        disapear = false;
        bottomBorder.clear();
        topBorder.clear();

        missles.clear();
        smoke.clear();

        minBorderHeight = 5;
        maxBorderHeight = 30;

        player.resetDYA();
        player.resetScore();
        player.setY(HEIGHT/2);

        if(player.getScore() > best){
            best = player.getScore();
        }

        //create initial borders
        for(int i = 0; i*20<WIDTH+40; i++)
        {
            if(i == 0){
                topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.
                        brick), i*20, 0, 10));
            } else {
                topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.
                        brick), i*20, 0, topBorder.get(i-1).getHeight()+1));
            }
        }

        //initial bottom border
        for(int i = 0; i*20 < WIDTH+40; i++)
        {
            if( i == 0){
                bottomBorder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(),R.
                        drawable.brick), i*20, HEIGHT - minBorderHeight));
            } else {
                bottomBorder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                        i * 20, bottomBorder.get(i - 1).getY() - 1));
            }
        }

        newGameCreated = true;
    }
}
