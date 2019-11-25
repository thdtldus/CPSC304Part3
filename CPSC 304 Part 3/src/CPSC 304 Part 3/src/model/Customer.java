package model;

public class Customer {
    private final int dlicense;
    private String cellphone;
    private String name;
    private String address;

    public Customer(int dlicense){
        this.dlicense = dlicense;
    }

    public int getDlicense(){
        return dlicense;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
