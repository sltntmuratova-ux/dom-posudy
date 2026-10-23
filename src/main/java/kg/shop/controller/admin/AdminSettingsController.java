package kg.shop.controller.admin;

import kg.shop.dto.ShopSettings;
import kg.shop.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class AdminSettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public String form(Model model) {
        model.addAttribute("form", settingsService.get());
        return "admin/settings";
    }

    @PostMapping
    public String save(@ModelAttribute("form") ShopSettings form,
                       RedirectAttributes ra) {
        settingsService.save(form);
        ra.addFlashAttribute("success", "Настройки сохранены");
        return "redirect:/admin/settings";
    }
}