package com.example.thanhhung.hungapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.thanhhung.hungapp.service.FloatingBubbleService;
import com.example.thanhhung.hungapp.utils.ImageUtils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    private static final int READ_EXTERNAL_STORAGE_CODE = 9996;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if( !Settings.canDrawOverlays(this)) {
                askDrawOverlaysPermission();
            }
            if ( !isCanReadExternalStorage()) {
                askReadExternalStoragePermission();
            }
        }
        System.out.println(ImageUtils.getAllImagePaths(this));
        findViewById(R.id.buttonON).setOnClickListener(this);
    }

    private void askDrawOverlaysPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    private boolean isCanReadExternalStorage() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void askReadExternalStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(this,
                    "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.",
                    Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_CODE);
        }
    }


    public void onClick(View v) {
        if (v.getId() == R.id.buttonON) {
//            Toast.makeText(this, "ok", Toast.LENGTH_LONG).show();
            if (Settings.canDrawOverlays(this)) {
                startService(new Intent(this, FloatingBubbleService.class));
                finish();
            } else {
                askDrawOverlaysPermission();
            }
        }
    }

}
