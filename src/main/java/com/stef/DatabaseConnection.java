package com.stef;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private String url = "jdbc:mysql://localhost:3306/stefapp_db";
    private String user = "root";
    private String password = "";
    private Connection connection;

    public DatabaseConnection(){
        try{
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected Successfully");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public Connection getConnection(){
        return connection;
    }

}