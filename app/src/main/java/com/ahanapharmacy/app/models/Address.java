package com.ahanapharmacy.app.models;

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


public class Address {

    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String district = "Bankura";
    private String state = "West Bengal";
    private String country = "India";
    private Integer pin;
    private String name;


/*    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();*/


    public Address() {
    }

    /**
     *
     * @return
     * The addressLine1
     */
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     *
     * @param addressLine1
     * The address_line_1
     */
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    /**
     *
     * @return
     * The addressLine2
     */
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     *
     * @param addressLine2
     * The address_line_2
     */
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    /**
     *
     * @return
     * The landmark
     */
    public String getLandmark() {
        return landmark;
    }

    /**
     *
     * @param landmark
     * The landmark
     */
    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    /**
     *
     * @return
     * The district
     */
    public String getDistrict() {
        return district;
    }

    /**
     *
     * @param district
     * The district
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     *
     * @return
     * The state
     */
    public String getState() {
        return state;
    }

    /**
     *
     * @param state
     * The state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @return
     * The country
     */
    public String getCountry() {
        return country;
    }

    /**
     *
     * @param country
     * The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     *
     * @return
     * The pin
     */
    public Integer getPin() {
        return pin;
    }

    /**
     *
     * @param pin
     * The pin
     */
    public void setPin(Integer pin) {
        this.pin = pin;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The address identifier name
     */
    public void setName(String name) {
        this.name = name;
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
