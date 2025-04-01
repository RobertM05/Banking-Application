package org.example;

public class User {
    private int id;
    private String username;
    private String email;
    private double balance;

    public User(int id, String username, String email, double balance) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.balance = balance;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public double getBalance() { return balance; }

    public void setBalance(double balance) { this.balance = balance; }
}