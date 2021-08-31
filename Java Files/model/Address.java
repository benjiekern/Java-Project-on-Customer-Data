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
public class Address {
    private int addressId;
    private String address;
    public Address(int addressId, String address) {
        this.addressId = addressId;
        this.address = address;
    }
    public int getAddressId() {
        return addressId;
    }
    public String getAddress() {
        return address;
    }
}
