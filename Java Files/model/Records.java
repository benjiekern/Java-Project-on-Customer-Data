/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DBQuery;

/**
 *
 * @author Benjamin
 */

//Keeps track of customers and appointments, while also providing convenient methods. Most are self explanatory
public class Records {
    private static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static String currentUser;
    private static int currentUserId;
    private final static LocalTime openingTime = LocalTime.of(9, 0);
    private final static LocalTime closingTime = LocalTime.of(17, 0);
    public static void addAppointment(Appointment apt) {
        allAppointments.add(apt);
    }
    public static void addCustomer(Customer cust) {
        allCustomers.add(cust);
    }
    public static ObservableList<Customer> getAllCustomers() {
        return allCustomers;
    }
    public static ObservableList<Appointment> getAllAppointments() {
        return allAppointments;
    }
    //Updates whichever customer has the provided id provided customer with the customer in the paramter's list
    public static void updateCustomer(int id, Customer cust) {
        for (Customer c : allCustomers) {
            if(c.getId() == cust.getId())
                allCustomers.set(allCustomers.indexOf(c), cust);
        }      
    }
    //Updates whichever appointment has the provided id provided appointment with the appointment in the paramter's list
    public static void updateAppointment(int id, Appointment apt) {
        for (Appointment a : allAppointments) {
            if(a.getId() == apt.getId())
                allAppointments.set(allAppointments.indexOf(a), apt);
        }
    }
    public static boolean deleteCustomer(Customer cust) {
        return allCustomers.remove(cust);
    }
    public static boolean deleteAppointment(Appointment apt) {
        return allAppointments.remove(apt);
    }
    //provides opening hours
    public static LocalTime getOpeningHours() {
        return openingTime;
    }
    //provides closing hours
    public static LocalTime getClosingHours() {
        return closingTime;
    }
    public static String getCurrentUser() {
        return currentUser;
    }
    public static int getCurrentUserId() {
        return currentUserId;
    }
    public static void setCurrentUser(String user) {
        currentUser = user;
    }
    public static void setCurrentUserId(int id) {
        currentUserId = id;
    }
    public static void changeAppointmentName(String newName, int id) {
        for (Appointment a : allAppointments) {
            if(a.getCustomerId() == id)
               a.setCustomerName(newName);
        }  
    }
    
}
