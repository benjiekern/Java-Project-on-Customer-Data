/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Benjamin
 */

//Customer class, consists of only getters and setters for customer fields
public class Customer {
    private final int id;
    private int addressId;  
    private int zip;
    private String name;
    private String address;
    private String address2;
    private String phone;
    private String city;
    private String country;
    public Customer(int id, int addressId, int zip, String name, String address, String address2,  String phone, String city, String country) {
        this.id = id;
        this.addressId = addressId;
        this.zip = zip;
        this.name = name;
        this.address = address;
        this.address2 = address2;
        this.phone = phone;
        this.city = city;
        this.country = country;
    }
    public int getId() {
        return id;
    }
    public int getAddressId() {
        return addressId;
    }
    public int getZip() {
        return zip;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public String getAddress2() {
        return address2;
    }
    public String getPhone() {
        return phone;
    }
    public String getCity() {
        return city;
    }
    public String getCountry() {
        return country;
    }
    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }
    public void setZip(int zip) {
        this.zip = zip;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setAddress2(String address2) {
        this.address2 = address;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setCountry(String country) {
        this.country = country;
    }
}
