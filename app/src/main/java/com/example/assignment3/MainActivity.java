package com.example.assignment3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView acc,max, height, result, t;
    private Sensor sensor;
    private SensorManager sensorManager;
    private float x,y,z,accel, maxAccel;
    private float eGravity = SensorManager.GRAVITY_EARTH;
    private float MIN_ACC = 20;
    ObjectAnimator objectAnimator;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         imageView = findViewById(R.id.ball);



        // Create sensor manager
        sensorManager =(SensorManager)getSystemService(SENSOR_SERVICE);

        // Get the Accelerometer
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);


        acc = findViewById(R.id.txt_acc);
        max = findViewById(R.id.txt_max);
        height = findViewById(R.id.txtHeight);
        result = findViewById(R.id.txt_result);
        max.setVisibility(View.INVISIBLE);
        result.setVisibility(View.INVISIBLE);
        t = findViewById(R.id.txt_t);
       // t.setVisibility(View.INVISIBLE);



    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
        accel = (float)(Math.sqrt(x*x + y*y + z*z) - eGravity);
        acc.setText(Float.toString(accel));
        acc.setVisibility(View.INVISIBLE);
        if (accel > maxAccel && accel > 0){
            maxAccel = accel;
        }
        max.setText(Float.toString(maxAccel));
        if (accel > MIN_ACC){
            ThrowEvent(maxAccel);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void ThrowEvent(float acc) {
        height.setText("");
        float initialSpeed = acc;
        final float maxHeight = ((initialSpeed * 2) / (eGravity * 2));

             t.setVisibility(View.VISIBLE);

                ValueAnimator animator = new ValueAnimator();
                animator.setObjectValues(0, Math.round(maxHeight));
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        t.setText(String.valueOf(animation.getAnimatedValue()));
                    }
                });
                animator.setEvaluator(new TypeEvaluator<Integer>() {
                    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                        return Math.round(startValue + (endValue - startValue) * fraction);
                    }
                });
                animator.setDuration(500);
                animator.start();

        final Thread tid2 = new Thread() {
            @SuppressLint("SetTextI18n")
            public void run ()
            {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                height.setText(Float.toString(maxHeight) + " Meters");
                maxAccel = 0;
                accel = 0;

            }
        };
        playAnimation(maxHeight, 500);
        tid2.start();
    }

    public void playAnimation(float height, int dur) {
        objectAnimator = ObjectAnimator.ofFloat(imageView, "y", height);
        objectAnimator.setDuration(dur);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(objectAnimator);
        animatorSet.start();
       // animatorSet.pause();
        animatorSet.addListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                animation.removeListener(this);
                objectAnimator.reverse();
            }
        });
    }
}
