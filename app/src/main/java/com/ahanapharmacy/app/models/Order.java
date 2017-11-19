package com.ahanapharmacy.app.models;

/**
 * Created by arka on 4/30/16.
 */
/**Schema
 {
 "uid": "34343434",
 "address": "dfererer",
 "prescription_url": "dfdfdfdf",
 "price": 3434.3434,
 "shipping_charge": 334.3434,
 "created_at": 343434
 "is_completed": true,
 "is_canceled": true,
 "note": "",
 "status": "PENDING" OR "CLOSED"
 }
 */

import android.support.annotation.StringDef;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;


public class Order {

    private String uid;
    private String address;
    private String prescriptionUrl;
    private Double price = 0.0;
    private Double shippingCharge = 0.0;
    private Long createdAt;
    private String orderId;
    private String status = Status.PENDING;
    private String note = "";
    private String orderPath;
    private String sellerNote = "";


    @StringDef({Status.PENDING, Status.ACKNOWLEDGED, Status.CONFIRMED, Status.CANCELED, Status.COMPLETED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {

         String PENDING = "PENDING";
         String ACKNOWLEDGED = "ACKNOWLEDGED";
         String CONFIRMED = "CONFIRMED";
         String CANCELED = "CANCELED";
         String COMPLETED = "COMPLETED";
    }

    public Order() {
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
     * The address
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @param address
     * The address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @return
     * The prescriptionUrl
     */
    public String getPrescriptionUrl() {
        return prescriptionUrl;
    }

    /**
     *
     * @param prescriptionUrl
     * The prescription_url
     */
    public void setPrescriptionUrl(String prescriptionUrl) {
        this.prescriptionUrl = prescriptionUrl;
    }

    /**
     *
     * @return
     * The price
     */
    public Double getPrice() {
        return price;
    }

    /**
     *
     * @param price
     * The price
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     *
     * @return
     * The shippingCharge
     */
    public Double getShippingCharge() {
        return shippingCharge;
    }

    /**
     *
     * @param shippingCharge
     * The shipping_charge
     */
    public void setShippingCharge(Double shippingCharge) {
        this.shippingCharge = shippingCharge;
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
    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Status
    public String getStatus() {
        return status;
    }

    @Status
    public void setStatus(@Status String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOrderPath() {
        return orderPath;
    }

    public void setOrderPath(String orderPath) {
        this.orderPath = orderPath;
    }

    public String getSellerNote() {
        return sellerNote;
    }

    public void setSellerNote(String sellerNote) {
        this.sellerNote = sellerNote;
    }

/*    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }*/

    @Override
    public boolean equals(Object o) {
        return (o instanceof Order) && this.getOrderId().equals(((Order) o).getOrderId());
    }

}