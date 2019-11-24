package model;

import java.util.Date;

public class Rent {
    private int rid;
    private int vlicense;
    private int dlicense;
    private Date fromDate;
    private Date toDate;
    private int odometer;
    private String cardName;
    private String cardNo;
    private Date expDate;
    private int confNo;

    public Rent(){
        setRid();
    }

    public void setRid(){
        rid = (int)(Math.random()*1000);
    }

    public void setExistingRid(int rid){
        this.rid = rid;
    }

    public int getRid(){
        return rid;
    }

    public int getVlicense() {
        return vlicense;
    }

    public void setVlicense(int vlicense) {
        this.vlicense = vlicense;
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

    public int getOdometer() {
        return odometer;
    }

    public void setOdometer(int odometer) {
        this.odometer = odometer;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public int getConfNo() {
        return confNo;
    }

    public void setConfNo(int confNo) {
        this.confNo = confNo;
    }
}
