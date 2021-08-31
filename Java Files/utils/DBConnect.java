/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Benjamin
 */
public class DBConnect {
    
    private static Connection conn = null;
    
    public static Connection startConnection()  {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection)DriverManager.getConnection(
                "URL@HERE",
                "USER@HERE",
                "PASSWORD@HERE");
            System.out.println("Successfully connected to database");
        }
        catch(ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    
    public static void closeConnection() {
        try {
            conn.close();
            System.out.println("Connection was successfully terminated");
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
