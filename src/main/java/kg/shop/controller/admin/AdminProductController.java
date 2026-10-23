package kg.shop.controller.admin;

import jakarta.validation.Valid;
import kg.shop.dto.ProductForm;
import kg.shop.model.Category;
import kg.shop.model.Product;
import kg.shop.model.ProductMedia;
import kg.shop.repository.CategoryRepository;
import kg.shop.repository.ProductMediaRepository;
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

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final ProductMediaRepository productMediaRepository;

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
        // Используем метод, который подтягивает и категорию, и медиа
        Product p = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ProductForm form = new ProductForm(
                p.getId(), p.getName(), p.getSlug(), p.getDescription(),
                p.getPrice(), p.getOldPrice(), p.getStock(), p.getMaterial(),
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getImageUrl()
        );

        model.addAttribute("form", form);
        model.addAttribute("product", p);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("isNew", false);
        return "admin/product-form";
    }

    /* ============ СОХРАНЕНИЕ (создание или обновление) ============ */

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("form") ProductForm form,
                       BindingResult result,
                       @RequestParam(value = "imageFile", required = false) MultipartFile[] files,
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
            // Обязательно подтягиваем медиа, чтобы не перетереть существующие
            product = productRepository.findByIdWithCategory(form.getId())
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

    /* ============ УДАЛЕНИЕ ТОВАРА ============ */

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

    /* ============ УДАЛЕНИЕ ФАЙЛА (МЕДИА) ============ */

    @PostMapping("/{productId}/media/{mediaId}/delete")
    public String deleteMedia(@PathVariable Long productId,
                              @PathVariable Long mediaId,
                              RedirectAttributes ra) {
        // ИСПОЛЬЗУЕМ findByIdWithCategory, чтобы медиа подтянулось
        Product product = productRepository.findByIdWithCategory(productId).orElse(null);

        if (product != null) {
            ProductMedia mediaToDelete = productMediaRepository.findById(mediaId).orElse(null);

            if (mediaToDelete != null) {
                String deletedUrl = mediaToDelete.getUrl();

                // Удаляем из БД
                productMediaRepository.delete(mediaToDelete);

                // Обновляем список медиа у товара в памяти
                product.getMedia().removeIf(m -> m.getId().equals(mediaId));

                // Если мы удалили файл, который был главной картинкой (imageUrl)
                if (deletedUrl.equals(product.getImageUrl())) {
                    if (!product.getMedia().isEmpty()) {
                        // Берем первый оставшийся файл (фото или видео)
                        product.setImageUrl(product.getMedia().get(0).getUrl());
                    } else {
                        // Если больше ничего нет — ставим заглушку
                        product.setImageUrl("/img/placeholder.svg");
                    }
                }

                // Удаляем физический файл с диска
                fileStorageService.delete(deletedUrl);

                // Сохраняем изменения товара
                productRepository.save(product);
            }
        }

        ra.addFlashAttribute("success", "Файл удалён");
        return "redirect:/admin/products/" + productId + "/edit";
    }
}