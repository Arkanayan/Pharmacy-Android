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


import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Map;


public class User {


    private String uid;
    private String firstName = "";
    private String lastName = "";
    private String phoneNumber = "";
    private String emailAddress = "";
    private long createdAt;
    private boolean isAdmin = false;
    private boolean isBanned = false;
    private int totalOrders = 0;

    private String fcmRegId = "";

/*
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();*/

    public User() {
    }

    /**
     *
     * @return
     * The uid
     */
    public String getUid() {
        return uid;
    }

    /**
     *
     * @param uid
     * The uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     *
     * @return
     * The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     * The first_name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     * The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     * The last_name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return
     * The phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     *
     * @param phoneNumber
     * The phone_number
     */
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
    public Map<String, String> getCreatedAt() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public Long getCreatedAtLong() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     * The created_at
     */
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    /*
        public java.util.Map<String, String> getCreatedAt() {
            return ServerValue.TIMESTAMP;
        }
    */
    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public String getFcmRegId() {
        return fcmRegId;
    }

    public void setFcmRegId(String fcmRegId) {
        this.fcmRegId = fcmRegId;
    }

    /*    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }*/

}