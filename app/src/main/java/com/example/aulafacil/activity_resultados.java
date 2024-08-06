package com.example.aulafacil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class activity_resultados extends AppCompatActivity {
    private Connection base;
    List<Map<String, String>> tabla_final;
    Intent intent;
    Connection connection;

    Button regresar, salir, siguiente;

    ViewAnimator viewAnimator;


    String lugar1 = "", lugar2 = "", docente, horario1 = "", horario2 = "", asignatura, codigo1="", codigo2 = "";
    String path;
    String path_b;

    Map<String, Integer> facultades;

    int id1, id2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resultados);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        intent = getIntent();

        path_b = "http://192.168.219.130";

        encontrarTablaFinal();

        facultades = new HashMap<>();
        facultades.put("FACULTAD DE CIENCIAS INFORMÁTICAS (CIENCIAS INFORMÁTICAS)", 1);


        regresar = findViewById(R.id.anterior);
        siguiente = findViewById(R.id.siguiente);
        salir = findViewById(R.id.salir);
        viewAnimator = findViewById(R.id.viewAnimator);

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAnimator.showPrevious();
            }
        });

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAnimator.showNext();
            }
        });

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setClass(activity_resultados.this, linocypher.class);
                startActivity(intent);
            }
        });

        procesarTabla();



    }


    @SuppressLint("SetTextI18n")
    public void procesarTabla(){

        for (Map<String, String> filaMap : tabla_final) {
            lugar1 = ""; lugar2 = ""; docente=""; horario1 = ""; horario2 = ""; asignatura=""; codigo1=""; codigo2 = "";

            docente = filaMap.get("docente");
            asignatura = filaMap.get("asignatura");

            String [] texto = filaMap.get("horario").split(" {4}|;-");



            try {
                horario1 = texto[0];
                lugar1 = texto[1].substring(9).replace("\n","");
                id1 = facultades.get(lugar1);
                codigo1 = texto[2].substring(18,22).replace("-","");
                horario2 = texto[3];
                lugar2 = texto[4].substring(9).replace("\n", "");;
                id2 = facultades.get(lugar2);
                codigo2 = texto[5].substring(18,22).replace("-","");
            }catch (Exception e){
                e.printStackTrace();
            }


            ConstraintLayout constraintLayout = new ConstraintLayout(this);

            ScrollView scrollView = new ScrollView(this);


            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            TextView titulo = (TextView) TextView.inflate(this, R.layout.template_textview2, null);
            layout.addView(titulo);

            TextView asign = (TextView) TextView.inflate(this, R.layout.template_textview1, null);
            asign.setText("Asignatura: "+asignatura);
            layout.addView(asign);

            TextView docen = (TextView) TextView.inflate(this, R.layout.template_textview1, null);
            docen.setText(docente);
            layout.addView(docen);

            TextView hora1 = (TextView) TextView.inflate(this, R.layout.template_textview1, null);
            hora1.setText("Clase #1: " +horario1);
            layout.addView(hora1);

            TextView facult = (TextView) TextView.inflate(this, R.layout.template_textview2, null);
            facult.setText("Facultad :" +lugar1);
            layout.addView(facult);

            ImageView img_facult = (ImageView) ImageView.inflate(this, R.layout.template_imagenes, null);


            //QUERY 1
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<String> future = executorService.submit(new Callable<String>() {
                                                               @Override
                                                               public String call() throws Exception {

                                                                   try {
                                                                       connection = new BD_Conection().connection();
                                                                       if (connection == null) {
                                                                           System.out.println(" NO TOI CONECTADO DE NUEVO ");
                                                                           return null;
                                                                       } else {
                                                                           System.out.println("TOY CONECTADO " + connection.getMetaData());
                                                                       }

                                                                       PreparedStatement statement = connection.prepareStatement("SELECT imagen FROM facultades WHERE id = ?");
                                                                       statement.setInt(1, id1);


                                                                       ResultSet resultSet = statement.executeQuery();

                                                                       System.out.println(resultSet);
                                                                       if (resultSet.next()) {
                                                                           return resultSet.getString("imagen");
                                                                       }

                                                                       return null;

                                                                   } catch (Exception e) {
                                                                       e.printStackTrace();
                                                                       return null;
                                                                   }
                                                               }
                                                           });




            try {
                path = future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            Picasso.get().load(path_b+path).into(img_facult);
            layout.addView(img_facult);

            TextView aula1 = (TextView) TextView.inflate(this, R.layout.template_textview1, null);
            aula1.setText("⬇️⬇️⬇️ Aula #: " + codigo1 + " ⬇️⬇️⬇️");
            layout.addView(aula1);

            ImageView img_aula1 = (ImageView) ImageView.inflate(this, R.layout.template_imagenes, null);


            //QUERY 2

            Future<String> future2 = executorService.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {

                    try {
                        connection = new BD_Conection().connection();
                        if (connection == null) {
                            System.out.println(" NO TOI CONECTADO DE NUEVO ");
                            return null;
                        } else {
                            System.out.println("TOY CONECTADO " + connection.getMetaData());
                        }

                        PreparedStatement statement = connection.prepareStatement("SELECT imagen FROM aulas WHERE cod = ? AND facultad_id = ?");
                        statement.setString(1, codigo1);
                        statement.setInt(2, id1);


                        ResultSet resultSet = statement.executeQuery();

                        System.out.println(resultSet);
                        if (resultSet.next()) {
                            return resultSet.getString("imagen");
                        }

                        return null;

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            });


            try {
                path = future2.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            Picasso.get().load(path_b+path).into(img_aula1);
            layout.addView(img_aula1);


            if(!horario2.isEmpty()){
                TextView hora2 = (TextView) TextView.inflate(this, R.layout.template_textview1, null);
                hora2.setText("Clase #2: " + horario2);
                layout.addView(hora2);
            }
            if(!lugar2.isEmpty()) {
                TextView facult1 = (TextView) TextView.inflate(this, R.layout.template_textview1, null);
                facult1.setText("Facultad: " + lugar2);
                layout.addView(facult1);


                ImageView img_facult1 = (ImageView) ImageView.inflate(this, R.layout.template_imagenes, null);

                //QUERY 3
                Future<String> future3 = executorService.submit(new Callable<String>() {
                    @Override
                    public String call() throws Exception {

                        try {
                            connection = new BD_Conection().connection();
                            if (connection == null) {
                                System.out.println(" NO TOI CONECTADO DE NUEVO ");
                                return null;
                            } else {
                                System.out.println("TOY CONECTADO " + connection.getMetaData());
                            }

                            PreparedStatement statement = connection.prepareStatement("SELECT imagen FROM facultades WHERE id = ?");
                            statement.setInt(1, id2);


                            ResultSet resultSet = statement.executeQuery();

                            System.out.println(resultSet);
                            if (resultSet.next()) {
                                return resultSet.getString("imagen");
                            }

                            return null;

                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                });

                try {
                    path = future3.get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Picasso.get().load(path_b+path).into(img_facult1);
                layout.addView(img_facult1);

            }
            if(!codigo2.isEmpty()){
                TextView aula2 = (TextView) TextView.inflate(this, R.layout.template_textview1, null);
                aula2.setText("⬇️⬇️⬇️ Aula #: " + codigo2 + " ⬇️⬇️⬇️");
                layout.addView(aula2);

                ImageView img_aula2 = (ImageView) ImageView.inflate(this, R.layout.template_imagenes, null);
                //QUERY 4
                Future<String> future4 = executorService.submit(new Callable<String>() {
                    @Override
                    public String call() throws Exception {

                        try {
                            connection = new BD_Conection().connection();
                            if (connection == null) {
                                System.out.println(" NO TOI CONECTADO DE NUEVO ");
                                return null;
                            } else {
                                System.out.println("TOY CONECTADO " + connection.getMetaData());
                            }

                            PreparedStatement statement = connection.prepareStatement("SELECT imagen FROM aulas WHERE cod = ? AND facultad_id = ?");
                            statement.setString(1, codigo2);
                            statement.setInt(2, id2);


                            ResultSet resultSet = statement.executeQuery();

                            System.out.println(resultSet);
                            if (resultSet.next()) {
                                return resultSet.getString("imagen");
                            }

                            return null;

                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                });

                try {
                    path = future4.get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Picasso.get().load(path_b+path).into(img_aula2);
                layout.addView(img_aula2);

            }



            scrollView.addView(layout);
            constraintLayout.addView(scrollView);
            viewAnimator.addView(constraintLayout);


        }



    }


    public void encontrarTablaFinal(){

        List<Map<String, String>> tabla =(ArrayList<Map<String, String>>)intent.getSerializableExtra("datos");
        tabla_final = new ArrayList<>();

        Map<String, String> mapa_temp = new HashMap<>();
        for (Map<String, String> filaMap : tabla) {
            String docente = filaMap.get("docente");
            if(docente != null){
                if(!docente.equals("-")){
                    mapa_temp.put("docente", docente);
                }

            }

            String asignatura = filaMap.get("asignatura");
            if(asignatura != null){
                if(!asignatura.equals("-")){
                    mapa_temp.put("asignatura", asignatura);
                }
            }

            String horario = filaMap.get("horario");
            if (horario != null) {
                mapa_temp.put("horario", horario);
                tabla_final.add(mapa_temp);
                mapa_temp = new HashMap<>();
            }


        }
        for (Map<String, String> filaMap : tabla_final) {
            System.out.println(filaMap);
        }
    }

}