package com.example.aulafacil;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class Logo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("Toque de pantalla" + " " +  event.getAction() + " " + findViewById(R.id.main));

            Intent intent = new Intent();
            intent.setClass(this, activity_quienes_somos.class);
            startActivity(intent);

        return true;
    }
}