package model;

public class VehicleType {
    private final String vtname;
    private String features;
    private int wrate;
    private int drate;
    private int hrate;
    private int wirate;
    private int dirate;
    private int hirate;
    private int krate;

    public VehicleType(String vtname){
        this.vtname = vtname;
    }

    public String getVtname() {
        return vtname;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public int getWrate() {
        return wrate;
    }

    public void setWrate(int wrate) {
        this.wrate = wrate;
    }

    public int getDrate() {
        return drate;
    }

    public void setDrate(int drate) {
        this.drate = drate;
    }

    public int getHrate() {
        return hrate;
    }

    public void setHrate(int hrate) {
        this.hrate = hrate;
    }

    public int getWirate() {
        return wirate;
    }

    public void setWirate(int wirate) {
        this.wirate = wirate;
    }

    public int getDirate() {
        return dirate;
    }

    public void setDirate(int dirate) {
        this.dirate = dirate;
    }

    public int getHirate() {
        return hirate;
    }

    public void setHirate(int hirate) {
        this.hirate = hirate;
    }

    public int getKrate() {
        return krate;
    }

    public void setKrate(int krate) {
        this.krate = krate;
    }
}