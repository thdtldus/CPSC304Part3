package model;

import java.util.Date;

public class Return {
    private final int rid;
    private Date returnDate;
    private int odometer;
    private String fulltank;
    private double value;


    public Return(int rid) {
        this.rid = rid;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public int getOdometer() {
        return odometer;
    }

    public String getFulltank() {
        return fulltank;
    }

    public double getValue() {
        return value;
    }
}