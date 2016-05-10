package com.apharmacy.app.models;

/**
 * Created by arka on 4/29/16.
 */
/**Schema
 {
 "address_line_1" : "dfdfd",
 "address_line_2" : "dfdfd",
 "landmark": "dfdfd",
 "district": "dfdfd",
 "state": "dfdfd",
 "country": "dfdfd",
 "pin": 343434,
 "name": "dfdfdf"
 }
 */

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "address_line_1",
        "address_line_2",
        "landmark",
        "district",
        "state",
        "country",
        "pin",
        "name"
})
public class Address {

    @JsonProperty("address_line_1")
    private String addressLine1;
    @JsonProperty("address_line_2")
    private String addressLine2;
    @JsonProperty("landmark")
    private String landmark;
    @JsonProperty("district")
    private String district = "Bankura";
    @JsonProperty("state")
    private String state = "West Bengal";
    @JsonProperty("country")
    private String country = "India";
    @JsonProperty("pin")
    private Integer pin;
    @JsonProperty("name")
    private String name;


    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public Address() {
    }

    /**
     *
     * @return
     * The addressLine1
     */
    @JsonProperty("address_line_1")
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     *
     * @param addressLine1
     * The address_line_1
     */
    @JsonProperty("address_line_1")
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    /**
     *
     * @return
     * The addressLine2
     */
    @JsonProperty("address_line_2")
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     *
     * @param addressLine2
     * The address_line_2
     */
    @JsonProperty("address_line_2")
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    /**
     *
     * @return
     * The landmark
     */
    @JsonProperty("landmark")
    public String getLandmark() {
        return landmark;
    }

    /**
     *
     * @param landmark
     * The landmark
     */
    @JsonProperty("landmark")
    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    /**
     *
     * @return
     * The district
     */
    @JsonProperty("district")
    public String getDistrict() {
        return district;
    }

    /**
     *
     * @param district
     * The district
     */
    @JsonProperty("district")
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     *
     * @return
     * The state
     */
    @JsonProperty("state")
    public String getState() {
        return state;
    }

    /**
     *
     * @param state
     * The state
     */
    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @return
     * The country
     */
    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    /**
     *
     * @param country
     * The country
     */
    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     *
     * @return
     * The pin
     */
    @JsonProperty("pin")
    public Integer getPin() {
        return pin;
    }

    /**
     *
     * @param pin
     * The pin
     */
    @JsonProperty("pin")
    public void setPin(Integer pin) {
        this.pin = pin;
    }

    /**
     *
     * @return
     * The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The address identifier name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
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
