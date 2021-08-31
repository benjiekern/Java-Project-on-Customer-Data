/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import utils.DBQuery;

/**
 *
 * @author Benjamin
 */

//Appointment class, consists of only getters and setters for appointment fields
public class Appointment {
    private int id;
    private int customerId;
    private int userId;
    private String customerName;
    private String userName;
    private String title;
    private String startTime;
    private String endTime;
    private String date;
    private String type;
    private String description;
    private String location;
    public Appointment(int id, int customerId, int userId, String customerName, String userName, String title, String startTime, String endTime, String date, String type, String description, String location) {
        this.id = id;
        this.customerId = customerId;
        this.userId = userId;
        this.customerName = customerName;
        this.userName = userName;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;        
        this.type = type;
        this.description = description;
        this.location = location;
        
        //the line below doesn't work, need to think of new way to get userId
        //this.user = Records.getCurrentUser();
    }
    public int getId() {
        return id;
    }
    public int getCustomerId() {
        return customerId;
    }
    public String getCustomerName() {
        return customerName;
    }
    public String getTitle() {
        return title;
    }
    public String getStartTime() {
        return startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public String getDate() {
        return date;
    }
    public String getType() {
        return type;
    }
    public String getDescription() {
        return description;
    }
    public String getLocation() {
        return location;
    }
    public int getUserId() {
        return userId;
    }
    public String getUserName() {
        return userName;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setType(String type) {
        this.type = type;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
}
