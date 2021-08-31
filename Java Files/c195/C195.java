package c195;


import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.TimeZone;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import model.Records;
import utils.DBConnect;
import utils.DBQuery;

/**
 *
 * @author benji
 */
public class C195 extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
       
        Connection conn = DBConnect.startConnection();
        DBQuery.setStatement(conn);
        Statement stat = DBQuery.getStatement();
        
        //Gets all customer information from database
        String selectStatement = "SELECT U06A1Q.customer.customerId, U06A1Q.address.addressId, U06A1Q.address.postalCode, U06A1Q.customer.customerName, U06A1Q.address.address, "
                       + "U06A1Q.address.address2, U06A1Q.address.phone, U06A1Q.city.city, U06A1Q.country.country "
                + "FROM U06A1Q.customer INNER JOIN U06A1Q.address ON U06A1Q.address.addressId = U06A1Q.customer.addressId "
                + "INNER JOIN U06A1Q.city ON U06A1Q.address.cityId = U06A1Q.city.cityId INNER JOIN U06A1Q.country ON U06A1Q.city.countryId = U06A1Q.country.countryId;";
        stat.execute(selectStatement);
        ResultSet rs = stat.getResultSet();
        
        //Adds customer to records
        while(rs.next()) {
            int customerId = rs.getInt("customerId");
            int customerAddressId = rs.getInt("addressId");
            int customerZip = rs.getInt("postalCode");
            String customerName = rs.getString("customerName");
            String customerAddress = rs.getString("address");
            String customerAddress2 = rs.getString("address2");
            String customerPhone = rs.getString("phone");
            String cityName = rs.getString("city");
            String countryName = rs.getString("country");
            Records.addCustomer(new Customer(customerId, customerAddressId, customerZip, customerName, customerAddress, customerAddress2, customerPhone, cityName, countryName));
        }        
        
        //Gets all appointment information from database
        selectStatement = "SELECT U06A1Q.appointment.appointmentId, U06A1Q.appointment.customerId, U06A1Q.appointment.userId, U06A1Q.customer.customerName, U06A1Q.user.userName, "
                + "U06A1Q.appointment.title, U06A1Q.appointment.type, U06A1Q.appointment.start, U06A1Q.appointment.end, U06A1Q.appointment.createDate, U06A1Q.appointment.description, "
                + "U06A1Q.appointment.location FROM U06A1Q.appointment INNER JOIN U06A1Q.customer ON U06A1Q.appointment.customerId = U06A1Q.customer.customerId "
                + "INNER JOIN U06A1Q.user on U06A1Q.user.userId = U06A1Q.appointment.userId";
        stat.execute(selectStatement);
        rs = stat.getResultSet();
        ZoneId localZoneId = ZoneId.of(TimeZone.getDefault().getID());
        ZoneId utcZoneId = ZoneId.of("Greenwich");
        Time start;
        Time end;
        Date date;
        LocalTime localStart;
        LocalTime localEnd;
        LocalDate localDate;
        ZonedDateTime utcStartZDT;
        ZonedDateTime utcEndZDT;
        
        //Adds appointment to records
        while(rs.next()) {
            int appointmentId = rs.getInt("appointmentId");
            int customerId = rs.getInt("customerId");
            int userId = rs.getInt("userId");
            String customerName = rs.getString("customerName");
            String userName = rs.getString("userName");
            String title = rs.getString("title");
            String type = rs.getString("type");
            start = rs.getTime("start");
            end = rs.getTime("end");
            date = rs.getDate("start");
            String description = rs.getString("description");
            String location = rs.getString("location");
            localStart = start.toLocalTime();
            localEnd = end.toLocalTime();
            localDate = date.toLocalDate();
            utcStartZDT = ZonedDateTime.of(localDate, localStart, utcZoneId);
            utcEndZDT = ZonedDateTime.of(localDate, localEnd, utcZoneId);
            Instant localStartToUTC = utcStartZDT.toInstant();
            Instant localEndToUTC = utcEndZDT.toInstant();
            LocalDateTime utcStartLDT = LocalDateTime.ofInstant(localStartToUTC, localZoneId);
            LocalDateTime utcEndLDT = LocalDateTime.ofInstant(localEndToUTC, localZoneId);
            String stringDate = date.toString();
            String startTime = utcStartLDT.toLocalTime().toString();
            String endTime = utcEndLDT.toLocalTime().toString();
            
            Records.addAppointment(new Appointment(appointmentId, customerId, userId, customerName, userName, 
                    title, startTime, endTime, stringDate, type, description, location));
        }      
        
        launch(args);
        DBConnect.closeConnection();
    }  
}