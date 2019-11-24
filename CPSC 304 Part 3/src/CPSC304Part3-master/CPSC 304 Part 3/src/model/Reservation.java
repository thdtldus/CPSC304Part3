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
        dlicense = this.dlicense;
        vtname = this.vtname;
        fromDate = this.fromDate;
        toDate = this.toDate;
    }

    public void setConfNo(){
        confNo = (int)Math.random()*1000;
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
