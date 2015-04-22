package it114112tm1415fyp.com.util;

public class OrderItem {
    public int orderId;
    public String user;
    public int numberOfGood;
    public String state;
    public String orderType;
    public String updateTime;

    public OrderItem(int orderId, String user, int numberOfGood, String state, String updateTime, String orderType) {
        this.user = user;
        this.numberOfGood = numberOfGood;
        this.orderId = orderId;
        this.state = state;
        this.updateTime = updateTime;
        this.orderType = orderType;
    }

    public String getUser() {
        return user;
    }

    public int getNumberOfGood() {
        return numberOfGood;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getState() {
        return state;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getOrderType() {
        return orderType;
    }
}
