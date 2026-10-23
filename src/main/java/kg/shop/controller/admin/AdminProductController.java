package kg.shop.controller.admin;

import jakarta.validation.Valid;
import kg.shop.dto.ProductForm;
import kg.shop.model.Category;
import kg.shop.model.Product;
import kg.shop.repository.CategoryRepository;
import kg.shop.repository.ProductRepository;
import kg.shop.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import kg.shop.model.ProductMedia;

import java.util.List;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    /* ============ НОВЫЙ ТОВАР ============ */

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new ProductForm());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("isNew", true);
        return "admin/product-form";
    }

    /* ============ РЕДАКТИРОВАНИЕ ============ */

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product p = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ProductForm form = new ProductForm(
                p.getId(), p.getName(), p.getSlug(), p.getDescription(),
                p.getPrice(), p.getOldPrice(), p.getStock(), p.getMaterial(),
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getImageUrl()
        );

        model.addAttribute("form", form);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("isNew", false);
        return "admin/product-form";
    }

    /* ============ СОХРАНЕНИЕ (создание или обновление) ============ */

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("form") ProductForm form,
                       BindingResult result,
                       @RequestParam(value = "imageFile", required = false) MultipartFile[] files, // Изменено на массив
                       Model model,
                       RedirectAttributes ra) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("isNew", form.getId() == null);
            return "admin/product-form";
        }

        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        Product product;
        if (form.getId() == null) {
            product = new Product();
        } else {
            product = productRepository.findById(form.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        }

        product.setName(form.getName());
        product.setSlug(form.getSlug());
        product.setDescription(form.getDescription());
        product.setPrice(form.getPrice());
        product.setOldPrice(form.getOldPrice());
        product.setStock(form.getStock());
        product.setMaterial(form.getMaterial());
        product.setCategory(category);

        // Сохраняем товар, чтобы у него появился ID (нужно для медиа)
        product = productRepository.save(product);

        // Обработка новых файлов
        if (files != null && files.length > 0) {
            int order = product.getMedia().size();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String url = fileStorageService.save(file);
                    String type = file.getContentType() != null && file.getContentType().startsWith("video") ? "VIDEO" : "IMAGE";

                    ProductMedia media = new ProductMedia();
                    media.setProduct(product);
                    media.setUrl(url);
                    media.setType(type);
                    media.setSortOrder(order++);
                    product.getMedia().add(media);

                    // Если это первое фото и у товара нет главного imageUrl — ставим его
                    if (product.getImageUrl() == null && type.equals("IMAGE")) {
                        product.setImageUrl(url);
                    }
                }
            }
            productRepository.save(product); // Сохраняем связи
        } else if (product.getImageUrl() == null) {
            product.setImageUrl("/img/placeholder.svg");
            productRepository.save(product);
        }

        ra.addFlashAttribute("success", "Товар сохранён");
        return "redirect:/admin/products";
    }

    /* ============ УДАЛЕНИЕ ============ */

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        productRepository.findById(id).ifPresent(p -> {
            if (p.getImageUrl() != null && p.getImageUrl().startsWith("/uploads/")) {
                fileStorageService.delete(p.getImageUrl());
            }
            productRepository.delete(p);
        });
        ra.addFlashAttribute("success", "Товар удалён");
        return "redirect:/admin/products";
    }
}