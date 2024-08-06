package com.example.aulafacil;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;
import java.util.Timer;

public class linocypher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_linocypher);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String [] frases = new String[]{getResources().getString(R.string.frasesCypher)
        , getResources().getString(R.string.frasesCypher2)};
        TextView texto = findViewById(R.id.texto);
        float X = texto.getX();

        texto.setText(frases[0]);
        texto.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation));


        new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                texto.setX(X);
                texto.setText(frases[1]);
                texto.startAnimation(AnimationUtils.loadAnimation(linocypher.this, R.anim.animation));

            }
        }.start();


        CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                texto.setAlpha(texto.getAlpha() - 0.05f);


            }

            @Override
            public void onFinish() {
                Intent intent = new Intent();
                intent.setClass(linocypher.this, Logo.class);
                startActivity(intent);

            }
        }.start();


    }
}