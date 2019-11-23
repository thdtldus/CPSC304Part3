import exception.NoCarAvailableException;
import exception.NoSuchCustomerException;
import model.Customer;
import model.Vehicle;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseHandler {

    public Connection connect(String username, String password){
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            //attempt connection to servers
            Connection c = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1522:stu", username, password);
            System.out.println("Successfully connected!");
            return c;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Vehicle> viewVehicles(Connection con){
        ArrayList<Vehicle> vehicleList = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Vehicle ORDER BY make");
            while(rs.next()){
                Vehicle vehicle = new Vehicle(rs.getInt("vlicense"));
                vehicle.setVid(rs.getInt("vid"));
                vehicle.setMake(rs.getString("make"));
                vehicle.setModel(rs.getString("model"));
                vehicle.setYear(rs.getInt("year"));
                vehicle.setColor(rs.getString("color"));
                vehicle.setOdometer(rs.getInt("odometer"));
                vehicle.setStatus(rs.getString("status"));
                vehicle.setVtname(rs.getString("vtname"));
                vehicle.setLocation(rs.getString("location"));
                vehicle.setCity(rs.getString("city"));
                vehicleList.add(vehicle);
            }
            return vehicleList;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return vehicleList;
    }

    public int createReservation(int dlicense, String carType, Connection con) throws NoSuchCustomerException, NoCarAvailableException {
        //steps:
        try {
            //check if dlicense has entry in customer
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Customer WHERE dlicense = "+dlicense);
            if (!rs.next()){
                //if not, then throw the exception and the customer creation window will be created after
                throw new NoSuchCustomerException();
            } else {
                //then, check if there is any available vtname as requested from date to date (aka if any arent rented during that time)
                throw new NoCarAvailableException();
                //finally, generate random confirmation number, rerolling if the number already exists in the db

            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public void createCustomer(Customer c, Connection con){
        try{
            PreparedStatement statement = con.prepareStatement("INSERT INTO Customer VALUES (?, ?, ?, ?)");
            statement.setInt(1, c.getDlicense());
            statement.setString(2, c.getCellphone());
            statement.setString(3, c.getName());
            statement.setString(4, c.getAddress());
            statement.executeUpdate();
            con.setAutoCommit(false);
            con.commit();
        } catch (SQLException e){
            e.printStackTrace();
        }

    }



    public void customQuery(String query, Connection con) throws SQLException{
        PreparedStatement statement = con.prepareStatement(query);
        statement.executeUpdate();
        con.setAutoCommit(false);
        con.commit();
    }



}
