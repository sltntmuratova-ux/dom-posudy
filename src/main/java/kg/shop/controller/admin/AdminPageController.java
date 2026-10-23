package kg.shop.controller.admin;

import kg.shop.model.Page;
import kg.shop.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/pages")
@RequiredArgsConstructor
public class AdminPageController {

    private final PageRepository pageRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pages", pageRepository.findAllByOrderBySortOrderAsc());
        return "admin/pages";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        Page page = new Page();
        page.setShowInFooter(true);
        page.setPublished(true);
        page.setSortOrder(0);
        model.addAttribute("page", page);
        model.addAttribute("isNew", true);
        return "admin/page-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("page", page);
        model.addAttribute("isNew", false);
        return "admin/page-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Page page, RedirectAttributes ra) {
        page.setSlug(page.getSlug().toLowerCase().trim().replaceAll("[^a-z0-9-]", "-"));
        if (page.getId() == null) page.setCreatedAt(LocalDateTime.now());
        page.setUpdatedAt(LocalDateTime.now());
        pageRepository.save(page);
        ra.addFlashAttribute("success", "Страница сохранена");
        return "redirect:/admin/pages";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        pageRepository.deleteById(id);
        ra.addFlashAttribute("success", "Страница удалена");
        return "redirect:/admin/pages";
    }
}