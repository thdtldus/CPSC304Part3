import exception.NoCarAvailableException;
import exception.NoSuchCustomerException;
import model.*;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseHandler {

    public Connection connect(String username, String password) throws SQLException{
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            //attempt connection to servers
            return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1522:stu", username, password);
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

    public Reservation createReservation(int dlicense, String carType, Connection con, java.util.Date fromDate, java.util.Date toDate) throws NoSuchCustomerException, NoCarAvailableException {
        Reservation reserve = null;
        try {
            //check if dlicense has entry in customer
            Statement statement = con.createStatement();
            Statement s3 = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Customer WHERE dlicense = " + dlicense);
            if (!rs.next()) {
                //if not, then throw the exception and the customer creation window will be created after
                throw new NoSuchCustomerException();
            } else {
                //then, check if there is any available vtname as requested from date to date (aka if any arent rented during that time)
                reserve = new Reservation(dlicense, carType, fromDate, toDate);
                java.sql.Date fDate = new java.sql.Date(fromDate.getTime());
                java.sql.Date tDate = new java.sql.Date(toDate.getTime());
                ResultSet tp = s3.executeQuery("SELECT * FROM TimePeriod WHERE fromDate = \'"+fDate+"\' AND toDate = \'"+tDate+"\'");
                if (!tp.next()) {
                    PreparedStatement ps = con.prepareStatement("INSERT INTO TimePeriod VALUES (?, ?)");
                    ps.setDate(1, new java.sql.Date(fromDate.getTime()));
                    ps.setDate(2, new java.sql.Date(toDate.getTime()));
                    ps.executeUpdate();
                    con.setAutoCommit(false);
                    con.commit();
                }
                PreparedStatement res = con.prepareStatement("INSERT INTO Reservation VALUES (?, ?, ?, ?, ?)");
                res.setInt(1, reserve.getConfNo());
                res.setString(2, reserve.getVtname());
                res.setInt(3, reserve.getDlicense());
                res.setDate(4, new java.sql.Date(reserve.getFromDate().getTime()));
                res.setDate(5, new java.sql.Date(reserve.getToDate().getTime()));
                res.executeUpdate();
                con.setAutoCommit(false);
                con.commit();
//                throw new NoCarAvailableException();
                //finally, generate random confirmation number, rerolling if the number already exists in the db
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reserve;
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
            ResultSet multiRent = statement.executeQuery("SELECT * FROM Rent WHERE confNo = " + confNum);
            while(!multiRent.next()) {
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
                    PreparedStatement rented = con.prepareStatement("UPDATE Vehicle SET status = " + "'rented'" + " WHERE " + "vlicense = " + rental.getVlicense());
                    rented.executeUpdate();
                    ps.executeUpdate();
                    con.setAutoCommit(false);
                    con.commit();
                }
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

    public ArrayList<Vehicle> getDailyRentalsBranch(Connection con, String branch){
        ArrayList<Vehicle> rentals = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Rent r, Vehicle v WHERE r.vlicense = v.vlicense AND r.fromDate = '2019-11-13' AND v.location = " +quote(branch)+" ORDER BY v.vtname");
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

    public ArrayList<Return> getDailyReturns(Connection con){
        ArrayList<Return> returns = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Return rt, Rent r, Vehicle v WHERE rt.rid = r.rid AND r.vlicense = v.vlicense AND r.fromDate = '2019-11-13' ORDER BY v.location, v.vtname");
            while (rs.next()){
                Return rt = new Return(rs.getInt("rid"));
                rt.setReturnDate(rs.getDate("returnDate"));
                rt.setOdometer(rs.getInt("odometer"));
                rt.setFulltank(rs.getString("fulltank"));
                rt.setValue(rs.getDouble("value"));
                returns.add(rt);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return returns;
    }

    public ArrayList<Return> getDailyReturnsBranch(Connection con, String branch){
        ArrayList<Return> returns = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Return rt, Rent r, Vehicle v WHERE rt.rid = r.rid AND r.vlicense = v.vlicense AND r.fromDate = '2019-11-13' AND v.location = "+quote(branch)+" ORDER BY v.vtname");
            while (rs.next()){
                Return rt = new Return(rs.getInt("rid"));
                rt.setReturnDate(rs.getDate("returnDate"));
                rt.setOdometer(rs.getInt("odometer"));
                rt.setFulltank(rs.getString("fulltank"));
                rt.setValue(rs.getDouble("value"));
                returns.add(rt);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return returns;
    }


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

    public ArrayList<Rent> getAllRents(Connection con){
        ArrayList<Rent> rents = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Rent");
            while(rs.next()){
                Rent rent = new Rent();
                rent.setExistingRid(rs.getInt("rid"));
                rent.setVlicense(rs.getInt("vlicense"));
                rent.setDlicense(rs.getInt("dlicense"));
                rent.setFromDate(rs.getDate("fromDate"));
                rent.setToDate(rs.getDate("toDate"));
                rent.setOdometer(rs.getInt("odometer"));
                rent.setCardName(rs.getString("cardName"));
                rent.setCardNo(rs.getString("cardNo"));
                rent.setExpDate(rs.getDate("expDate"));
                rent.setConfNo(rs.getInt("confNo"));
                rents.add(rent);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return rents;
    }

    public ArrayList<Return> getAllReturns(Connection con){
        ArrayList<Return> returns = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM RETURN");
            while (rs.next()){
                Return rt = new Return(rs.getInt("rid"));
                rt.setReturnDate(rs.getDate("returnDate"));
                rt.setOdometer(rs.getInt("odometer"));
                rt.setFulltank(rs.getString("fulltank"));
                rt.setValue(rs.getDouble("value"));
                returns.add(rt);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return returns;
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
