package in.hypernation.storyscrolls.models;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Order {
    private String uid;
    private String name;
    private String email;
    private String number;
    private String address;
    private String totalAmount;
    private int totalItems;
    private ArrayList<Product> products;
    private String paymentMode;
    private Timestamp orderTime;

    public Order(String uid, String name, String email, String number, String address, String totalAmount, int totalItems, ArrayList<Product> products, String paymentMode, Timestamp orderTime) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.number = number;
        this.address = address;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
        this.products = products;
        this.paymentMode = paymentMode;
        this.orderTime=orderTime;

    }

    public Timestamp getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Timestamp orderTime) {
        this.orderTime = orderTime;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}
