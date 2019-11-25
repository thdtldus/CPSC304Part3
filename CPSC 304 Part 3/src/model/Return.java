package model;

import java.util.Date;

public class Return {
    private final int rid;
    private Date returnDate;
    private int odometer;
    private String fulltank; //can be true or false
    private double value;

    public Return(int rid){
        this.rid = rid;
    }

    public int getRid() {
        return rid;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public int getOdometer() {
        return odometer;
    }

    public void setOdometer(int odometer) {
        this.odometer = odometer;
    }

    public String getFulltank() {
        return fulltank;
    }

    public void setFulltank(String fulltank) {
        this.fulltank = fulltank;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
