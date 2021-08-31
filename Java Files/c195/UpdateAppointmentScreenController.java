/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.Records;
import utils.DBQuery;

/**
 * FXML Controller class
 *
 * @author Benjamin
 */
public class UpdateAppointmentScreenController implements Initializable {
    
    private Appointment apt;
    @FXML
    private TextField customerNameTxt;
    @FXML
    private TextField titleTxt;
    @FXML
    private TextField startTimeTxt;
    @FXML
    private TextField endTimeTxt;
    @FXML
    private TextField dateTxt;
    @FXML
    private TextField typeTxt;
    @FXML
    private TextArea descriptionTxt;
    @FXML
    private TextField locationTxt;
    public void loadAppointment(Appointment apt) {
        this.apt = apt;
        customerNameTxt.setText(apt.getCustomerName());
        titleTxt.setText(apt.getTitle());
        startTimeTxt.setText(apt.getStartTime().substring(0, 5));
        endTimeTxt.setText(apt.getEndTime().substring(0, 5));
        dateTxt.setText(apt.getDate());
        typeTxt.setText(apt.getType());
        descriptionTxt.setText(apt.getDescription());
        locationTxt.setText(apt.getLocation());
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void updateButton(ActionEvent event) throws IOException, SQLException {
        int appointmentId = apt.getId();       
        int userId = apt.getUserId();
        int customerId = getCustomerId(customerNameTxt.getText());
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime;
        LocalDateTime endTime;
        try {
            startTime = LocalDateTime.parse(dateTxt.getText() + " " + startTimeTxt.getText(), timeFormat);
            endTime = LocalDateTime.parse(dateTxt.getText() + " " + endTimeTxt.getText(), timeFormat);
        }
        catch(Exception e) {
            System.out.println("Please use correct time and date formats");
            return;
        }
        //Sets up all necessary time conversions
        ZoneId localZone = ZoneId.of(TimeZone.getDefault().getID());
        ZonedDateTime adjustedStartTime = ZonedDateTime.of(startTime.toLocalDate(), startTime.toLocalTime(), localZone);
        ZonedDateTime adjustedEndTime = ZonedDateTime.of(endTime.toLocalDate(), endTime.toLocalTime(), localZone);
        
        Instant localStartTimeToUTC = adjustedStartTime.toInstant();
        Instant localEndTimeToUTC = adjustedEndTime.toInstant();
        
        LocalDateTime newStartTime = LocalDateTime.ofInstant(localStartTimeToUTC, ZoneOffset.UTC);
        LocalDateTime newEndTime = LocalDateTime.ofInstant(localEndTimeToUTC, ZoneOffset.UTC);
        //Checks that customer exists
        try {
            if (customerId == -1)
                throw new IOException();
        }
        catch(IOException e) {
            System.out.println("Please enter a valid customer name");
            return;
        }
        //Checks that time is within opening and closing hours
        try {
            if ((ChronoUnit.MINUTES.between(startTime.toLocalTime(), Records.getOpeningHours())) > 0
                || (ChronoUnit.MINUTES.between(endTime.toLocalTime(), Records.getClosingHours()) < 0)) {
                throw new IOException();            
            }            
        }
        catch (IOException e){
            System.out.println("Sorry, but the time you selected is outside business hours, please try again");
            return;
        }
        //Checks that appointments don't overlap
        try {
            for (Appointment apt : Records.getAllAppointments()) {
                if((apt.getCustomerId() == customerId && apt.getId() != appointmentId) && (startTime.toLocalDate().compareTo(LocalDate.parse(apt.getDate())) == 0)) {
                    if((((ChronoUnit.MINUTES.between(startTime.toLocalTime(), LocalTime.parse(apt.getStartTime())) <= 0) && 
                            (ChronoUnit.MINUTES.between(startTime.toLocalTime(), LocalTime.parse(apt.getEndTime())) >= 0))) ||
                            ((ChronoUnit.MINUTES.between(LocalTime.parse(apt.getStartTime()), startTime.toLocalTime()) <= 0) &&
                            (ChronoUnit.MINUTES.between(LocalTime.parse(apt.getStartTime()), endTime.toLocalTime()) >= 0)))              
                        throw new IOException();
                }
                                                      
            }
        }
        catch(IOException e) {
            System.out.println("Sorry, but at least 2 of your appointments overlap. Please fix this");
            return;       
        }
        //Updates appointments in database
        Statement stat = DBQuery.getStatement();
        String selectStatement = "UPDATE U06A1Q.appointment SET customerId = '" + customerId + "', title = '" + titleTxt.getText() + "', start = '" + newStartTime 
                                + "', end = '" + newEndTime + "', type = '" + typeTxt.getText() + "', description = '" 
                                + descriptionTxt.getText() + "', location = '" + locationTxt.getText() + "' WHERE appointmentId = " + appointmentId;
        stat.execute(selectStatement);
        Records.updateAppointment(appointmentId, new Appointment(appointmentId, customerId, userId, customerNameTxt.getText(), getUserName(userId), titleTxt.getText(), startTimeTxt.getText(), 
                                endTimeTxt.getText(), dateTxt.getText(), typeTxt.getText(), descriptionTxt.getText(), locationTxt.getText()));
        
        Parent root = FXMLLoader.load(getClass().getResource("UserDashboard.fxml"));
        Scene UserDashboard = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(UserDashboard);
        stage.show();
    }
    public int getCustomerId(String name) throws SQLException {
        Statement stat = DBQuery.getStatement();
        String selectStatement = "SELECT customerId FROM U06A1Q.customer WHERE customerName = '" + customerNameTxt.getText() + "'";
        stat.execute(selectStatement);
        ResultSet rs = stat.getResultSet();
        while(rs.next()) {
            return rs.getInt("customerId");
        }
        return -1;
    }
    public static String getUserName(int userId) throws SQLException {
        Statement stat = DBQuery.getStatement();
        String selectStatement = "SELECT userName FROM U06A1Q.user WHERE userId = " + userId;
        stat.execute(selectStatement);
        ResultSet rs = stat.getResultSet();
        while(rs.next()) {
            return rs.getString("userName");
        }
        return "Username not found";
    }   
}
