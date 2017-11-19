package com.ahanapharmacy.app.Utils;

/**
 * Created by arka on 9/6/16.
 */

public class Analytics {

    public class Param {
        public static final String ORDER_ID = "order_id";
        public static final String ORDER_STATUS = "order_status";
        public static final String ORDER_PRICE = "order_price";
        public static final String ORDER_SHIPPING_CHARGE = "order_shipping_charge";

        public static final String USER_EMAIL_PROVIDED = "user_email_provided";
        public static final String USER_ID = "user_id";
        public static final String USER_NAME = "user_name";
        public static final String USER_PIN_CODE = "user_pin_code";


        public static final String ORDER_TYPE = "order";
        public static final String ORDER_PRESCRIPTION_PROVIDED = "order_presc_provided";
        public static final String ORDER_NOTE_PROVIDED = "order_note_provided";
    }

    public class Event {
        public static final String ORDER_CANCEL = "cancel_order";
        public static final String ORDER_NEW = "new_order";
        public static final String ORDER_CONFIRM = "confirm_order";
        public static final String ORDER_FAILED = "order_failed";
        public static final String EDIT_USER = "edit_user";
        public static final String LOGIN = "login";
        public static final String LOGOUT = "logout";
        public static final String VIEW_MAP = "view_map";

        public static final String CONTACT_DEVELOPER = "contact_developer";

    }
}
