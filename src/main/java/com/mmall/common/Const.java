package com.mmall.common;

/**
 * Created by Flash on 2017/8/11.
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";
    public static final String EMIAL = "email";
    public static final String USERNAME = "username";

    public interface Role{
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管理员
    }

    public interface Cart{
        int CHECKED = 1;//选中状态
        int UN_CHECKED = 0;//未选中
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public enum PaymentTypeEnum {
        ONLINE_PAY(1,"在线支付")
        ;
        private int code;
        private String desc;
        PaymentTypeEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        public int getCode() {
            return code;
        }
        public String getDesc() {
            return desc;
        }
    }

    public enum OrderStatusEnum {
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");

        private int code;
        private String value;
        OrderStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        public int getCode() {
            return code;
        }
        public String getValue() {
            return value;
        }
    }
    public enum PlatFormEnum{
        ALIPAY(1,"支付宝")

        ;
        private int code;
        private String value;
        PlatFormEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }
        public int getCode() {
            return code;
        }
        public String getValue() {
            return value;
        }
    }

    public interface AlipayCallback {
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum ProductStatusEnum {
        ON_SALE(1,"在线")
        ;
        private int code;
        private String desc;
        ProductStatusEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        public int getCode() {
            return code;
        }
        public String getDesc() {
            return desc;
        }
    }


}


