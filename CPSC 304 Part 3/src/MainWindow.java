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
import model.VehicleType;

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
        Text carType = new Text("Type of car wanted:");
        grid.add(carType, 0, 4);
        TextField carTypeWantedField = new TextField();
        grid.add(carTypeWantedField, 1, 4);
        Text fromDate = new Text("Starting from:");
        grid.add(fromDate, 0, 5);
        TextField fromField = new TextField();
        grid.add(fromField, 1, 5);
        Text toDate = new Text("Ending on:");
        grid.add(toDate, 2, 5);
        TextField toField = new TextField();
        grid.add(toField, 3, 5);
        Button reserveButton = new Button("Make a reservation");
        grid.add(reserveButton, 0, 6);
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
        grid.add(reserveNo, 0, 7);
        TextField reserveField = new TextField();
        grid.add(reserveField, 1, 7);
        Button rentCar = new Button ("Rent car out");
        grid.add(rentCar, 0, 8);
        rentCar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //rent car will go here - will create a reservation first if there was no reservation number provided
                //should also create error if reservation number doesn't exist
            }
        });
        Text rid = new Text("Rent ID Number");
        grid.add(rid, 0, 9);
        TextField ridField = new TextField();
        grid.add(ridField, 1, 9);
        Button returnsCar = new Button("Return a rented car");
        grid.add(returnsCar, 0, 10);
        returnsCar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //returning car process should go here, error if the rid doesn't exist
            }
        });
        Text generateReports = new Text("Generate reports for:");
        grid.add(generateReports, 0, 11);
        Button dailyRentals = new Button("Daily Rentals");
        grid.add(dailyRentals, 0, 12);
        dailyRentals.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //daily rentals window
            }
        });
        Button dailyRentalsBranch = new Button("Daily Rentals for Branch");
        grid.add(dailyRentalsBranch, 1, 12);
        dailyRentalsBranch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //daily rentals for branch window
            }
        });
        Button dailyReturns = new Button("Daily Returns");
        grid.add(dailyReturns, 2, 12);
        dailyReturns.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //daily returns window
            }
        });
        Button dailyReturnsBranch = new Button("Daily Returns for Branch");
        grid.add(dailyReturnsBranch, 3, 12);
        dailyReturnsBranch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //daily returns for branch window
            }
        });
        Button displayRows = new Button("Display all rows");
        grid.add(displayRows, 0, 15);
        displayRows.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                createDisplayWindow();
            }
        });
        Button modifyTables = new Button("Insert or delete a tuple (type query in right)");
        grid.add(modifyTables, 0, 16);
        TextField query = new TextField();
        grid.add(query, 1, 16);
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
        Scene scene = new Scene(grid, 250, 500);
        customerStage.setScene(scene);
        customerStage.show();
    }

    private void createReservationWindow(int confNum, String carType){
        Stage reserveStage = new Stage();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20.0);
        grid.setVgap(10.0);


    }

    private void createDisplayWindow(){
        Stage displayStage = new Stage();
        GridPane grid = new GridPane();
        grid.setHgap(20.0);
        grid.setVgap(10.0);
        int acc = 0;
        //display vehicletypes
        Text vehicleTypes = new Text("VehicleTypes:");
        grid.add(vehicleTypes, 0, acc);
        acc++;
        for(VehicleType vt : dbhandler.getAllVehicleTypes(con)){
            Text vtname = new Text(vt.getVtname());
            grid.add(vtname, 0, acc);
            Text features = new Text(vt.getFeatures());
            grid.add(features, 1, acc);
            Text wrate = new Text(Integer.toString(vt.getWrate()));
            grid.add(wrate, 2, acc);
            Text drate = new Text(Integer.toString(vt.getDrate()));
            grid.add(drate, 3, acc);
            Text hrate = new Text(Integer.toString(vt.getHrate()));
            grid.add(hrate, 4, acc);
            Text wirate = new Text(Integer.toString(vt.getWirate()));
            grid.add(wirate, 5, acc);
            Text dirate = new Text(Integer.toString(vt.getDirate()));
            grid.add(dirate, 6, acc);
            Text hirate = new Text(Integer.toString(vt.getHirate()));
            grid.add(hirate, 7, acc);
            Text krate = new Text(Integer.toString(vt.getKrate()));
            grid.add(krate, 8, acc);
            acc++;
        }
        //display vehicles
        Text vehicles = new Text("Vehicles:");
        grid.add(vehicles, 0, acc);
        acc++;
        for(Vehicle v : dbhandler.getAllVehicles(con)){

        }
        Scene scene = new Scene(grid, 1000, 1000);
        displayStage.setScene(scene);
        displayStage.show();
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
