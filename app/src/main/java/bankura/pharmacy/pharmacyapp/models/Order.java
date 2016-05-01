package bankura.pharmacy.pharmacyapp.models;

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
 }
 */

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "uid",
        "address",
        "prescription_url",
        "price",
        "shipping_charge",
        "created_at",
        "is_completed",
        "is_canceled",
        "status"
})
public class Order {

    @JsonProperty("uid")
    private String uid;
    @JsonProperty("address")
    private String address;
    @JsonProperty("prescription_url")
    private String prescriptionUrl;
    @JsonProperty("price")
    private Double price = 0.0;
    @JsonProperty("shipping_charge")
    private Double shippingCharge = 0.0;
    @JsonProperty("created_at")
    private Long createdAt;
    @JsonProperty("is_completed")
    private boolean isCompleted = false;
    @JsonProperty("is_canceled")
    private boolean isCanceled = false;
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("status")
    private Status status = Status.OPEN;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public enum Status { OPEN, CLOSED, CANCELED;


        // used for serializing Status to int , instead of "OPEN" or "CLOSED"
        @JsonValue
        public int getValue() {
            return this.ordinal();
        }


        // used for reading data back from json (int) to Status type
        @JsonCreator
        public static Status fromValue(int typeCode) {
            for (Status c : Status.values()) {
                if (c.ordinal() == typeCode) {
                    return c;
                }
            }
            throw new IllegalArgumentException("Invalid Status type code: " + typeCode);
        }

    }

    public Order() {
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
     * The address
     */
    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    /**
     *
     * @param address
     * The address
     */
    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @return
     * The prescriptionUrl
     */
    @JsonProperty("prescription_url")
    public String getPrescriptionUrl() {
        return prescriptionUrl;
    }

    /**
     *
     * @param prescriptionUrl
     * The prescription_url
     */
    @JsonProperty("prescription_url")
    public void setPrescriptionUrl(String prescriptionUrl) {
        this.prescriptionUrl = prescriptionUrl;
    }

    /**
     *
     * @return
     * The price
     */
    @JsonProperty("price")
    public Double getPrice() {
        return price;
    }

    /**
     *
     * @param price
     * The price
     */
    @JsonProperty("price")
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     *
     * @return
     * The shippingCharge
     */
    @JsonProperty("shipping_charge")
    public Double getShippingCharge() {
        return shippingCharge;
    }

    /**
     *
     * @param shippingCharge
     * The shipping_charge
     */
    @JsonProperty("shipping_charge")
    public void setShippingCharge(Double shippingCharge) {
        this.shippingCharge = shippingCharge;
    }

    /**
     *
     * @return
     * The createdAt
     */
    @JsonProperty("created_at")
    public Long getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     * The created_at
     */
    @JsonProperty("created_at")
    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("is_completed")
    public boolean isCompleted() {

        return isCompleted;
    }
    @JsonProperty("is_completed")
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @JsonProperty("is_canceled")
    public boolean isCanceled() {
        return isCanceled;
    }

    @JsonProperty("is_canceled")
    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    @JsonProperty("order_id")
    public String getOrderId() {
        return orderId;
    }

    @JsonProperty("order_id")
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Order) && this.getOrderId().equals(((Order) o).getOrderId());
    }
}