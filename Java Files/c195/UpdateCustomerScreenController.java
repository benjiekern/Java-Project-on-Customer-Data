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
import model.Appointment;
import model.Customer;
import model.Records;
import utils.DBQuery;

/**
 * FXML Controller class
 *
 * @author Benjamin
 */
public class UpdateCustomerScreenController implements Initializable {

    @FXML
    private TextField customerNameTxt;
    @FXML
    private TextField phoneTxt;
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
    private Customer cust;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    public void loadCustomer(Customer cust) {
        this.cust = cust;
        customerNameTxt.setText(cust.getName());
        phoneTxt.setText(cust.getPhone());
        addressTxt.setText(cust.getAddress());
        address2Txt.setText(cust.getAddress2());
        zipTxt.setText(Integer.toString(cust.getZip()));
        cityTxt.setText(cust.getCity());
        countryTxt.setText(cust.getCountry());
    }

    @FXML
    private void updateButton(ActionEvent event) throws SQLException, IOException {
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
        
        //The address in the database is updated with any new information
        //If no address with this name exists, a new one is inserted into the database        
        if(isCity) {
            selectStatement = "UPDATE U06A1Q.city SET city = '" + cityTxt.getText() + "', cityId = '" + cityId + "', countryId = '" + countryId + "', createDate = '" + LocalDateTime.now() 
                    + "', createdBy = 'Benji', lastUpdateBy = 'Benji' WHERE LOWER(city) = '" + cityTxt.getText().toLowerCase() + "'";
        }
        else {
            selectStatement = "INSERT INTO city(city, cityId, countryId, createDate, createdBy, lastUpdateBy) VALUES('"
                                + cityTxt.getText() + "', '" + cityId + "', '" + countryId + "', '"  + LocalDateTime.now() + "', '" + "Benji', 'Benji')";           
        }
        stat.execute(selectStatement);
        
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
        
        //The address in the database is updated with any new information    
        //If no address with this name exists, a new one is inserted into the database   
        }
        if(isAddress) {
            selectStatement = "UPDATE U06A1Q.address SET address = '" + addressTxt.getText() + "', address2 = '" + address2Txt.getText() 
                            + "', addressId = '" + addressId + "', cityId = '" + cityId + "', postalCode = '" + zipTxt.getText()
                            + "', phone = '" + phoneTxt.getText() + "', createDate = '" + LocalDateTime.now() + "', createdBy = 'Benji', lastUpdateBy = 'Benji'"
                    + "WHERE LOWER(address) = '" + addressTxt.getText().toLowerCase() + "'";
        } 
        else {
            selectStatement = "INSERT INTO address(address, address2, addressId, cityId, postalCode, phone,  createDate, createdBy, lastUpdateBy) VALUES('"
                + addressTxt.getText() + "', '" + address2Txt.getText() + "', '" + addressId + "', '" + cityId + "', '" + zipTxt.getText() 
                + "', '" + phoneTxt.getText() + "', '" + LocalDateTime.now() + "', '" + "Benji', " + "'Benji'" + ")";                
        }
        stat.execute(selectStatement);
        
        //Finally, the customer information in the database is updated
        int customerId = cust.getId();
        selectStatement = "UPDATE U06A1Q.customer SET customerId = '" + customerId + "', customerName = '" + customerNameTxt.getText() + "', addressId = '" 
                       + addressId + "', active = '" + 1 + "', createDate = '" + LocalDateTime.now() + "', createdBy = 'Benji', lastUpdateBy = 'Benji'"
                + " WHERE customerId = '" + customerId + "'";
        stat.execute(selectStatement);
        Records.updateCustomer(customerId, new Customer(customerId, addressId, Integer.parseInt(zipTxt.getText()), customerNameTxt.getText(), addressTxt.getText(), address2Txt.getText(),
        phoneTxt.getText(), cityTxt.getText(), countryTxt.getText()));
        
        Records.changeAppointmentName(customerNameTxt.getText(), customerId);
        
        Parent root = FXMLLoader.load(getClass().getResource("UserDashboard.fxml"));
        Scene UserDashboard = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(UserDashboard);
        stage.show();
    }
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
}
