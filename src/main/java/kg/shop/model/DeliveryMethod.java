package kg.shop.model;

public enum DeliveryMethod {
    COURIER("Курьером"),
    PICKUP("Самовывоз");

    private final String title;
    DeliveryMethod(String title) { this.title = title; }
    public String getTitle() { return title; }
}