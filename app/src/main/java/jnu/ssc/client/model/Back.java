package jnu.ssc.client.model;

public class Back {
    private String orderId;
    private String clothesId;
    private int backAmount;
    private String shelf;
    private int position;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getClothesId() {
        return clothesId;
    }

    public void setClothesId(String clothesId) {
        this.clothesId = clothesId;
    }

    public int getBackAmount() {
        return backAmount;
    }

    public void setBackAmount(int backAmount) {
        this.backAmount = backAmount;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
