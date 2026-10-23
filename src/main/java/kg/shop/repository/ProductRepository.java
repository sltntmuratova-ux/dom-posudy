package kg.shop.repository;

import kg.shop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;      // ← обязательно
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategorySlug(String slug);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category ORDER BY p.id DESC")
    List<Product> findAllWithCategory();
}