package kg.shop.model;

public enum OrderStatus {
    NEW("Новый"),
    CONFIRMED("Подтверждён"),
    SHIPPED("Отправлен"),
    DONE("Выполнен"),
    CANCELLED("Отменён");

    private final String title;
    OrderStatus(String title) { this.title = title; }
    public String getTitle() { return title; }
}