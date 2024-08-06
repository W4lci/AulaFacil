package com.example.aulafacil;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewAnimator;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;


import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.text.TextPosition;

import java.io.File;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class activity_subir_archivo extends AppCompatActivity {

    //Esta activity si voy a documentar ya que será la activity con más contenido

    //Botones
    Button sig1, importar, sig3, regresar;
    ImageButton  sigFinal;

    //Views
    ViewAnimator viewAnimator;

    //Imagen de preview
    ImageView prevImg;

    //Barra de progreso
    ProgressBar barraProgreso;

    //cosas del archivo
    Uri uri;
    String uriString;
    File archivo;
    String path;

    //PDF
    PDFTextStripper pdfTextStripper;
    PDDocument documento;


    //Extraer datos
    final double [] cord_asig = new double[]{32.4, 180.72};
    final double [] cord_nivel = new double[]{182.88, 208};
    final double [] cord_paral = new double[]{210, 240};
    final double [] cord_cred = new double[]{242, 269};
    final double [] cord_docen = new double[]{271, 420};
    final double [] cord_dep = new double[]{422, 502};
    final double [] cord_hora = new double[]{504, 810};

    final int cord_y1 = 223;
    final int cord_y2 = 465;

    final double tolerancia_cambio = 15;

    int progreso = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subir_archivo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        PDFBoxResourceLoader.init(getApplicationContext());




        viewAnimator = findViewById(R.id.switcher);

        sig1 = findViewById(R.id.sig1);
        importar = findViewById(R.id.sig2);
        sig3 = findViewById(R.id.sig3);
        regresar = findViewById(R.id.regresar);
        sigFinal = findViewById(R.id.sig4);
        barraProgreso = findViewById(R.id.barraProg);

        prevImg = findViewById(R.id.renderizar);

        //El orden de los listeners va en orden de como se presentan al usuario
        //Sig1 es el primer botón de siguiente y así sucesivamente

        //El primer view es informativo, al dar siguiente solo cambia de vista
        sig1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAnimator.showNext();
            }
        });

        //Para importar archihvo
        importar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setAction(Intent.ACTION_GET_CONTENT); //Hacemos que in intent sea de conseguir contenido
                intent.setType("application/pdf"); //Ese contenido tiene que ser pdf
                startActivityForResult(intent, 55); //Aunque startActivityForResult esta drepecated, debido a la limitación de tiempo voy a lo seguro
            }
        });

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAnimator.showPrevious();
            }
        });

        sig3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAnimator.showNext();
                analizar();
            }
        });

    }

    public void preview(){ //Acá vamos a mostrar la preview/previsualizacion del documento antes de importar

        try {
            RealFilePath filePath = new RealFilePath(this);
            path = filePath.getPath(uri);
            // String[] ruta = aTratar.getAbsolutePath().split(":"); //Aqui ya obtenemos el path en el index 1
            archivo = new File(path); //Actualizamos el File
            if(!archivo.exists()){
                Toast.makeText(this, "No existe el archivo?", Toast.LENGTH_SHORT).show();
                viewAnimator.showPrevious();
                return;
            }

            PDFRenderer pdfRenderer = new PDFRenderer(PDDocument.load(new File(path)));
            prevImg.setImageBitmap(pdfRenderer.renderImage(0));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    public void analizar() {

        barraProgreso.setProgress((int)progreso);
        try {

            documento = PDDocument.load(archivo);
            int longintud = new PDFTextStripper().getText(documento).split("").length;
            barraProgreso.setMax(longintud);

            List<Map<String, String>> tabla = new ArrayList<>();


            PDFTextStripper stripper = new PDFTextStripper() {


                Map<String, String> fila = new HashMap<>();
                StringBuilder asignatura = new StringBuilder();
                StringBuilder docente = new StringBuilder();
                StringBuilder horario = new StringBuilder();
                double lastY = -1;
                boolean isSameLine = true;

                @Override
                protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                    super.writeString(text, textPositions);


                    // Procesa todas las posiciones de texto
                    for (TextPosition texto : textPositions) {
                        double x = texto.getXDirAdj();
                        double y = texto.getYDirAdj();
                        progreso  += 1;



                        // Verifica si el texto está dentro del rango de coordenadas Y especificado
                        if (y >= cord_y1 && y <= cord_y2) {
                            // Determina si estamos en la misma línea
                            if (lastY != -1 && Math.abs(y - lastY) > tolerancia_cambio) {
                                // Si el cambio en Y es significativo, considera que hemos cambiado de línea
                                isSameLine = false;

                                // Imprime el texto acumulado para cada categoría y reinicia los acumuladores
                                if (asignatura.length() > 0) {
                                    fila.put("asignatura", asignatura.toString().trim());
                                    asignatura.setLength(0);
                                    asignatura.append(texto.getUnicode());// Reinicia el StringBuilder
                                }
                                if (docente.length() > 0) {
                                    fila.put("docente", docente.toString().trim());
                                    docente.setLength(0);
                                    // Reinicia el StringBuilder
                                }
                                if (horario.length() > 0) {
                                    fila.put("horario", horario.toString().trim());
                                    horario.setLength(0);
                                    // Reinicia el StringBuilder
                                }

                                // Agrega la fila a la tabla si es necesario
                                if (!fila.isEmpty()) {
                                    tabla.add(new HashMap<>(fila)); // Agrega una copia de la fila a la tabla
                                    fila.clear(); // Limpia la fila para la siguiente línea
                                }
                            } else {
                                isSameLine = true;
                            }

                            // Actualiza lastY
                            lastY = y;

                            // Acumula el texto para cada categoría en función del rango de coordenadas X
                            if (x >= cord_asig[0] && x <= cord_asig[1]) {
                                if (isSameLine) {
                                    asignatura.append(texto.getUnicode());
                                }
                            }

                            if (x >= cord_docen[0] && x <= cord_docen[1]) {
                                if (isSameLine) {
                                    docente.append(texto.getUnicode());
                                }
                            }

                            if (x >= cord_hora[0] && x <= cord_hora[1]) {
                                lastY = y;
                                if (isSameLine) {
                                    horario.append(texto.getUnicode());
                                }
                            }
                        }
                    }

                    // Imprime el texto acumulado para la última línea
                    if (asignatura.length() > 0) {
                        fila.put("asignatura", asignatura.toString().trim());
                    }
                    if (docente.length() > 0) {
                        fila.put("docente", docente.toString().trim());
                    }
                    if (horario.length() > 0) {
                        fila.put("horario", horario.toString().trim());
                    }

                    barraProgreso.setProgress(progreso);


                }

                @Override
                protected void writePage() throws IOException {
                    super.writePage();
                    // Agrega la última fila a la tabla si es necesario
                    if (!fila.isEmpty()) {
                        tabla.add(new HashMap<>(fila)); // Agrega una copia de la fila a la tabla
                        fila.clear();
                        asignatura.setLength(0);
                        docente.setLength(0);
                        horario.setLength(0);// Limpia la fila para la siguiente página
                    }
                }
            };
            stripper.getText(documento);
            documento.close();




            Intent intent = new Intent();
            intent.setClass(this, activity_resultados.class);
            Bundle datos = new Bundle();

            datos.putSerializable("datos", (Serializable) tabla);
            intent.putExtras(datos);

            for(int i = 0; i <= Math.abs(barraProgreso.getMax() - barraProgreso.getProgress()); i++) {
                if (barraProgreso.getProgress() != barraProgreso.getMax()) {
                    progreso = progreso + i;
                    barraProgreso.setProgress(progreso);
                }
            }

            startActivity(intent);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //Luego de que el usuario seleccionó el PDF comprobamos que sea correcto
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != 55) {
            Toast.makeText(this, "Al parecer hubo un problema, intenta de nuevo!", Toast.LENGTH_SHORT).show();
            return;
        }


        if (resultCode == RESULT_OK) { //Obtenemos todos los datos del pdf
            uri = data.getData();
            viewAnimator.showNext();
            preview();
            return;

        }

        Toast.makeText(this, "Al parecer hubo un problema, intenta de nuevo!", Toast.LENGTH_SHORT).show();



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}


