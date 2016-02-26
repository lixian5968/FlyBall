# FlyBall
目标： 1：在SurfaceView 实现球的运动（实现） 2：游戏背景能否调用 摄像头拍摄的图片背景？（未实现 有看到麻烦@下我）   SurfaceView 通过实现  SurfaceHolder.Callback, Runnable， 实现 线程   th = new Thread(this);         th.start(); 在这个线程中实现   draw(); logic(); 画图与逻辑的实现 运动效果：某个点或者线，x轴坐标一直减少表示左移动，具体逻辑 看代码 可以把 鸟，地板，墙 分别实现之后，在判断是否碰触  2：通过直接调用Camera  未能实现 背景效果。help
