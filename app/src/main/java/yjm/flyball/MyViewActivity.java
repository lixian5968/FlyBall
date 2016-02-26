package yjm.flyball;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class MyViewActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    SurfaceView surfaceView;
    private Camera mCamera;
    private SurfaceHolder sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_view);
        surfaceView = (SurfaceView) findViewById(R.id.my_surfaceview);
        sh = surfaceView.getHolder();
        sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        sh.addCallback(this);
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        SetAndStartPreview(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        mCamera = Camera.open(0);
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size pre_size = parameters.getPreviewSize();
        Camera.Size pic_size = parameters.getPictureSize();
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        int camera_number = Camera.getNumberOfCameras();
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            Log.e("lx", "There are " + camera_number + " camera."
                    + "This is the Front Camera!");
        } else {
            Log.e("lx", "There are " + camera_number + " camera."
                    + "This is the Back Camera!");
        }
    }

    private Void SetAndStartPreview(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
