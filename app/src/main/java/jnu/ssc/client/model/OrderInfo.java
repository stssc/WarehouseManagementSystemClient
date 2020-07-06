package jnu.ssc.client.model;

public class OrderInfo {
    private String orderId;

    private String clothesId;

    private Integer amount;

    private Integer state;

    private Integer back;

    private Integer backAmount;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getClothesId() {
        return clothesId;
    }

    public void setClothesId(String clothesId) {
        this.clothesId = clothesId == null ? null : clothesId.trim();
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getBack() {
        return back;
    }

    public void setBack(Integer back) {
        this.back = back;
    }

    public Integer getBackAmount() {
        return backAmount;
    }

    public void setBackAmount(Integer backAmount) {
        this.backAmount = backAmount;
    }
}