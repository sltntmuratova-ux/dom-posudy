package kg.shop.service;

import jakarta.mail.internet.MimeMessage;
import kg.shop.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SettingsService settingsService;

    // Отправка клиенту
    public void sendOrderConfirmation(Order order) {
        if (order.getCustomerEmail() == null || order.getCustomerEmail().isBlank()) {
            return; // Если клиент не указал email, не отправляем
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(settingsService.get().getEmail(), settingsService.get().getShopName());
            helper.setTo(order.getCustomerEmail());
            helper.setSubject("Ваш заказ №" + order.getNumber() + " принят!");

            String body = "Здравствуйте, " + order.getCustomerName() + "!\n\n" +
                    "Спасибо за ваш заказ в магазине " + settingsService.get().getShopName() + ".\n" +
                    "Номер вашего заказа: №" + order.getNumber() + "\n" +
                    "Сумма заказа: " + order.getTotal() + " сом.\n\n" +
                    "Мы свяжемся с вами в ближайшее время для подтверждения.\n\n" +
                    "С уважением,\n" + settingsService.get().getShopName() + "\n" +
                    settingsService.get().getPhone();

            helper.setText(body, false);
            mailSender.send(message);
            System.out.println(">>> Письмо клиенту отправлено: " + order.getCustomerEmail());

        } catch (Exception e) {
            System.err.println("Ошибка отправки письма клиенту: " + e.getMessage());
        }
    }

    // Отправка администратору
    public void sendNewOrderToAdmin(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(settingsService.get().getEmail(), "Робот магазина");
            helper.setTo(settingsService.get().getEmail()); // Отправляем на почту магазина
            helper.setSubject("🔔 Новый заказ №" + order.getNumber());

            String body = "Поступил новый заказ!\n\n" +
                    "Клиент: " + order.getCustomerName() + "\n" +
                    "Телефон: " + order.getCustomerPhone() + "\n" +
                    "Адрес: " + order.getCity() + ", " + order.getAddress() + "\n" +
                    "Сумма: " + order.getTotal() + " сом.\n\n" +
                    "Посмотреть детали: http://localhost:8080/admin/orders/" + order.getId();

            helper.setText(body, false);
            mailSender.send(message);
            System.out.println(">>> Письмо админу отправлено: " + settingsService.get().getEmail());

        } catch (Exception e) {
            System.err.println("Ошибка отправки письма админу: " + e.getMessage());
        }
    }
}