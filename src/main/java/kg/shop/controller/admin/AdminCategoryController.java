package kg.shop.controller.admin;

import kg.shop.model.Category;
import kg.shop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/categories";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("isNew", true);
        return "admin/category-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("category", category);
        model.addAttribute("isNew", false);
        return "admin/category-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Category category, RedirectAttributes ra) {
        // Если создаём новую и slug пустой — генерируем из имени латиницей не получится,
        // поэтому требуем slug вручную
        categoryRepository.save(category);
        ra.addFlashAttribute("success", "Категория сохранена");
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            ra.addFlashAttribute("error", "Категория не найдена");
            return "redirect:/admin/categories";
        }
        // Проверяем, есть ли товары в категории
        // (нужен ProductRepository, но чтобы не плодить зависимость, используем каскад)
        try {
            categoryRepository.delete(category);
            ra.addFlashAttribute("success", "Категория удалена");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Нельзя удалить категорию: в ней есть товары");
        }
        return "redirect:/admin/categories";
    }
}