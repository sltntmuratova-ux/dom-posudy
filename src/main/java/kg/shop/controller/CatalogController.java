package kg.shop.controller;

import kg.shop.model.Product;
import kg.shop.repository.CategoryRepository;
import kg.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String catalog(@RequestParam(required = false) String category,
                          @RequestParam(required = false) String q,
                          @RequestParam(defaultValue = "0") int page,
                          Model model) {

        // Выводим по 12 товаров на страницу, сортируем от новых к старым
        Pageable pageable = PageRequest.of(page, 12, Sort.by("id").descending());
        Page<Product> products;

        boolean hasCategory = category != null && !category.isBlank();
        boolean hasQuery = q != null && !q.isBlank();

        // Выбираем нужный метод в зависимости от того, что передано в URL
        if (hasCategory && hasQuery) {
            products = productRepository.findByCategoryAndSearch(category, q, pageable);
        } else if (hasCategory) {
            products = productRepository.findByCategorySlugPaged(category, pageable);
        } else if (hasQuery) {
            products = productRepository.searchByName(q, pageable);
        } else {
            products = productRepository.findAllPaged(pageable);
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("currentCategory", category);
        model.addAttribute("q", q); // Передаем поисковый запрос обратно в шаблон

        return "catalog/list";
    }

    @GetMapping("/{id}")
    public String product(@PathVariable Long id, Model model) {
        Product product = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        return "catalog/detail";
    }
}