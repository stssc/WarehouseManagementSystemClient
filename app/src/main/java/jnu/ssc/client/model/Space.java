package jnu.ssc.client.model;

public class Space {
    private String shelf;

    private Integer position;

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf == null ? null : shelf.trim();
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}