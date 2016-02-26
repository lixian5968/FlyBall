package yjm.flyball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GameBirdSurfaceView extends SurfaceView implements Callback, Runnable {

    private SurfaceHolder sfh;
    private Paint paint;

    private Thread th;
    private boolean flag;

    private Canvas canvas;


    // //屏幕的宽度
    private static int screenW, screenH;

    private static final int GAME_MENU = 0;
    private static final int GAMEING = 1;
    private static final int GAME_OVER = -1;

    private static int gameState = GAME_MENU;

    //地板位置
    private int[] floor = new int[2];
    //地板宽度
    private int floor_width = 15;

    private int speed = 3;

    private int[] level = new int[2];
    private int level_value = 0;

    private int[] bird = new int[2];
    private int bird_width = 10;
    private int bird_v = 0;
    private int bird_a = 2;
    private int bird_vUp = -16;


    private ArrayList<int[]> walls = new ArrayList<int[]>();
    private ArrayList<int[]> remove_walls = new ArrayList<int[]>();
    private int wall_w = 50;
    private int wall_h = 100;

    private int wall_step = 30;

    public GameBirdSurfaceView(Context context) {

        super(context);
        sfh = this.getHolder();
        sfh.addCallback(this);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        /*
        　　给Paint加上抗锯齿标志。然后将Paint对象作为参数传给canvas的绘制方法。
　　paint.setAntiAlias(true);
         */
        paint.setAntiAlias(true);
        paint.setTextSize(50);
        paint.setStyle(Style.STROKE); //空心
        /**将控件设置成可获取焦点状态，默认是无法获取焦点的，只有设置成true，才能获取控件的点击事件*/
        setFocusable(true);

        /**
         * setFocusable与setFocusableInTouchMode区别
         setFocusable这个是用键盘是否能获得焦点
         setFocusableInTouchMode这个是触摸是否能获得焦点
         */
        setFocusableInTouchMode(true);

        /**
         * “请将屏幕一直保持为开启状态,以便校准.”
         */
        this.setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //屏幕的宽度
        screenW = this.getWidth();
        screenH = this.getHeight();

        //System.out.println("=======screenW="+screenW+"=======screenH="+screenH);

        initGame();

        flag = true;

        th = new Thread(this);
        th.start();
    }

    private void initGame() {

        if (gameState == GAME_MENU) {

            floor[0] = 0;
            floor[1] = screenH - screenH/5;
            //强的y轴坐标

            level[0] = screenW/2;
            level[1] = screenH/5;

            level_value = 0;

            bird[0] = screenW/3;
            bird[1] = screenH/2;

            walls.clear();

            //dp to px
            floor_width = dp2px(15);

            speed = dp2px(3);

            bird_width = dp2px(10);
            bird_a = dp2px(2);
            bird_vUp = -dp2px(16);

            wall_w = dp2px(45);
            wall_h = dp2px(100);

            wall_step = wall_w*4;
        }
    }

    private int dp2px(float dp){
        int px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
        return px;
    }

    public void myDraw() {
        try {
            canvas = sfh.lockCanvas();
            if (canvas != null) {
                //clear
                canvas.drawColor(Color.BLACK);
                //background
                int floor_start = floor[0];
                while(floor_start<screenW){
                    canvas.drawLine(floor_start, floor[1], floor_start+floor_width, floor[1], paint);
                    floor_start += floor_width*2;
                }

//                //wall
//                for (int i = 0; i < walls.size(); i++) {
//                    int[] wall = walls.get(i);
//
//                    float[] pts = {
//                            wall[0],0,wall[0],wall[1],
//                            wall[0],wall[1]+wall_h,wall[0],floor[1],
//                            wall[0]+wall_w,0,wall[0]+wall_w,wall[1],
//                            wall[0]+wall_w,wall[1]+wall_h,wall[0]+wall_w,floor[1],
//                            wall[0],wall[1], wall[0]+wall_w, wall[1],
//                            wall[0],wall[1]+wall_h, wall[0]+wall_w, wall[1]+wall_h
//                            //,wall[0],floor[1], wall[0]+wall_w, floor[1]
//                    };
//                    canvas.drawLines(pts, paint);
//
//                    //canvas.drawRect(wall[0], 0, wall[0]+wall_w, wall[1], paint);
//                    //canvas.drawRect(wall[0], wall[1]+wall_h, wall[0]+wall_w, floor[1], paint);
//                }
//
//                //bird
//                canvas.drawCircle(bird[0], bird[1], bird_width, paint);
//
//                //level
//                canvas.drawText(String.valueOf(level_value), level[0], level[1], paint);

            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (canvas != null)
                sfh.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN){

            switch (gameState) {
                case GAME_MENU:
                    gameState = GAMEING;
//					bird_v = bird_vUp;
//					break;
                case GAMEING:
                    bird_v = bird_vUp;
                    break;
                case GAME_OVER:
                    //bird down
                    if(bird[1] >= floor[1] - bird_width){
                        gameState = GAME_MENU;
                        initGame();
                    }

                    break;
            }
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            GameBirdActivity.instance.finish();
            System.exit(0);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private int move_step = 0;
    private void logic() {

        switch (gameState) {
            case GAME_MENU:

                break;
            case GAMEING:

                //bird
                bird_v+=bird_a;
                bird[1] += bird_v;
                if(bird[1] > floor[1] - bird_width){
                    bird[1] = floor[1] - bird_width;
                    gameState = GAME_OVER;
                }
                //top
//				if(bird[1]<=bird_width){
//					bird[1]=bird_width;
//				}

                //floor
                if(floor[0] < -floor_width){
                    floor[0] += floor_width*2;
                }
                floor[0] -= speed;

                //wall
                remove_walls.clear();
                for (int i = 0; i < walls.size(); i++) {
                    int[] wall = walls.get(i);
                    wall[0] -= speed;
                    if(wall[0]<-wall_w){
                        remove_walls.add(wall);
                    }else if(wall[0]-bird_width<=bird[0] && wall[0]+wall_w+bird_width>=bird[0]
                            && (bird[1]<=wall[1]+bird_width || bird[1]>=wall[1]+wall_h-bird_width)){
                        gameState = GAME_OVER;
                    }

                    int pass = wall[0]+wall_w+bird_width-bird[0];
                    if(pass<0 && -pass<=speed){
                        level_value++;
                    }
                }
                //out of screen
                if(remove_walls.size()>0){
                    walls.removeAll(remove_walls);
                }

                //new wall
                move_step += speed;
                if(move_step>wall_step){
                    int[] wall = new int[]{screenW, (int)(Math.random()*(floor[1]-2*wall_h)+0.5*wall_h)};
                    walls.add(wall);
                    move_step = 0;
                }
                break;
            case GAME_OVER:
                //bird
                if(bird[1] < floor[1] - bird_width){
                    bird_v+=bird_a;
                    bird[1] += bird_v;
                    if(bird[1] >= floor[1] - bird_width){
                        bird[1] = floor[1] - bird_width;
                    }
                }else{
                    GameBirdActivity.instance.showMessage(level_value);
                    gameState = GAME_MENU;
                    initGame();
                }
                break;

        }
    }

    @Override
    public void run() {
        while (flag) {
            long start = System.currentTimeMillis();
            myDraw();
            logic();
            long end = System.currentTimeMillis();
            try {
                if (end - start < 50) {
                    Thread.sleep(50 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
    }
}
