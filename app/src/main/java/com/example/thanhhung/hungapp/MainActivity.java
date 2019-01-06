package com.example.thanhhung.hungapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.thanhhung.hungapp.service.FloatingBubbleService;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission();
        }
        findViewById(R.id.buttonON).setOnClickListener(this);
    }

    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.buttonON) {
//            Toast.makeText(this, "ok", Toast.LENGTH_LONG).show();
            if (Settings.canDrawOverlays(this)) {
                startService(new Intent(this, FloatingBubbleService.class));
                finish();
            } else {
                askPermission();
            }
        }
    }

}
