package model;

public class Vehicle {

    private final int vlicense;
    private String make;
    private String model;
    private int year;
    private String color;
    private int odometer;
    private String status; //can be "available", "rented", or "maintenance"
    private String vtname;
    private String location;
    private String city;


    public Vehicle(int vlicense){
        this.vlicense = vlicense;
    }

    public int getVLicense() {
        return vlicense;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public String getColor() {
        return color;
    }

    public int getOdometer() {
        return odometer;
    }

    public String getStatus() {
        return status;
    }

    public String getVTName() {
        return vtname;
    }

    public String getLocation() {
        return location;
    }

    public String getCity() {
        return city;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setOdometer(int odometer) {
        this.odometer = odometer;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setVtname(String vtname) {
        this.vtname = vtname;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
