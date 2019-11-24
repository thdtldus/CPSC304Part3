import exception.NoCarAvailableException;
import exception.NoSuchCustomerException;
import model.*;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseHandler {

    public Connection connect(String username, String password) throws SQLException{
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            //attempt connection to servers
            Connection c = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1522:stu", username, password);
            return c;
    }

    public ArrayList<Vehicle> viewVehicles(Connection con){
        ArrayList<Vehicle> vehicleList = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Vehicle ORDER BY make");
            while(rs.next()){
                Vehicle vehicle = new Vehicle(rs.getInt("vlicense"));
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

    public int createReservation(int dlicense, String carType, Connection con, java.util.Date fromDate, java.util.Date toDate) throws NoSuchCustomerException, NoCarAvailableException {
        //steps:
        try {
            //check if dlicense has entry in customer
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Customer WHERE dlicense = " + dlicense);
            if (!rs.next()) {
                //if not, then throw the exception and the customer creation window will be created after
                throw new NoSuchCustomerException();
            } else {
                //then, check if there is any available vtname as requested from date to date (aka if any arent rented during that time)
//                ResultSet vt = statement.executeQuery("SELECT vlicense FROM Vehicle WHERE vtname = "+carType);
//                Statement s2 = con.createStatement();
//                while(rs.next()) {
//                    int vl = rs.getInt("vlicense");
//                    ResultSet reserves = statement.executeQuery("SELECT * FROM Rent WHERE vlicense = "+vl+" AND NOT EXISTS "+"SELECT * FROM Rent WHERE ())
//                }
                throw new NoCarAvailableException();
                //finally, generate random confirmation number, rerolling if the number already exists in the db

            }
        } catch (SQLException e) {
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

    public Rent createRental(int confNum, Connection con, String cardName, String cardNo, java.util.Date expDate) {
        Rent rental = new Rent();
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Reservation WHERE confNo = " + confNum);
            while (rs.next()) {
                Statement s2 = con.createStatement();
                String vtname = rs.getString("vtname");
                int dlicense = rs.getInt("dlicense");
                ResultSet r2 = s2.executeQuery("SELECT * FROM Vehicle WHERE vtname = " + quote(vtname) + " AND " + "status = " + "'available'");
                while (r2.next()) {
                    rental.setVlicense(r2.getInt("vlicense"));
                    rental.setOdometer(r2.getInt("odometer"));
                    rental.setDlicense(dlicense);
                    rental.setFromDate(rs.getDate("fromDate"));
                    rental.setToDate(rs.getDate("toDate"));
                    rental.setConfNo(rs.getInt("confNo"));
                    rental.setCardName(cardName);
                    rental.setCardNo(cardNo);
                    rental.setExpDate(expDate);
                }
                PreparedStatement ps = con.prepareStatement("INSERT INTO Rent VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                ps.setInt(1, rental.getRid());
                ps.setInt(2, rental.getVlicense());
                ps.setInt(3, rental.getDlicense());
                ps.setDate(4, new java.sql.Date(rental.getFromDate().getTime()));
                ps.setDate(5, new java.sql.Date(rental.getToDate().getTime()));
                ps.setInt(6, rental.getOdometer());
                ps.setString(7, rental.getCardName());
                ps.setString(8, rental.getCardNo());
                ps.setDate(9, new java.sql.Date(rental.getExpDate().getTime()));
                ps.setInt(10, rental.getConfNo());
                ps.executeUpdate();
                con.setAutoCommit(false);
                con.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rental;
    }

    public ArrayList<Vehicle> getDailyRentals(Connection con){
        ArrayList<Vehicle> rentals = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Rent r, Vehicle v WHERE r.vlicense = v.vlicense AND r.fromDate = '2019-11-13' ORDER BY v.location, v.vtname");
            while (rs.next()){
                Vehicle v = new Vehicle(rs.getInt("vlicense"));
                v.setMake(rs.getString("make"));
                v.setModel(rs.getString("model"));
                v.setYear(rs.getInt("year"));
                v.setColor(rs.getString("color"));
                v.setOdometer(rs.getInt("odometer"));
                v.setVtname(rs.getString("vtname"));
                v.setLocation(rs.getString("location"));
                v.setCity(rs.getString("city"));
                rentals.add(v);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return rentals;
    }

//    public ArrayList<Return> getDailyReturns(Connection con){
//        ArrayList<Return> returns = new ArrayList<>();
//        try {
//            Statement statement = con.createStatement();
//            ResultSet rs = statement.executeQuery("SELECT * FROM Return rt, Rent r, Vehicle v WHERE rt.rid = r.rid AND r.vlicense = v.vlicense ORDER BY v.location, v.vtname ");
//            while (rs.next()){
//                //Return rt = new Return();
//            }
//        } catch (SQLException e){
//            e.printStackTrace();
//        }
//        return returns;
//    }


    public ArrayList<VehicleType> getAllVehicleTypes(Connection con){
        ArrayList<VehicleType> vehicleTypeList = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM VehicleType");
            while(rs.next()){
                VehicleType vehicleType = new VehicleType(rs.getString("vtname"));
                vehicleType.setFeatures(rs.getString("features"));
                vehicleType.setWrate(rs.getInt("wrate"));
                vehicleType.setDrate(rs.getInt("drate"));
                vehicleType.setHrate(rs.getInt("hrate"));
                vehicleType.setWirate(rs.getInt("wirate"));
                vehicleType.setDirate(rs.getInt("dirate"));
                vehicleType.setHirate(rs.getInt("hirate"));
                vehicleType.setKrate(rs.getInt("krate"));
                vehicleTypeList.add(vehicleType);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return vehicleTypeList;
    }

    public ArrayList<Vehicle> getAllVehicles(Connection con){
        ArrayList<Vehicle> vehicleList = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Vehicle");
            while(rs.next()){
                Vehicle vehicle = new Vehicle(rs.getInt("vlicense"));
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
        } catch (SQLException e){
            e.printStackTrace();
        }
        return vehicleList;
    }

    public ArrayList<Customer> getAllCustomers(Connection con){
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Customer");
            while(rs.next()){
                Customer customer = new Customer(rs.getInt("dlicense"));
                customer.setCellphone(rs.getString("cellphone"));
                customer.setName(rs.getString("name"));
                customer.setAddress(rs.getString("address"));
                customers.add(customer);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return customers;
    }

    public ArrayList<Reservation> getAllReservations(Connection con){
        ArrayList<Reservation> reservations = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Reservation");
            while(rs.next()){
                Reservation reservation = new Reservation(rs.getInt("dlicense"), rs.getString("vtname"), rs.getDate("fromDate"), rs.getDate("toDate"));
                reservation.setExistingConf(rs.getInt("confNo"));
                reservations.add(reservation);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return reservations;
    }

    public ArrayList<Rent> getAllRent(Connection con){
        ArrayList<Rent> rents = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Rent");
            while(rs.next()){
               // Rent rent = new Rent();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return rents;
    }


    public void customQuery(String query, Connection con) throws SQLException{
        PreparedStatement statement = con.prepareStatement(query);
        statement.executeUpdate();
        con.setAutoCommit(false);
        con.commit();
    }


    private static String quote(String s) {
        return new StringBuilder()
                .append('\'')
                .append(s)
                .append('\'')
                .toString();
    }


}
