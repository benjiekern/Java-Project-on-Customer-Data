/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 * @author benji
 */
public class DBQuery {
    
    private static Statement stat;
    
    public static void setStatement(Connection conn) throws SQLException {
        stat = conn.createStatement();
    }
    
    public static Statement getStatement() {
        return stat;
    }
}
