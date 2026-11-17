package kg.shop.repository;

import kg.shop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByNumber(String number);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items ORDER BY o.createdAt DESC")
    List<Order> findAllWithItems();

    // НОВЫЙ МЕТОД: Поиск заказов по номеру телефона с подгрузкой состава
    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Order> findByCustomerPhoneOrderByCreatedAtDesc(String customerPhone);
}





