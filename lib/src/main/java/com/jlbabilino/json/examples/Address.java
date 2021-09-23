// package com.jlbabilino.json.examples;

// import com.jlbabilino.json.DeserializedJSONObjectValue;
// import com.jlbabilino.json.JSONSerializable;
// import com.jlbabilino.json.SerializedJSONObjectValue;

// @JSONSerializable
// public class Address {
//     private int streetNumber;
//     private String streetName;
//     private String city;
//     private String state;
//     private int zipCode;
//     private String country;

//     @DeserializedJSONObjectValue(keys = {"street_number", "street_name", "city", "state", "zip", "country"})
//     public Address(int streetNumber, String streetName, String city, String state, int zipCode, String country) {
//         this.setStreetNumber(streetNumber);
//         this.setStreetName(streetName);
//         this.setCity(city);
//         this.setState(state);
//         this.setZipCode(zipCode);
//         this.setCountry(country);
//     }

//     public Address() {
//         this(0, "", "", "", 0, "");
//     }

//     @SerializedJSONObjectValue(key = "country")
//     public String getCountry() {
//         return country;
//     }

//     public void setCountry(String country) {
//         this.country = country;
//     }

//     @SerializedJSONObjectValue(key = "zip")
//     public int getZipCode() {
//         return zipCode;
//     }

//     public void setZipCode(int zipCode) {
//         this.zipCode = zipCode;
//     }

//     @SerializedJSONObjectValue(key = "state")
//     public String getState() {
//         return state;
//     }

//     public void setState(String state) {
//         this.state = state;
//     }

//     @SerializedJSONObjectValue(key = "city")
//     public String getCity() {
//         return city;
//     }

//     public void setCity(String city) {
//         this.city = city;
//     }

//     @SerializedJSONObjectValue(key = "street_name")
//     public String getStreetName() {
//         return streetName;
//     }

//     public void setStreetName(String streetName) {
//         this.streetName = streetName;
//     }

//     @SerializedJSONObjectValue(key = "street_number")
//     public int getStreetNumber() {
//         return streetNumber;
//     }

//     public void setStreetNumber(int streetNumber) {
//         this.streetNumber = streetNumber;
//     }
// }
