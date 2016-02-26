package yjm.flyball;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(new MySurfaceView(this));
//        setContentView(new MyView(this));

        setContentView(R.layout.activity_my_view);

    }


}
