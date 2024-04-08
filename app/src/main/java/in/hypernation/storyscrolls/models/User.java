package in.hypernation.storyscrolls.models;

public class User {
    private String uid;
    private String name;
    private String number;
    private String email;
    private String address;

    public User(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    public User(String uid, String name, String number, String email, String address) {
        this.uid = uid;
        this.name = name;
        this.number = number;
        this.email = email;
        this.address = address;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
