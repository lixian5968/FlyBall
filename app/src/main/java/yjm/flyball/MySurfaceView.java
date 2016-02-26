package yjm.flyball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by lixian on 2015/10/21.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    // //屏幕的宽度
    private static int screenW, screenH;
    private Thread th;
    //画笔
    private Paint paint;
    private Canvas canvas;
    //地板位置
    private int[] floor = new int[2];
    //地板宽度
    private int floor_width = 30;
    private boolean flag;
    //速度
    private int speed = 3;
    //鸟的坐标
    private int[] bird = new int[2];
    //宽度
    private int bird_width = 10;
    //初速度
    private int bird_v = 0;
    //加速度
    private int bird_a = 2;
    private int bird_vUp = -16;



    //墙
    private ArrayList<int[]> walls = new ArrayList<int[]>();
    private ArrayList<int[]> remove_walls = new ArrayList<int[]>();
    private int wall_w = 50;
    private int wall_h = 100;
    private int wall_step = 30;



    //访问SurfaceView的底层图形是通过SurfaceHolder接口来实现的
    // ，通过 getHolder()方法可以得到这个 SurfaceHolder对象。你应该实现
    SurfaceHolder MyHolder;

    public MySurfaceView(Context context) {
        super(context);
        MyHolder = this.getHolder();
        /**
         * SurfaceHolder.Callback中定义了三个接口方法：
         * 而SurfaceView.getHolder()方法就是用来返回SurfaceHolder对象以便访问surface
         * ,而holder.addCallback(this)是因为当前Activity实现了一个接口SurfaceHolder.Callback，
         * 所以this也是Callback的一个对象，
         * 在surface的各个生命周期(create change destroy)中会调用你重写的那三个方法。
         * 正因为你调用了holder.addCallback(this)，就将当前重写的三个方法与surface关联起来了
         */
        MyHolder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextSize(50);
        paint.setStyle(Paint.Style.STROKE); //空心
        //屏幕的宽度
        screenW = this.getWidth();
        screenH = this.getHeight();
        floor[0] = 0;
        floor[1] = screenH - screenH / 5;
        //鸟的坐标
        bird[0] = screenW / 3;
        bird[1] = screenH / 2;
        //dp to px
        floor_width = dp2px(15);
        speed = dp2px(3);
        bird_width = dp2px(10);
        bird_a = dp2px(2);
        bird_vUp = -dp2px(16);
        //墙清理
        walls.clear();
        wall_w = dp2px(45);
        wall_h = dp2px(100);
        wall_step = wall_w*4;


        flag = true;
        th = new Thread(this);
        th.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private int dp2px(float dp){
        int px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
        return px;
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


    public void myDraw() {
        try {
            canvas = MyHolder.lockCanvas();
            if (canvas != null) {
                //clear
                canvas.drawColor(Color.BLACK);
                //background

                //地板
                int floor_start = floor[0];
                Log.e("lx", floor_start + "");
                while (floor_start < screenW) {
                    canvas.drawLine(floor_start, floor[1], floor_start + floor_width, floor[1], paint);
                    floor_start += floor_width * 2;
                }


                //小鸟
                canvas.drawCircle(bird[0], bird[1], bird_width, paint);


                //                //wall
                for (int i = 0; i < walls.size(); i++) {
                    int[] wall = walls.get(i);

                    float[] pts = {
                            wall[0],0,wall[0],wall[1],
                            wall[0],wall[1]+wall_h,wall[0],floor[1],
                            wall[0]+wall_w,0,wall[0]+wall_w,wall[1],
                            wall[0]+wall_w,wall[1]+wall_h,wall[0]+wall_w,floor[1],
                            wall[0],wall[1], wall[0]+wall_w, wall[1],
                            wall[0],wall[1]+wall_h, wall[0]+wall_w, wall[1]+wall_h
                            //,wall[0],floor[1], wall[0]+wall_w, floor[1]
                    };
                    canvas.drawLines(pts, paint);

                    //canvas.drawRect(wall[0], 0, wall[0]+wall_w, wall[1], paint);
                    //canvas.drawRect(wall[0], wall[1]+wall_h, wall[0]+wall_w, floor[1], paint);
                }


            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (canvas != null)
                MyHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            bird_v = bird_vUp;
        }
        return true;
    }

    private int move_step = 0;
    private void logic() {

        //floor
        if (floor[0] <= -floor_width) {
            floor[0] += floor_width * 2;
        }
        floor[0] -= speed;


        //鸟
        bird_v += bird_a;
        bird[1] += bird_v;
        Log.e("bird_v", bird_v + "");
        if (bird[1] > floor[1] - bird_width) {
            bird[1] = floor[1] - bird_width;
        }

        //wall
        remove_walls.clear();
        for (int i = 0; i < walls.size(); i++) {
            int[] wall = walls.get(i);
            wall[0] -= speed;
            if(wall[0]<-wall_w){
                remove_walls.add(wall);
            }

        }
        //out of screen
        if(remove_walls.size()>0){
            walls.removeAll(remove_walls);
        }


        //new wall
        move_step += speed;
        if(move_step>wall_step){
            //最低 floor[1] -1.5个 最高wall_h
            int[] wall = new int[]{screenW, (int)(Math.random()*(floor[1]-2*wall_h)+0.5*wall_h)};
            walls.add(wall);
            move_step = 0;
        }

    }

}
