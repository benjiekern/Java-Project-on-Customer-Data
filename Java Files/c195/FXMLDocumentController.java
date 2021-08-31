/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.Records;
import utils.DBQuery;

/**
 *
 * @author benji
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label loginLbl;
    
    @FXML
    private Label usernameLbl;
    
    @FXML
    private Label passwordLbl;
    
    @FXML 
    private TextField usernameTxt;
    
    @FXML
    private TextField passwordTxt;
    
    @FXML
    private Button button;
    
    private Map <String, String> loginInfo = new HashMap<String, String>();
    private static Alert appointmentAlert;
    private static Alert loginAlert;
    
    //Logs the user in if their information is correct; will also load userdashboard
    @FXML
    private void button(ActionEvent event) throws IOException, SQLException {
        if(passwordTxt.getText().equals(loginInfo.get(usernameTxt.getText()))) {
            Records.setCurrentUser(usernameTxt.getText());        
            Statement stat = DBQuery.getStatement();
            String selectStatement = "SELECT userName, userId FROM U06A1Q.user";
            stat.execute(selectStatement);
            ResultSet rs = stat.getResultSet();
            while(rs.next()) {
                if(rs.getString("userName").toLowerCase().equals(usernameTxt.getText().toLowerCase())) {
                    Records.setCurrentUserId(rs.getInt("userId"));
                    break;
                }
            }
            
            //Will present an alert if there is an appointment within 15 minutes
            for (Appointment apt : Records.getAllAppointments()) {
                if (((ChronoUnit.MINUTES.between(LocalTime.now(), LocalTime.parse(apt.getStartTime()))) <= 15) &&
                ((ChronoUnit.MINUTES.between(LocalTime.now(), LocalTime.parse(apt.getStartTime()))) >= -15) && 
                (LocalDate.now().compareTo(LocalDate.parse(apt.getDate()))) == 0) {
                    appointmentAlert = new Alert(Alert.AlertType.INFORMATION);
                    appointmentAlert.setTitle("Alert");
                    appointmentAlert.setContentText("There is an appointment within 15 minutes from now");
                    appointmentAlert.showAndWait();
            }
        }
        
        //The lines below write/append to a file to keep logs of the users and times    
        String logString = "\n\nUser: "+ usernameTxt.getText() + "\nTime: " + LocalDateTime.now().toString(); 
        File file = new File("logs.txt");
        try  {  
            if(!file.exists())
                file.createNewFile();
        }
        catch(Exception e) {
            System.out.println("Error");
        }
        
        FileWriter fw = new FileWriter(file, true);
        fw.write(logString);
        fw.close();
        
        Parent root = FXMLLoader.load(getClass().getResource("UserDashboard.fxml"));
        Scene userDashBoard = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(userDashBoard);
        stage.show();
        }
        //If the user information was incorrect, the user will be notified/not granted access
        else {
            if(!loginLbl.getText().equals("Einloggen")) {
                loginAlert = new Alert(Alert.AlertType.ERROR);
                loginAlert.setTitle("Error");
                loginAlert.setContentText("Your username/password was incorrect");
                loginAlert.showAndWait();
            }
            else {
                loginAlert = new Alert(Alert.AlertType.ERROR);
                loginAlert.setTitle("Warnung");
                loginAlert.setContentText("Es tut mir leid aber das war leider falsch");
                loginAlert.showAndWait();
            }
        }
    }
    
    //Checks users, as well as setting up text in the correct language
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (Locale.getDefault().toLanguageTag().toString().toLowerCase().equals("de") || Locale.getDefault().getCountry().toString().toLowerCase().equals("de")) {
            loginLbl.setText("Einloggen");
            usernameLbl.setText("Benutzer Name");
            passwordLbl.setText("Passwort");
            button.setText("Weiter Gehen");
        }
        loginInfo.put("jacob3", "secret!365");
        loginInfo.put("test", "pass");
        loginInfo.put("ryan26", "secret_pass");
    }    
    
}
