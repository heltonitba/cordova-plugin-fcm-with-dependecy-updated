package com.gae.scaffolder.plugin;

import br.com.irricontrol.R;

import android.graphics.drawable.AnimationDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;

import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlertActivity extends Activity {

  Vibrator vibrator;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);

    String package_name = getApplication().getPackageName();
    setContentView(getApplication().getResources().getIdentifier("activity_alert", "layout", package_name));

    //requestWindowFeature(Window.FEATURE_NO_TITLE);

    String pivot_name = getIntent().getStringExtra("pivot_name");
    String reason_id = getIntent().getStringExtra("reason_id");
    String created = getIntent().getStringExtra("created");

    //nao ta encontrando a view daas coisas
    TextView pivo = findViewById(R.id.pivot);
    Log.e("PIVOT DA VIEW", pivo.getText().toString());
    pivo.setText(pivot_name.toString());

    TextView reason = findViewById(R.id.reason);
    reason.setText(reasonHandler(reason_id).toString());

    TextView horario = findViewById(R.id.time);
    horario.setText(created.toString());

    LinearLayout linearLayout = findViewById(R.id.layout);

    AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
    animationDrawable.setEnterFadeDuration(500);
    animationDrawable.setExitFadeDuration(1700);
    animationDrawable.start();

    this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

    Button okButton = findViewById(R.id.okButton);
    okButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        // TODO:
        // This function closes Activity Two
        // Hint: use Context's finish() method
        finishAndRemoveTask();
      }
    });

    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    if (Build.VERSION.SDK_INT >= 26) {
      vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));
    } else {
      vibrator.vibrate(2000);
    }

    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
    r.play();

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
}

