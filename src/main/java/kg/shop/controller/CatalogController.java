package kg.shop.controller;

import kg.shop.model.Product;
import kg.shop.repository.CategoryRepository;
import kg.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Controller
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String catalog(@RequestParam(required = false) String category, Model model) {
        List<Product> products;
        if (category != null && !category.isBlank()) {
            products = productRepository.findByCategorySlug(category);
        } else {
            products = productRepository.findAll();
        }
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("currentCategory", category);
        return "catalog/list";
    }

    @GetMapping("/{id}")
    public String product(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        return "catalog/detail";
    }
}