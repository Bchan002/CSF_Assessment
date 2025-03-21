package vttp.batch5.csf.assessment.server.models;

import java.util.Date;
import java.util.List;

public class PlaceOrders {

    private String orderId;
    private String paymentId;
    private String orderDate;
    private double total;
    private String username;
    private List<productItems> productItems;

    public String getOrderId() {
        return orderId;
    }
    public List<productItems> getProductItems() {
        return productItems;
    }
    public void setProductItems(List<productItems> productItems) {
        this.productItems = productItems;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    public String getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    

}
