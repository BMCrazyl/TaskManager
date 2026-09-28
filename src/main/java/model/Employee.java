/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author mai09
 */
public class Employee {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String position;

    public Employee() {}

    public Employee(int id, String name, String email, String phone, String position) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.position = position;
    }

    public Employee(String name, String email, String phone, String position) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.position = position;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}
