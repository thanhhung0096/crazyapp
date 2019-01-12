package com.example.thanhhung.hungapp.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.example.thanhhung.hungapp.R;
import com.example.thanhhung.hungapp.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Thanh Hung on 1/4/2019.
 */

public class FloatingBubbleService extends Service {
    private final int  DESTROY_THRESHOLD = 20;

    private WindowManager mWindowManager;
    private View mFloatingView;
    private GestureDetector gestureDetector;
    public FloatingBubbleService(){}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void onCreate() {
        super.onCreate();
        gestureDetector = new GestureDetector(this, new SingleTapConfirm());
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.widget_floating_bubble, null);
        setDefaultViewImage(mFloatingView);


        //setting the layout parameters
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        //assert mWindowManager != null;
        mWindowManager.addView(mFloatingView, params);
        mFloatingView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    pullDownNotification();
                    changeViewRandom(mFloatingView);
                    return true;
                } else {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            initialX = params.x;
                            initialY = params.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            params.x = initialX + (int)(event.getRawX() - initialTouchX);
                            params.y = initialY + (int)(event.getRawY() - initialTouchY);
                            mWindowManager.updateViewLayout(mFloatingView, params);
                            return true;

                        case MotionEvent.ACTION_UP:
                            moveToEdge(params, params.x, params.y);
                            mWindowManager.updateViewLayout(mFloatingView, params);
                            int yourScreenheigh = Resources.getSystem().getDisplayMetrics().heightPixels;
                            if (params.y > (yourScreenheigh/2 - DESTROY_THRESHOLD)) {
                                stopSelf();
                            }
                    }
                }
                return false;
            }
        });

    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

    public void onDestroy(){
        super.onDestroy();
        mWindowManager.removeView(mFloatingView);
    }

    public void pullDownNotification() {
        try {
            @SuppressLint("WrongConstant") Object sbservice = getSystemService("statusbar");
            @SuppressLint("PrivateApi") Class<?> statusbarManager = Class.forName( "android.app.StatusBarManager" );
            Method showsb = statusbarManager.getMethod( "expandNotificationsPanel" );
            showsb.invoke( sbservice );

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void moveToEdge(WindowManager.LayoutParams params, int current_x, int current_y) {
        int yourScreenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        //int yourScreenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        params.x = current_x < 0 ? -(yourScreenWidth/2) : (yourScreenWidth/2);
        params.y = current_y;
    }

    private void setDefaultViewImage(View view){
        CircleImageView myView = (CircleImageView)view;
        Drawable myDrawable = getResources().getDrawable(R.drawable.ava, null);
        Bitmap bitmapFromDrawable = ((BitmapDrawable)myDrawable).getBitmap();
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapFromDrawable,
                ImageUtils.ICON_WIDTH, ImageUtils.ICON_HEIGH, true);
        myView.setImageBitmap(resizedBitmap);
    }

    private void changeViewRandom(View view){
        CircleImageView myView = (CircleImageView)view;
        String imagePathPrefix = "file://";
        try {
            String imgPath = imagePathPrefix + ImageUtils.getRandomImageFromGallery(this);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                    Uri.parse(new File(imgPath).toString()));
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, ImageUtils.ICON_WIDTH,
                    ImageUtils.ICON_HEIGH, true);
            myView.setImageBitmap(resizedBitmap);
        } catch (IOException e) {
            setDefaultViewImage(view);
        }
    }
}