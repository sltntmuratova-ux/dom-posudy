package kg.shop.service;

import kg.shop.model.OrderStatus;
import kg.shop.cart.Cart;
import kg.shop.cart.CartItem;
import kg.shop.dto.CheckoutForm;
import kg.shop.model.Order;
import kg.shop.model.OrderItem;
import kg.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(Cart cart, CheckoutForm form) {
        Order order = new Order();
        order.setCustomerName(form.getName());
        order.setCustomerPhone(form.getPhone());
        order.setCustomerEmail(form.getEmail());
        order.setCity(form.getCity());
        order.setAddress(form.getAddress());
        order.setDeliveryMethod(form.getDeliveryMethod());
        order.setPaymentMethod(form.getPaymentMethod());
        order.setComment(form.getComment());
        order.setTotal(cart.getTotalPrice());

        for (CartItem ci : cart.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setProductName(ci.getProduct().getName());
            oi.setPrice(ci.getProduct().getPrice());
            oi.setQuantity(ci.getQuantity());
            oi.setSubtotal(ci.getTotal());
            order.getItems().add(oi);
        }

        orderRepository.save(order);

        // Генерируем красивый номер после сохранения (id уже есть)
        order.setNumber(String.format("%06d", order.getId()));
        orderRepository.save(order);

        return order;
    }

    public Order findByNumber(String number) {
        return orderRepository.findByNumber(number).orElse(null);
    }

    public List<Order> findAll() {
        return orderRepository.findAllWithItems();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public void updateStatus(Long id, kg.shop.model.OrderStatus status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
    }
}