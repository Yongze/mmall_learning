package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by yw850 on 6/22/2017.
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";
    public interface Role{
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public enum ProductStatusEnum{
        ON_SALE(1,"Online");

        private String value;
        private int code;

        ProductStatusEnum(int code,String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public interface productListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }
}
