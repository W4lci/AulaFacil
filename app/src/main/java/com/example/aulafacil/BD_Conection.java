package com.example.aulafacil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BD_Conection {
    private String db = "aulafacil";
    private String ip = "192.168.219.130";
    private String port = "3306";
    private String user = "root";
    private String pass = "123456";

    public Connection connection(){
        Connection base = null;

        try {

            Class.forName("com.mysql.jdbc.Driver");
            String datosConexion = "jdbc:mysql://" + ip + ":" + port + "/" + db;
            base = DriverManager.getConnection(datosConexion, user, pass);



        } catch (SQLException | ClassNotFoundException e) {

            System.out.println(e);;

        }

        return base;
    }
}
