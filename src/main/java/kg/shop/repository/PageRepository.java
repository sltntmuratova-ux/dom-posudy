package kg.shop.repository;

import kg.shop.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Long> {

    Optional<Page> findBySlugAndPublishedTrue(String slug);

    List<Page> findByShowInFooterTrueAndPublishedTrueOrderBySortOrderAsc();

    List<Page> findAllByOrderBySortOrderAsc();
}