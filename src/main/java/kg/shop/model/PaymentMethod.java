package kg.shop.model;

public enum PaymentMethod {
    CASH("Наличными при получении"),
    TRANSFER("Переводом на карту");

    private final String title;
    PaymentMethod(String title) { this.title = title; }
    public String getTitle() { return title; }
}