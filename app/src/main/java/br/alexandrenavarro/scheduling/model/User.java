package br.alexandrenavarro.scheduling.model;

/**
 * Created by alexandrenavarro on 09/09/17.
 */

public class User {

    private String name;
    private String email;
    private String phone;
    private String uid;

    public User(String name, String email, String phone, String uid){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
