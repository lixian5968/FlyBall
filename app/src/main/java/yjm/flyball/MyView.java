package yjm.flyball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by lixian on 2015/10/22.
 */
public class MyView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    Context ct;

    private SurfaceHolder holder;


    // //屏幕的宽度
    private static int screenW, screenH;
    private Thread th;
    //画笔
    private Paint paint;
    private Paint bridpaint;
    private Paint passpaint;
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

    //墙出现的时机
    private int move_step = 0;


    //通过个数
    private int pass = 0;


    //地板位置
    private int[] passPoint = new int[2];

    //墙
    private ArrayList<int[]> walls = new ArrayList<int[]>();
    private ArrayList<int[]> remove_walls = new ArrayList<int[]>();
    private int wall_w = 50;
    private int wall_h = 100;
    //墙出现的限制
    private int wall_step = 30;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ct = context;


        holder = this.getHolder();
        holder.addCallback(this);

        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);
        paint.setTextSize(50);
        paint.setStyle(Paint.Style.STROKE); //空心


        bridpaint = new Paint();
        bridpaint.setColor(Color.RED);
        bridpaint.setAntiAlias(true);
        bridpaint.setTextSize(50);
        bridpaint.setStyle(Paint.Style.STROKE); //空心


        passpaint = new Paint();
        passpaint.setColor(Color.YELLOW);
        passpaint.setAntiAlias(true);
        passpaint.setTextSize(50);
        passpaint.setStyle(Paint.Style.STROKE); //空心


        //显示层数
//        holder.setFormat(PixelFormat.TRANSPARENT);
        //surfceview放置在顶层，即始终位于最上层
//        setZOrderOnTop(true);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyView(Context context) {
        super(context);


    }


    private static final int GAME_MENU = 0;
    private static final int GAMEING = 1;
    private static final int GAME_OVER = -1;
    private static int gameState = GAME_MENU;


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //屏幕的宽度
        screenW = this.getWidth();
        screenH = this.getHeight();
        passPoint[0] = screenW / 2;
        passPoint[1] = screenH / 5;
        initGame();
        flag = true;
        th = new Thread(this);
        th.start();

    }

    //游戏初始化
    private void initGame() {

        if (gameState == GAME_MENU || gameState == GAME_OVER) {
            floor[0] = 0;
            floor[1] = screenH - screenH / 5;
            //鸟的坐标
            bird[0] = screenW / 3;
            bird[1] = screenH / 3;
            //dp to px
            floor_width = dp2px(15);
            speed = dp2px(3);
            bird_width = dp2px(10);
            bird_a = dp2px(2);
            bird_v = 0;
            bird_vUp = -dp2px(16);
            //墙清理
            walls.clear();
            wall_w = dp2px(45);
            wall_h = dp2px(100);
            wall_step = wall_w * 4;
            pass = 0;
        }


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
    }

    @Override
    public void run() {

        while (flag) {
            long start = System.currentTimeMillis();
            draw();
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


    private void draw() {
        try {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                //clearholder
                canvas.drawColor(Color.BLACK);
//                canvas.drawColor(ct.getResources().getColor(R.color.myblack));


                //background
                int startFloor = floor[0];
                while (startFloor < screenW) {
                    canvas.drawLine(startFloor, floor[1], startFloor + floor_width, floor[1], paint);
                    startFloor += floor_width * 2;
                }


                //障碍物
                for (int i = 0; i < walls.size(); i++) {
                    int[] wall = walls.get(i);
                    float[] pts = {
                            wall[0], 0, wall[0], wall[1],
                            wall[0], wall[1] + wall_h, wall[0], floor[1],

                            wall[0] + wall_w, 0, wall[0] + wall_w, wall[1],
                            wall[0] + wall_w, wall[1] + wall_h, wall[0] + wall_w, floor[1],

                            wall[0], wall[1], wall[0] + wall_w, wall[1],
                            wall[0], wall[1] + wall_h, wall[0] + wall_w, wall[1] + wall_h,
                    };
                    canvas.drawLines(pts, paint);
                }

                //小鸟
                canvas.drawCircle(bird[0], bird[1], bird_width, bridpaint);
                //过的个数
                canvas.drawText(pass + "", passPoint[0], passPoint[1], passpaint);
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (canvas != null)
                holder.unlockCanvasAndPost(canvas);
        }
    }


    //动作
    private void logic() {

        switch (gameState) {
            case GAME_OVER:
                break;
            case GAMEING:
                //地板
                floor[0] -= speed;
                if (floor[0] < floor_width) {
                    floor[0] += floor_width * 2;
                }

                //阻碍物
                remove_walls.clear();
                for (int[] wallpoint : walls) {
                    wallpoint[0] -= speed;
                    if (wallpoint[0] < -wall_w) {
                        remove_walls.add(wallpoint);
                    } else if (bird[0] >= wallpoint[0] - bird_width &&
                            bird[0] <= wallpoint[0] + wall_w + bird_width &&
                            (bird[1] < wallpoint[1] + bird_width || bird[1] > wallpoint[1] + wall_h - bird_width)) {
                        //小鸟死了
                        gameState = GAME_OVER;
                    }
                    int mypassValue = bird[0] - wallpoint[0] - wall_w - bird_width;
                    if (mypassValue > 0 && mypassValue <= speed) {
                        pass++;
                    }

                }
                if (remove_walls != null && remove_walls.size() > 0) {
                    walls.removeAll(remove_walls);
                }
                move_step += speed;
                if (move_step > wall_step) {
                    int[] point = new int[]{screenW, (int) ((Math.random() * (floor[1] - 2 * wall_h)) + 0.5 * wall_h)};
                    walls.add(point);
                    move_step = 0;
                }


                //鸟
                bird_v += bird_a * 1; //速度
                bird[1] += bird_v * 1;
                if (bird[1] > (floor[1] - bird_width)) {
                    bird[1] = floor[1] - bird_width;
                    //gameover //小鸟死了
                    gameState = GAME_OVER;
                }

                //过的个数


                break;
            case GAME_MENU:

                break;
        }


    }

    private int dp2px(float dp) {
        int px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
        return px;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (gameState) {
                case GAME_OVER:
                    initGame();
                    gameState = GAMEING;
                    break;
                case GAMEING:
                    bird_v = bird_vUp;
                    break;
                case GAME_MENU:
                    gameState = GAMEING;

                    break;
            }


        }
        return true;
    }
}
