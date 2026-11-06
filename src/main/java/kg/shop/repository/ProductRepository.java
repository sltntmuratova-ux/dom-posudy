package kg.shop.repository;

import kg.shop.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 1. Для страницы товара (полная информация)
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.media WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);

    // 2. Для главной страницы (Хиты продаж)
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.media ORDER BY p.id DESC")
    List<Product> findAllWithMedia();

    // 3. Для админки (список товаров)
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.media ORDER BY p.id DESC")
    List<Product> findAllWithCategory();

    // ==========================================
    // 4. НОВЫЕ МЕТОДЫ ДЛЯ ПОИСКА И ПАГИНАЦИИ
    // ==========================================

    @EntityGraph(attributePaths = {"category", "media"})
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Product> searchByName(@Param("query") String query, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "media"})
    @Query("SELECT p FROM Product p WHERE p.category.slug = :slug")
    Page<Product> findByCategorySlugPaged(@Param("slug") String slug, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "media"})
    @Query("SELECT p FROM Product p WHERE p.category.slug = :slug AND LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Product> findByCategoryAndSearch(@Param("slug") String slug, @Param("query") String query, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "media"})
    @Query("SELECT p FROM Product p")
    Page<Product> findAllPaged(Pageable pageable);
}