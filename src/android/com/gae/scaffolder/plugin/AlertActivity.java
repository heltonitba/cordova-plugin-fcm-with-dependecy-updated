package com.gae.scaffolder.plugin;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.com.irricontrol.R;
import java.io.IOException;
import android.view.View;

public class AlertActivity extends Activity implements View.OnClickListener {

  Vibrator vibrator;
  Context context;
  MediaPlayer mp;
  AudioManager mAudioManager;
  int userVolume;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    // removendo o header com o título do app (default do android)
    requestWindowFeature(Window.FEATURE_NO_TITLE);

    super.onCreate(savedInstanceState);

    String package_name = getApplication().getPackageName();
    setContentView(getApplication().getResources().getIdentifier("activity_alert", "layout", package_name));

    // pegando os dados que foram passados do MyFirebaseMessagingService
    String pivot_name = getIntent().getStringExtra("pivot_name");
    String reason_id = getIntent().getStringExtra("reason_id");

    // tratando a string created para mostrar na View apenas horário na forma HH:mm
    String created = getIntent().getStringExtra("created");
    String[] parts = created.split("T");
    created = parts[1];
    created = created.substring(0, Math.min(created.length(), 5));

    // setando informações do pivô na view
    TextView pivo = findViewById(R.id.pivot);
    Log.e("PIVOT DA VIEW", pivo.getText().toString());
    pivo.setText(pivot_name.toString());

    // setando informações da razão do problema do pivô na view
    TextView reason = findViewById(R.id.reason);
    reason.setText(reasonHandler(reason_id).toString());

    // mostrando o hoŕario HH:MM no qual ocorreu o envio da notificação pelo backend
    TextView horario = findViewById(R.id.time);
    horario.setText(created.toString());

    // arrumando animação de background da view
    LinearLayout linearLayout = findViewById(R.id.layout);

    AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
    animationDrawable.setEnterFadeDuration(500);
    animationDrawable.setExitFadeDuration(1700);
    animationDrawable.start();

    // flags necessárias para que a view apareça na tela do usuário em qualquer
    // circunstâncias
    this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

    // configurando o botão para gerenciar funções que previamente serão chamadas
    // por ele
    Button okButton = findViewById(R.id.okButton);
    okButton.setOnClickListener(this);

    // ativar vibração, existe um IF, pois dependendo do API Level do Android do
    // usuário isso pe feito de forma diferente
    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    if (Build.VERSION.SDK_INT >= 26) {
      vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));
    } else {
      vibrator.vibrate(2000);
    }

    AlarmController(this);
    this.playSound();

  }

  @Override
  public void onClick(View v) {

    // TODO:
    // This function closes Activity Two
    // Hint: use Context's finish() method
    this.stopSound();
    /*
     * função própria do Android para fechar e remover tarefas que estão nesta
     * activity outras formas foram testadas porém esta foi a única que fechou por
     * completo a atividade, outras acabavam por minimizá-la apenas em circustâncias
     * adversas.
     */
    finishAndRemoveTask();
  }

  public String reasonHandler(String reason) {

    Log.e("REASON", reason);

    if (reason.equals("242")) {

      return "FATOR DESCONHECIDO";

    } else if (reason.equals("243")) {

      return "FIM DA AUTO-REVERSÃO";

    } else if (reason.equals("244")) {

      return "FIM DA PROGRAMAÇÃO DIÁRIA";

    } else if (reason.equals("245")) {

      return "EXECUTANDO AUTO-REVERSÃO";

    } else if (reason.equals("246")) {

      return "FORA DA FAIXA DE TENSÃO";

    } else if (reason.equals("247")) {

      return "HORÁRIO DE PICO";

    } else if (reason.equals("248")) {

      return "FALTA DE PRESSÃO";

    } else if (reason.equals("249")) {

      return "PARADO ATRAVÉS DA WEB";

    } else if (reason.equals("250")) {

      return "TEMPO DE PRESSURIZAÇÃO EXCEDIDO";

    } else if (reason.equals("251")) {

      return "RELÉ TÉRMICO ACIONADO";

    } else if (reason.equals("252")) {

      return "FIM DA IRRIGAÇÃO PROGRAMADA";

    } else if (reason.equals("253")) {

      return "FALTA DE ENERGIA";

    } else if (reason.equals("254")) {

      return "BOTÃO PARAR PRESSIONADO";

    } else if (reason.equals("255")) {

      return "DESALINHAMENTO";

    }
    return "NA";
  }

  public void AlarmController(Context c) { // constructor for my alarm controller class
    this.context = c;
    mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    // remeber what the user's volume was set to before we change it.
    userVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);

    mp = new MediaPlayer();
  }

  public void playSound() {

    Uri alarmSound = null;
    Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

    alarmSound = ringtoneUri;

    try {

      if (!mp.isPlaying()) {
        mp.setDataSource(context, alarmSound);
        mp.setAudioStreamType(AudioManager.STREAM_ALARM);
        mp.setLooping(true);
        mp.prepare();
        mp.start();
      }

    } catch (IOException e) {
      Log.e("ERROR", "Your alarm sound was unavailable.");

    }
    // set the volume to what we want it to be. In this case it's max volume for the
    // alarm stream.
    mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
        mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), AudioManager.FLAG_PLAY_SOUND);

  }

  public void stopSound() {
    // reset the volume to what it was before we changed it.
    mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, userVolume, AudioManager.FLAG_PLAY_SOUND);
    mp.stop();
    mp.reset();

  }

  public void releasePlayer() {
    mp.release();
  }

}

