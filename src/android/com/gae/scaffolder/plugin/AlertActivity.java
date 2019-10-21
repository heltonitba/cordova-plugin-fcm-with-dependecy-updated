package com.gae.scaffolder.plugin;
import br.com.irricontrol.R;

import android.graphics.Color;
import android.media.AudioManager;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;

public class AlertActivity extends Activity {

  Vibrator vibrator;
  MediaPlayer mMediaPlayer;
  Window win;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    String package_name = getApplication().getPackageName();
    setContentView(getApplication().getResources().getIdentifier("activity_alert", "layout", package_name));


    LinearLayout linearLayout = findViewById(R.id.alert_layout);
    AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
    animationDrawable.setEnterFadeDuration(500);
    animationDrawable.setExitFadeDuration(1700);
    animationDrawable.start();


    this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    if (Build.VERSION.SDK_INT >= 26) {
      vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));
    } else {
      vibrator.vibrate(2000);
    }

  }
}
