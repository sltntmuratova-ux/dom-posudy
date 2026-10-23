package kg.shop.controller;

import kg.shop.repository.CategoryRepository;
import kg.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping("/")
    public String home(Model model) {
        // Хиты — первые 4 товара, сортировка по id (можно потом заменить на "популярные")
        var featured = productRepository.findAll(
                PageRequest.of(0, 4, Sort.by("id").descending())
        ).getContent();

        model.addAttribute("featured", featured);
        model.addAttribute("categories", categoryRepository.findAll());
        return "index";
    }
}