package c195;

import java.sql.SQLException;
import java.io.IOException;
import java.util.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import model.Records;
import static model.Records.deleteCustomer;
import utils.DBQuery;

//Presents a dashboard to the user, allowing them to modify/add customers, appointments, as well as a variety of other options
public class UserDashboardController implements Initializable {

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<?, ?> customerID;
    @FXML
    private TableColumn<?, ?> customerName;
    @FXML
    private TableColumn<?, ?> customerAddress;
    @FXML
    private TableColumn<?, ?> customerPhone;
    @FXML
    private Button updateCustomerBtn;
    @FXML
    private TableView <Appointment> appointmentTable;
    @FXML
    private TableColumn<?, ?> appointmentId;
    @FXML
    private TableColumn<?, ?> appointmentName;
    @FXML
    private TableColumn<Appointment, String> appointmentType;
    @FXML
    private Button updateAppointmentBtn;
    private static ObservableList<Calendar> calendars = FXCollections.observableArrayList();
    private static Map<Appointment, Calendar> listOfAppointments = new HashMap<Appointment, Calendar>();
    private static ObservableList<Appointment> appointmentsThisWeek = FXCollections.observableArrayList();
    private static ObservableList<Appointment> appointmentsThisMonth = FXCollections.observableArrayList();
    private static ObservableList<Appointment> customersAppointments = FXCollections.observableArrayList();
    @FXML
    private ToggleGroup calendarSort;
    @FXML
    private TableView<Appointment> calendarTable;
    @FXML
    private TableColumn<?, ?> calendarAppointmentId;
    @FXML
    private TableColumn<?, ?> calendarAppointmentDate;
    @FXML
    private TableColumn<?, ?> calendarCustomerName;
    @FXML
    private RadioButton weekRB;
    @FXML
    private RadioButton monthRB;
    private static ObservableList<Appointment> allAppointments = Records.getAllAppointments(); 
    private static ObservableList<Appointment> searchAppointments = FXCollections.observableArrayList();
    @FXML
    private Button distinctTypesBtn;
    @FXML
    private TableView<?> generateTable;
    @FXML
    private Label distinctLbl;
    @FXML
    private TextArea citiesTxt;
    private TextField customerTxt;
    private TableView<Appointment> customerSearchTable;
    @FXML
    private Button searchBtn;
    @FXML
    private TableColumn<?, ?> customerNameCol;
    @FXML
    private TableColumn<?, ?> timesCol;
    @FXML
    private TextField userTxt;
    @FXML
    private TableView<Appointment> userSearchTable;
    @FXML
    private TableColumn<?, ?> userNameCol;
    @FXML
    private Button listCustomersBtn;
    @FXML
    private TextField customerSearchTxt;
    @FXML
    private Button customerSearchBtn;
    private int customerId;
    @FXML
    private Label customerSearchLbl;
    
