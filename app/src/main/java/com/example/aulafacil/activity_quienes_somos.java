package com.example.aulafacil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_quienes_somos extends AppCompatActivity {

    Button sig, emp;
    ViewSwitcher viewSwitcher;
    ConstraintLayout quienes1, quienes2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quienes_somos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sig =  findViewById(R.id.siguiente);
        emp = findViewById(R.id.empezar);
        viewSwitcher = findViewById(R.id.switcher);
        quienes1 = findViewById(R.id.quienes1);
        quienes2 = findViewById(R.id.quienes2);

        sig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cambio de vista
                if(viewSwitcher.getCurrentView() == quienes1){
                    viewSwitcher.showNext();
                }

            }
        });

        emp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setClass(activity_quienes_somos.this, activity_subir_archivo.class);
                startActivity(intent);
            }
        });

    }
}