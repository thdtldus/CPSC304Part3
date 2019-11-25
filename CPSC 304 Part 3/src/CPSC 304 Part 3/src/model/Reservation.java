package model;

import java.util.Date;

public class Reservation {
    private int confNo;
    private String vtname;
    private int dlicense;
    private Date fromDate;
    private Date toDate;

    public Reservation(int dlicense, String vtname, Date fromDate, Date toDate){
        setConfNo();
        this.dlicense = dlicense;
        this.vtname = vtname;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public void setConfNo(){
        this.confNo = (int)(Math.random()*10000);
    }

    public void setExistingConf(int confNo) {
        this.confNo = confNo;
    }

    public int getConfNo(){
        return confNo;
    }

    public String getVtname() {
        return vtname;
    }

    public void setVtname(String vtname) {
        this.vtname = vtname;
    }

    public int getDlicense() {
        return dlicense;
    }

    public void setDlicense(int dlicense) {
        this.dlicense = dlicense;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
