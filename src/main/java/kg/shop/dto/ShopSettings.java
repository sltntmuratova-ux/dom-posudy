package kg.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopSettings {

    // Общие
    private String shopName = "Дом посуды";
    private String phone = "+996 555 000 000";
    private String email = "info@dom-posudy.kg";
    private String address = "г. Бишкек";
    private String currency = "с";

    // Соцсети
    private String instagram = "";
    private String whatsapp = "";
    private String telegram = "";

    // Главная
    private String heroTitle = "Красивая посуда, которая собирает за столом";
    private String heroSubtitle = "Керамика, фарфор и чугун. Для уютных семейных ужинов и праздничных застолий. Доставка по Кыргызстану.";
    private String aboutText = "Мы — небольшой магазин посуды в Бишкеке. Работаем с 2020 года. Отбираем товары, которые сами используем дома.";

    // Доставка
    private String deliveryCities = "Бишкек,Ош,Джалал-Абад,Каракол,Токмок,Кара-Балта,Узген,Нарын,Талас,Баткен,Кант,Кара-Суу";
    private String deliveryPrice = "200";
    private String freeDeliveryFrom = "3000";

    // Футер
    private String requisites = "";

    // SEO
    private String seoDescription = "Купить красивую и качественную посуду в Бишкеке. Керамика, фарфор, чугун. Доставка по всему Кыргызстану.";
    private String seoKeywords = "посуда бишкек, купить посуду, керамика, фарфор, кастрюли, тарелки";
    private String analyticsCode = ""; // Сюда заказчик вставит код Яндекс.Метрики или Google Analytics
}