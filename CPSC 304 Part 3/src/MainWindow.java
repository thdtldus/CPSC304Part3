import exception.NoCarAvailableException;
import exception.NoSuchCustomerException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Customer;
import model.Vehicle;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainWindow extends Application {
    private String username;
    private String password;
    private DatabaseHandler dbhandler = new DatabaseHandler();
    private Connection con;

    @Override
    public void start(Stage primaryStage){
        Scene mainScene = initializeMainWindow();
        primaryStage.setScene(mainScene);
        primaryStage.show();
        Stage secondStage = new Stage();
        secondStage.setTitle("Login");
        secondStage.show();
        Scene loginScene = initializeLoginWindow(primaryStage);
        secondStage.setScene(loginScene);
    }

    private Scene initializeLoginWindow(Stage primaryStage){
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20.0);
        grid.setVgap(10.0);
        Text usernameText = new Text("Username: ");
        grid.add(usernameText, 0, 0);
        TextField usernameField = new TextField();
        grid.add(usernameField, 1, 0);
        Text passwordText = new Text("Password: ");
        grid.add(passwordText, 0, 1);
        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 1);
        Button loginButton = new Button("Login");
        grid.add(loginButton, 0, 2);
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                username = usernameField.getText();
                password = passwordField.getText();
                con = dbhandler.connect(username, password);
                enterMainWindow(primaryStage);
            }
        });
        return new Scene(grid, 500, 300);
    }

    private Scene initializeMainWindow(){
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20.0);
        grid.setVgap(10.0);
        Text notConnected = new Text("Not Connected!");
        grid.add(notConnected, 0, 0);
        return new Scene(grid, 400, 200);
    }

    private void enterMainWindow(Stage primaryStage){
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20.0);
        grid.setVgap(10.0);
        Button vehicleButton = new Button("View all available cars");
        grid.add(vehicleButton, 0, 2);
        Text carTypeText = new Text("Car Type: ");
        grid.add(carTypeText, 0, 0);
        TextField carTypeField = new TextField();
        grid.add(carTypeField,1, 0);
        Text locationText = new Text("Location: ");
        grid.add(locationText, 0, 1);
        TextField locationField = new TextField();
        grid.add(locationField, 1, 1);
        vehicleButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                ArrayList<Vehicle> vehicleList = dbhandler.viewVehicles(con);
                //this could probably be done properly through queries later
                ArrayList<Vehicle> filteredList = new ArrayList<>();
                for(Vehicle v : vehicleList){
                    //won't show any unavailable cars
                    if ("available".equals(v.getStatus())){
                        boolean flag = true;
                        if (!carTypeField.getText().isEmpty()){
                            //flag set to false if the vehicle's field doesn't match the field entered
                            flag = (carTypeField.getText().equals(v.getVTName()));
                        }
                        if (!locationField.getText().isEmpty()){
                            flag = (locationField.getText().equals(v.getLocation()));
                        }
                        //not sure how time period will work...
                        if (flag){
                            //will be removed from list if the vehicle has been set to be filtered
                            filteredList.add(v);
                        }
                    }
                }
                //pass the filtered list into function creating new window
                createVehiclesWindow(filteredList);
            }
        });
        Text dlicense = new Text("Driver's license number: ");
        grid.add(dlicense, 0, 3);
        TextField licenseField = new TextField();
        grid.add(licenseField, 1, 3);
        Text carType = new Text("Type of car wanted: (choices: SUV, )");
        grid.add(carType, 0, 4);
        TextField carTypeWantedField = new TextField();
        grid.add(carTypeWantedField, 1, 4);
        //need fromDate and toDate as well
        Button reserveButton = new Button("Make a reservation");
        grid.add(reserveButton, 0, 5);
        reserveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    int confNum = dbhandler.createReservation(Integer.parseInt(licenseField.getText()), carTypeWantedField.getText(), con);
                    createReservationWindow(confNum, carType.getText());
                } catch (NumberFormatException ne){
                    createErrorWindow("You must pass in a proper license number!");
                } catch (NoSuchCustomerException e){
                    createCustomerWindow(Integer.parseInt(licenseField.getText()));
                } catch (NoCarAvailableException e2){
                    createErrorWindow("There is no car available at that time!");
                }
            }
        });
        Text reserveNo = new Text("Reservation number: (if none, leave it blank)");
        grid.add(reserveNo, 0, 6);
        TextField reserveField = new TextField();
        grid.add(reserveField, 1, 6);
        Button rentCar = new Button ("Rent car out");
        grid.add(rentCar, 0, 7);
        rentCar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //rent car will go here - will create a reservation first if there was no reservation number provided
                //should also create error if reservation number doesn't exist
            }
        });
        Text rid = new Text("Rent ID Number");
        grid.add(rid, 0, 8);
        TextField ridField = new TextField();
        grid.add(ridField, 1, 8);
        Button returnsCar = new Button("Return a rented car");
        grid.add(returnsCar, 0, 9);
        returnsCar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //returning car process should go here, error if the rid doesn't exist
            }
        });
        Text generateReports = new Text("Generate reports for:");
        grid.add(generateReports, 0, 10);
        Button dailyRentals = new Button("Daily Rentals");
        grid.add(dailyRentals, 0, 11);
        dailyRentals.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //daily rentals window
            }
        });
        Button dailyRentalsBranch = new Button("Daily Rentals for Branch");
        grid.add(dailyRentalsBranch, 1, 11);
        dailyRentalsBranch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //daily rentals for branch window
            }
        });
        Button dailyReturns = new Button("Daily Returns");
        grid.add(dailyReturns, 2, 11);
        dailyReturns.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //daily returns window
            }
        });
        Button dailyReturnsBranch = new Button("Daily Returns for Branch");
        grid.add(dailyReturnsBranch, 3, 11);
        dailyReturnsBranch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //daily returns for branch window
            }
        });
        Button displayRows = new Button("Display all rows");
        grid.add(displayRows, 0, 14);
        displayRows.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //this will create new window that inside it you can modify the tables
            }
        });
        Button modifyTables = new Button("Insert or delete a tuple (type query in right)");
        grid.add(modifyTables, 0, 15);
        TextField query = new TextField();
        grid.add(query, 1, 15);
        modifyTables.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
               try {
                   dbhandler.customQuery(query.getText(), con);
                   //should create a window saying the query was successfully executed
               } catch (SQLException e){
                   createErrorWindow("The query was not formed correctly!");
               }
            }
        });
        Scene mainScene = new Scene (grid, 1000, 600);
        primaryStage.setScene(mainScene);
    }




    private void createVehiclesWindow(ArrayList<Vehicle> vehicleList){
        Stage thirdStage = new Stage();
        GridPane grid = new GridPane();
        grid.setHgap(20.0);
        grid.setVgap(10.0);
        Text vehicleCount = new Text("Number of vehicles available: "+vehicleList.size());
        grid.add(vehicleCount, 0, 0);
        int acc = 1;
        for (Vehicle v : vehicleList){
            Button vehicle = new Button(v.getMake()+" "+v.getModel()+" "+v.getYear());
            vehicle.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent event){
                    createOneVehicleWindow(v);
                }
            });
            grid.add(vehicle, 0, acc);
            acc++;
        }
        thirdStage.show();
        Scene scene = new Scene(grid, 500,500);
        thirdStage.setScene(scene);
    }

    private void createOneVehicleWindow(Vehicle v){
        Stage fourthStage = new Stage();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20.0);
        grid.setVgap(10.0);
        Text vehicleName = new Text(v.getMake()+" "+v.getModel()+" "+v.getYear());
        grid.add(vehicleName, 0, 0);
        Text colour = new Text("Colour: "+v.getColor());
        grid.add(colour, 0 ,1);
        Text vehicleID = new Text("ID: "+v.getVid());
        grid.add(vehicleID, 0, 2);
        Text vehicleLicense = new Text("License Plate: "+v.getVLicense());
        grid.add(vehicleLicense, 0, 3);
        Text vehicleType = new Text("Type of car: "+v.getVTName());
        grid.add(vehicleType, 0, 4);
        Text odometer = new Text("KM travelled: "+v.getOdometer());
        grid.add(odometer, 0, 5);
        Text location = new Text("Location: "+v.getLocation()+", "+v.getCity());
        grid.add(location, 0, 6);
        Scene scene = new Scene(grid, 250, 500);
        fourthStage.setScene(scene);
        fourthStage.show();
    }

    private void createCustomerWindow(int dlicense){
        Stage customerStage = new Stage();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20.0);
        grid.setVgap(10.0);
        Text dlicenseText = new Text("Creating profile for driver license #: "+dlicense);
        grid.add(dlicenseText, 0, 0);
        Text cellphone = new Text("Cellphone number (no spaces or dashes): ");
        grid.add(cellphone, 0, 1);
        TextField cellphoneField = new TextField();
        grid.add (cellphoneField, 1, 1);
        Text name = new Text("Name: ");
        grid.add(name, 0, 2);
        TextField nameField = new TextField();
        grid.add(nameField, 1, 2);
        Text address = new Text("Address: ");
        grid.add(address, 0, 3);
        TextField addressField = new TextField();
        grid.add(addressField, 1, 3);
        Button submit = new Button("Submit information");
        grid.add(submit, 0, 4);
        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Customer customer = new Customer(dlicense);
                customer.setCellphone(cellphoneField.getText());
                customer.setAddress(addressField.getText());
                customer.setName(nameField.getText());
                dbhandler.createCustomer(customer, con);
                //after the information is submitted, the window should close
                submit.setDisable(true);
                customerStage.hide();
            }
        });
        Scene scene = new Scene(grid, 500, 500);
        customerStage.setScene(scene);
        customerStage.show();
    }

    private void createReservationWindow(int confNum, String carType){

    }

    private void createErrorWindow(String error){
        Stage errorStage = new Stage();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20.0);
        grid.setVgap(10.0);
        Text errorMessage = new Text("Error: "+error);
        grid.add(errorMessage, 0, 0);
        Scene scene = new Scene(grid, 500, 500);
        errorStage.setScene(scene);
        errorStage.show();
    }


    public static void main(String[] args) {
        //for reference (need to run this before launching program, replace username):
        // ssh -l <username> -L localhost:1522:dbhost.students.cs.ubc.ca:1522 remote.students.cs.ubc.ca
        // ssh -l jennyf98 -L localhost:1522:dbhost.students.cs.ubc.ca:1522 remote.students.cs.ubc.ca
        launch(args);
    }
}
