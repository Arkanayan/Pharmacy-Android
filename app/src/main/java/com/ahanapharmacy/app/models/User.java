package com.ahanapharmacy.app.models;

/**
 * Created by arka on 4/29/16.
 */

/**Schema
 *
 *
 {
 "uid": 34343434,
 "first_name": "fname",
 "last_name": "last",
 "phone_number": "343434",
 "created_at": 34343434,
 "is_admin": false
 "is_banned": false
 }
 */

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "uid",
        "first_name",
        "last_name",
        "phone_number",
        "email_address",
        "created_at",
        "is_admin",
        "is_banned",
        "total_orders"
        })
public class User {


    @JsonProperty("uid")
    private String uid;
    @JsonProperty("first_name")
    private String firstName = "";
    @JsonProperty("last_name")
    private String lastName = "";
    @JsonProperty("phone_number")
    private String phoneNumber = "";
    @JsonProperty("email_address")
    private String emailAddress = "";
    @JsonProperty("created_at")
    private long createdAt;
    @JsonProperty("is_admin")
    private boolean isAdmin = false;
    @JsonProperty("is_banned")
    private boolean isBanned = false;
    @JsonProperty("total_orders")
    private int totalOrders = 0;


    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public User() {
    }

    /**
     *
     * @return
     * The uid
     */
    @JsonProperty("uid")
    public String getUid() {
        return uid;
    }

    /**
     *
     * @param uid
     * The uid
     */
    @JsonProperty("uid")
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     *
     * @return
     * The firstName
     */
    @JsonProperty("first_name")
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     * The first_name
     */
    @JsonProperty("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     * The lastName
     */
    @JsonProperty("last_name")
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     * The last_name
     */
    @JsonProperty("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return
     * The phoneNumber
     */
    @JsonProperty("phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     *
     * @param phoneNumber
     * The phone_number
     */
    @JsonProperty("phone_number")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     *  Populate the timestamp server side
     * @return Firebase ServerValue.TIMESTAMP
     * The createdAt
     */
    @JsonProperty("created_at")
    public Map<String, String> getCreatedAt() {
        return ServerValue.TIMESTAMP;
    }

    @JsonIgnore
    public Long getCreatedAtLong() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     * The created_at
     */
    @JsonProperty("created_at")
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

/*
    public java.util.Map<String, String> getCreatedAt() {
        return ServerValue.TIMESTAMP;
    }
*/
    @JsonProperty("is_admin")
    public boolean isAdmin() {
        return isAdmin;
    }

    @JsonProperty("is_banned")
    public boolean isBanned() {
        return isBanned;
    }

    @JsonProperty("total_orders")
    public int getTotalOrders() {
        return totalOrders;
    }

    @JsonProperty("total_orders")
    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}