    //Sets up the User dashboard and displays all necessary tables, buttons, etc.
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        calendars.clear();
        listOfAppointments.clear();
        appointmentsThisWeek.clear();
        appointmentsThisMonth.clear();
        
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        for (Appointment apt : Records.getAllAppointments()) {
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(apt.getDate()));
            } catch (ParseException ex) {
                Logger.getLogger(UserDashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }
            listOfAppointments.put(apt, cal);
            calendars.add(cal);
        }
        for (Map.Entry<Appointment, Calendar> entry : listOfAppointments.entrySet()) {            
            if ((entry.getValue().get(Calendar.WEEK_OF_YEAR) == currentWeek) && (entry.getValue().get(Calendar.YEAR) == currentYear))
                appointmentsThisWeek.add(entry.getKey());
            if ((entry.getValue().get(Calendar.MONTH) == currentMonth) && (entry.getValue().get(Calendar.YEAR) == currentYear))
                appointmentsThisMonth.add(entry.getKey());
        }
        Comparator<Appointment> calendarComparator = Comparator.comparing(Appointment::getDate);
        FXCollections.sort(appointmentsThisWeek, calendarComparator);
        FXCollections.sort(appointmentsThisMonth, calendarComparator);
        calendarTable.setItems(appointmentsThisWeek);
        
        calendarAppointmentId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        calendarCustomerName.setCellValueFactory(new PropertyValueFactory<>("CustomerName"));
        calendarAppointmentDate.setCellValueFactory(new PropertyValueFactory<>("Date"));
        
    
        customerTable.setItems(Records.getAllCustomers());
        
        customerID.setCellValueFactory(new PropertyValueFactory<>("Id"));
        customerName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        customerAddress.setCellValueFactory(new PropertyValueFactory<>("Address"));
        customerPhone.setCellValueFactory(new PropertyValueFactory<>("Phone")); 
       
        Comparator<Appointment> appointmentComparator = Comparator.comparing(Appointment::getId);
        FXCollections.sort(allAppointments, appointmentComparator);
        appointmentTable.setItems(allAppointments);
       
        appointmentId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        appointmentName.setCellValueFactory(new PropertyValueFactory<>("CustomerName"));
        appointmentType.setCellValueFactory(cell -> {return new SimpleStringProperty (cell.getValue().getType());}); //This lambda expression is convenient, since it can directly use the getter methods
                                                                                                                     //If for whatever reason the getter method was setup with an odd name, it would not be any issue
                                                                                                                     //in this instance. The 2 lines above, however, would not work at all if this were the case.  
    }

    //Takes user to a customer add screen
    @FXML
    private void customerAddBtn(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("addCustomerScreen.fxml"));
        Scene addCustomerScreen = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(addCustomerScreen);
        stage.show();
    }

    //Takes user to a customer update screen for selected customer
    @FXML
    private void customerUpdateButton(ActionEvent event) throws IOException {
        if(customerTable.getSelectionModel().getSelectedItem() == null)
            return;
        Stage stage;
        Parent root;
        
        stage = (Stage)updateCustomerBtn.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("updateCustomerScreen.fxml"));
        root = loader.load();
        Scene updateCustomerScreen = new Scene(root);
        
        stage.setScene(updateCustomerScreen);
        stage.show();
        UpdateCustomerScreenController controller = loader.getController();
        controller.loadCustomer(customerTable.getSelectionModel().getSelectedItem());
    }

    //Deletes selected customer
    @FXML
    private void customerDeleteButton(ActionEvent event) throws SQLException {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if(selectedCustomer == null)
            return;
        
        deleteCustomer(selectedCustomer);
        Statement stat = DBQuery.getStatement();
        String selectStatement = "DELETE FROM U06A1Q.customer WHERE customerId = " + selectedCustomer.getId();
        stat.execute(selectStatement);
    }

    //Takes user to an appointment add screen
    @FXML
    private void appointmentAddBtn(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("addAppointmentScreen.fxml"));
        Scene addAppointmentScreen = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(addAppointmentScreen);
        stage.show();
    }

    //Takes the user to an appointment update screen for selected appointment
    @FXML
    private void appointmentUpdateButton(ActionEvent event) throws IOException {
        if(appointmentTable.getSelectionModel().getSelectedItem() == null)
            return;
        Stage stage;
        Parent root;
        
        stage = (Stage)updateAppointmentBtn.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("updateAppointmentScreen.fxml"));
        root = loader.load();
        Scene updateApointmentScreen = new Scene(root);
        
        stage.setScene(updateApointmentScreen);
        stage.show();
        UpdateAppointmentScreenController controller = loader.getController();
        controller.loadAppointment(appointmentTable.getSelectionModel().getSelectedItem());
    }

    //Deletes selected appointment
    @FXML
    private void appointmentDeleteButton(ActionEvent event) throws SQLException {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if(selectedAppointment == null)
            return;
        Records.deleteAppointment(selectedAppointment);
        Statement stat = DBQuery.getStatement();
        String selectStatement = "DELETE FROM U06A1Q.appointment WHERE appointmentId = " + selectedAppointment.getId();
        stat.execute(selectStatement);
    }

    //Lists appointments of current week
    @FXML
    private void sortByWeekRB(ActionEvent event) {
        //calendarTable.getItems().clear();
        calendarTable.setItems(appointmentsThisWeek);
        
        calendarAppointmentId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        calendarCustomerName.setCellValueFactory(new PropertyValueFactory<>("CustomerName"));
        calendarAppointmentDate.setCellValueFactory(new PropertyValueFactory<>("Date"));
    }

    //Lists appointments of current month
    @FXML
    private void sortByMonthRB(ActionEvent event) {
        //calendarTable.getItems().clear();
        calendarTable.setItems(appointmentsThisMonth);
        
        calendarAppointmentId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        calendarCustomerName.setCellValueFactory(new PropertyValueFactory<>("CustomerName"));
        calendarAppointmentDate.setCellValueFactory(new PropertyValueFactory<>("Date"));
    }

    //Lists number of distinct appointment types of current month
    @FXML
    private void distinctTypesButton(ActionEvent event) throws SQLException {
        int count = -1;
        customerSearchBtn.setVisible(false);
        customerSearchTxt.setVisible(false);
        customerSearchLbl.setVisible(false);
        searchBtn.setVisible(false);
        userSearchTable.setVisible(false);
        userTxt.setVisible(false);
        citiesTxt.setVisible(false);
        generateTable.setVisible(false);
        distinctLbl.setVisible(true);
        LocalDateTime.now().getMonthValue();
        String selectStatement = "SELECT COUNT(DISTINCT type) FROM U06A1Q.appointment WHERE MONTH(start) = " 
                + LocalDateTime.now().getMonthValue() + " AND YEAR(start) = " + LocalDateTime.now().getYear();
        Statement stat = DBQuery.getStatement();
        stat.execute(selectStatement);
        ResultSet rs = stat.getResultSet();
        while (rs.next()) {
            count = rs.getInt("COUNT(DISTINCT type)");
        }
        distinctLbl.setText("Distinct appointment types this month: " + count); 
    }

    //Lists out all distinct cities
    @FXML
    private void citiesButton(ActionEvent event) throws SQLException {
        String cities = "";
        customerSearchBtn.setVisible(false);
        customerSearchTxt.setVisible(false);
        customerSearchLbl.setVisible(false);
        searchBtn.setVisible(false);
        userSearchTable.setVisible(false);
        userTxt.setVisible(false);
        generateTable.setVisible(false);
        distinctLbl.setVisible(false); 
        citiesTxt.setVisible(true);
        String selectStatement = "SELECT DISTINCT city FROM U06A1Q.city";
        Statement stat = DBQuery.getStatement();
        stat.execute(selectStatement);
        ResultSet rs = stat.getResultSet();
        while (rs.next()) {
            cities += ", " + rs.getString("city");
        }
        citiesTxt.setText("Distinct cities: " + cities); 
    }

    //Displys the search bar and button for username searching
    @FXML
    private void scheduleButton(ActionEvent event) {
        searchBtn.setVisible(true);
        userSearchTable.setVisible(true);
        userTxt.setVisible(true);
        customerSearchBtn.setVisible(false);
        customerSearchTxt.setVisible(false);
        customerSearchLbl.setVisible(false);
        generateTable.setVisible(false);
        distinctLbl.setVisible(false); 
        citiesTxt.setVisible(false)
;    }

    //Lists all appointments with the userId that was searched for
    @FXML
    private void searchButton(ActionEvent event) {
        if (userTxt.getText().equals(""))
            return;
        searchAppointments.clear();
        String userSearch = userTxt.getText();
        for (Appointment apt : allAppointments) {
            if(apt.getUserName().toLowerCase().contains(userSearch.toLowerCase()))
                searchAppointments.add(apt);
        }
        userSearchTable.setItems(searchAppointments);
        
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("UserName"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("CustomerName"));
        timesCol.setCellValueFactory(new PropertyValueFactory<>("StartTime"));
    }   

    //Dispalys the search bar and button for customer searching
    @FXML
    private void listCustomersButton(ActionEvent event) {
        searchBtn.setVisible(false);
        userSearchTable.setVisible(false);
        userTxt.setVisible(false);
        generateTable.setVisible(false);
        distinctLbl.setVisible(false); 
        citiesTxt.setVisible(false);
        customerSearchBtn.setVisible(true);
        customerSearchTxt.setVisible(true);
        customerSearchLbl.setVisible(true);
        customerSearchLbl.setText("");
    }

    //Lists # of appointments with the customerId that was searched for
    @FXML
    private void customerSearchButton(ActionEvent event) {
        try {
            customerId = Integer.parseInt(customerSearchTxt.getText());
        }
        catch (Exception e) {
            System.out.println("Please enter a valid ID");
            return;
        }
        customersAppointments = allAppointments.filtered(a -> { //This Line uses a lambda expression to drastrically reduce the amount of code, 
            return a.getCustomerId() == customerId;             //while also demonstrating how effective lambdas can be used in order to quickly filter lists.
        });                                                     //this lambda returns every appointment into a list where the customer Id of that appointment is equal to the one provided by the user
        customerSearchLbl.setText("# of this customer's appointments: " + customersAppointments.size());
    }
    
}