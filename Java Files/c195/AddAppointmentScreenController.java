/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import model.Records;
import utils.DBQuery;

/**
 * FXML Controller class
 *
 * @author Benjamin
 */
public class AddAppointmentScreenController implements Initializable {

    @FXML
    private TextField customerNameTxt;
    @FXML
    private TextField titleTxt;
    @FXML
    private TextField typeTxt;
    @FXML
    private TextArea descriptionTxt;
    @FXML
    private TextField locationTxt;
    @FXML
    private TextField startTimeTxt;
    @FXML
    private TextField endTimeTxt;
    private LocalDateTime appointmentTime;
    @FXML
    private TextField dateTxt;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    //Adds appointment
    @FXML
    private void addButton(ActionEvent event) throws ParseException, SQLException, IOException, SQLException {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime endTime;
        LocalDateTime startTime;
        try { 
            startTime = LocalDateTime.parse(dateTxt.getText() + " " + startTimeTxt.getText(), timeFormat);        
            endTime = LocalDateTime.parse(dateTxt.getText() + " " + endTimeTxt.getText(), timeFormat);
        }
        catch(Exception e) {
            System.out.println("Please use correct time and date formats");
            return;
        }
        //Adds time/does time conversions. Will not allow the user to set an appointment time outside of business hours
        LocalDateTime localStartTime = LocalDateTime.parse(dateTxt.getText() + " " + startTimeTxt.getText(), timeFormat);
        LocalDateTime localEndTime = LocalDateTime.parse(dateTxt.getText() + " " + endTimeTxt.getText(), timeFormat);
        ZoneId localZone = ZoneId.of(TimeZone.getDefault().getID());
        ZoneId universalTimeZone = ZoneId.of("Greenwich");
        ZonedDateTime adjustedStartTime = ZonedDateTime.of(startTime.toLocalDate(), startTime.toLocalTime(), localZone);
        ZonedDateTime adjustedEndTime = ZonedDateTime.of(endTime.toLocalDate(), endTime.toLocalTime(), localZone);
        adjustedEndTime.withZoneSameLocal(universalTimeZone);

        Instant localStartTimeToUTC = adjustedStartTime.toInstant();
        Instant localEndTimeToUTC = adjustedEndTime.toInstant();
        ZonedDateTime utcToLocal = localStartTimeToUTC.atZone(universalTimeZone);
        ZonedDateTime utcToLocal2 = localEndTimeToUTC.atZone(universalTimeZone);  
        int customerId = getCustomerId(customerNameTxt.getText());
        int appointmentId = getAppointmentId();
        int userId = Records.getCurrentUserId();
        //Below try catch blocks make sure that times are valid, as well as that the customerId provided is correct
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
        try {
            if (customerId == -1)
                throw new IOException();
        }
        catch(IOException e) {
            System.out.println("Please enter a valid customer name");
            return;
        }
        //Checks that appointments don't overlap
        try {
            for (Appointment apt : Records.getAllAppointments()) {
                if((apt.getCustomerId() == customerId) && (startTime.toLocalDate().compareTo(LocalDate.parse(apt.getDate())) == 0)) {
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
        //Inserts appointment into database
        Statement stat = DBQuery.getStatement();
        String selectStatement = "INSERT INTO appointment(appointmentId, customerId, userId, title, description, location, contact, "
                                + "type, url, start, end, createDate, createdBy, lastUpdateBy) "
                                + "VALUES('" + appointmentId + "', '" + customerId + "', '" + userId + "', '" + titleTxt.getText() + "', '" + descriptionTxt.getText() 
                                + "', '" + locationTxt.getText() + "', 'NONE', '" + typeTxt.getText() + "', 'NONE', '" 
                                + utcToLocal.toLocalDateTime()+ "', '" + utcToLocal2.toLocalDateTime() + "', '" + LocalDateTime.now() + "', 'Benji', 'Benji')";
        stat.execute(selectStatement);
        Records.addAppointment(new Appointment(appointmentId, customerId, userId, customerNameTxt.getText(), getUserName(userId), titleTxt.getText(), 
                startTimeTxt.getText(), endTimeTxt.getText(), dateTxt.getText(), typeTxt.getText(),
        descriptionTxt.getText(), locationTxt.getText()));
        Parent root = FXMLLoader.load(getClass().getResource("UserDashboard.fxml"));
        Scene UserDashboard = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(UserDashboard);
        stage.show();
    }
    
    public static int getCustomerId(String name) {
        for (Customer c : Records.getAllCustomers())
        {
            if(c.getName().equals(name))
                return c.getId();
        }
        return -1;
    }
    public static int getAppointmentId() throws SQLException{
        Statement stat = DBQuery.getStatement();
        String selectStatement = "SELECT MAX(appointmentId) FROM U06A1Q.appointment";
        stat.execute(selectStatement);
        ResultSet rs = stat.getResultSet();
        while(rs.next()) {
            return rs.getInt("MAX(appointmentId)") + 1;
        }
        return 1;
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
