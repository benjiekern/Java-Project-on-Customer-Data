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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Customer;
import model.Records;
import utils.DBQuery;

/**
 * FXML Controller class
 *
 * @author Benjamin
 */
public class AddCustomerScreenController implements Initializable {

    @FXML
    private TextField customerNameTxt;
    @FXML
    private TextField addressTxt;
    @FXML
    private TextField address2Txt;
    @FXML
    private TextField zipTxt;
    @FXML
    private TextField cityTxt;
    @FXML
    private TextField countryTxt;
    @FXML
    private TextField phoneTxt;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    //Adds customer if possible. Otherwise
    @FXML
    private void addButton(ActionEvent event) throws SQLException, IOException {
        boolean isCountry = false, isCity = false, isAddress = false;
        int countryId = getCountryId(), cityId = getCityId(), addressId = getAddressId();
        try {
            int zip = Integer.parseInt(zipTxt.getText());
        }
        catch(Exception e) {
            System.out.println("Zip must be a number");
            return;
        }
        //Makes sure all necessary fields are filled in
        if(customerNameTxt.getText().equals("") || addressTxt.getText().equals("") || cityTxt.getText().equals("") || countryTxt.getText().equals("") || phoneTxt.getText().equals("")) {
            System.out.println("Please fill in the required fields");
            return;
        }
        //Creates new country if none exists, otherwise gets country information
        Statement stat = DBQuery.getStatement();
        String selectStatement = "SELECT country, countryId FROM U06A1Q.country";
        stat.execute(selectStatement);
        ResultSet rs = stat.getResultSet();
        
        while(rs.next()) {
            if(rs.getString("country").toLowerCase().equals(countryTxt.getText().toLowerCase())) {
                isCountry = true;
                countryId = rs.getInt("countryId");
                break;
            }
        }
        if(!isCountry) {
            selectStatement = "INSERT INTO country(country, countryId, createDate, createdBy, lastUpdateBy) VALUES('"
                                + countryTxt.getText() + "', '" + countryId + "', '" + LocalDateTime.now() + "', '" + "Benji', " + "'Benji'" + ")";
            stat.execute(selectStatement);
        }
        
        //Creates new CITY if none exists, otherwise gets CITY information
        selectStatement = "SELECT city, cityId FROM U06A1Q.city";
        stat.execute(selectStatement);
        rs = stat.getResultSet();
        
        while(rs.next()) {
            if(rs.getString("city").toLowerCase().equals(cityTxt.getText().toLowerCase())) {
                isCity = true;
                cityId = rs.getInt("cityId");
                break;
            }
        }
        
        //If no address with this name exists, a new one is inserted into the database
        if(!isCity) {
            selectStatement = "INSERT INTO city(city, cityId, countryId, createDate, createdBy, lastUpdateBy) VALUES('"
                                + cityTxt.getText() + "', '" + cityId + "', '" + countryId + "', '"  + LocalDateTime.now() + "', '" + "Benji', " + "'Benji'" + ")";
            stat.execute(selectStatement);
        }
        
        //Creates new ADDRESS if none exists, otherwise gets ADDRESS information
        selectStatement = "SELECT address, addressId FROM U06A1Q.address";
        stat.execute(selectStatement);
        rs = stat.getResultSet();
        
        while(rs.next()) {
            if(rs.getString("address").toLowerCase().equals(addressTxt.getText().toLowerCase())) {
                isAddress = true;
                addressId = rs.getInt("addressId");
                break;
            }
            
        //If no address with this name exists, a new one is inserted into the database   
        }
        if(!isAddress) {
            selectStatement = "INSERT INTO address(address, address2, addressId, cityId, postalCode, phone,  createDate, createdBy, lastUpdateBy) VALUES('"
                + addressTxt.getText() + "', '" + address2Txt.getText() + "', '" + addressId + "', '" + cityId + "', '" + zipTxt.getText() 
                + "', '" + phoneTxt.getText() + "', '" + LocalDateTime.now() + "', '" + "Benji', " + "'Benji'" + ")";
            stat.execute(selectStatement);       
        }
        int customerId = getCustomerId();
        Records.addCustomer(new Customer(customerId, addressId, Integer.parseInt(zipTxt.getText()), customerNameTxt.getText(), addressTxt.getText(), address2Txt.getText(),
               phoneTxt.getText(), cityTxt.getText(), countryTxt.getText()));
        selectStatement = "INSERT INTO customer(customerId, customerName, addressId, active, createDate, createdBy, lastUpdateBy) VALUES('" 
                + customerId + "', '" + customerNameTxt.getText() + "', '" + addressId + "', '" + 1 + "', '" + LocalDateTime.now() + "', '" 
                + "Benji" +  "', 'Benji')";
        stat.execute(selectStatement);
        
        Parent root = FXMLLoader.load(getClass().getResource("UserDashboard.fxml"));
        Scene UserDashboard = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(UserDashboard);
        stage.show();
    }
    //Gets country id
    public static int getCountryId() throws SQLException{
        Statement stat = DBQuery.getStatement();
        String selectStatement = "SELECT COUNT(country) FROM U06A1Q.country";
        stat.execute(selectStatement);
        ResultSet rs = stat.getResultSet();
        
        while(rs.next()) {
            return rs.getInt("COUNT(country)") + 1;
        }    
        return 1;
    }
    //Gets city id
    public static int getCityId() throws SQLException{
        Statement stat = DBQuery.getStatement();
        String selectStatement = "SELECT COUNT(city) FROM U06A1Q.city";
        stat.execute(selectStatement);
        ResultSet rs = stat.getResultSet();
        
        while(rs.next()) {
            return rs.getInt("COUNT(city)") + 1;
        }    
        return 1;
    }
    //Gets address id
    public static int getAddressId() throws SQLException{
        Statement stat = DBQuery.getStatement();
        String selectStatement = "SELECT COUNT(address) FROM U06A1Q.address";
        stat.execute(selectStatement);
        ResultSet rs = stat.getResultSet();
        
        while(rs.next()) {
            return rs.getInt("COUNT(address)") + 1;
        }    
        return 1;
    }
    //Gets customer id
    public static int getCustomerId() throws SQLException{
        Statement stat = DBQuery.getStatement();
        String selectStatement = "SELECT MAX(customerId) FROM U06A1Q.customer";
        stat.execute(selectStatement);
        ResultSet rs = stat.getResultSet();
        while(rs.next()) {
            return rs.getInt("MAX(customerId)") + 1;
        }    
        return 1;
    }
}
