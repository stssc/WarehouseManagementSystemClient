package jnu.ssc.client.model;

public class InventoryTaskDetail {//盘点任务具体信息
    private int id;
    private String clothesId;
    private String shelf;
    private int position;
    private int amount;
    private String staffId;
    private int state;

    public InventoryTaskDetail(Clothes clothes){
        this.clothesId=clothes.getId();
        this.shelf=clothes.getShelf();
        this.position=clothes.getPosition();
        this.amount=clothes.getAmount();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClothesId() {
        return clothesId;
    }

    public void setClothesId(String clothesId) {
        this.clothesId = clothesId;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
