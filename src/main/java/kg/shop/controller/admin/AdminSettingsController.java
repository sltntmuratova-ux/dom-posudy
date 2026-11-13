package kg.shop.controller.admin;

import kg.shop.dto.ChangePasswordForm;
import kg.shop.dto.ShopSettings;
import kg.shop.model.User;
import kg.shop.repository.UserRepository;
import kg.shop.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class AdminSettingsController {

    private final SettingsService settingsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String form(Model model) {
        model.addAttribute("form", settingsService.get());
        model.addAttribute("passwordForm", new ChangePasswordForm());
        return "admin/settings";
    }

    @PostMapping
    public String save(@ModelAttribute("form") ShopSettings form, RedirectAttributes ra) {
        settingsService.save(form);
        ra.addFlashAttribute("success", "Настройки сохранены");
        return "redirect:/admin/settings";
    }

    // НОВЫЙ МЕТОД: Смена пароля
    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("passwordForm") ChangePasswordForm form,
                                 Authentication authentication,
                                 RedirectAttributes ra) {

        // 1. Проверяем, совпадают ли новые пароли
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            ra.addFlashAttribute("passwordError", "Новые пароли не совпадают");
            return "redirect:/admin/settings";
        }

        // 2. Находим текущего пользователя
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            ra.addFlashAttribute("passwordError", "Пользователь не найден");
            return "redirect:/admin/settings";
        }

        // 3. Проверяем, правильно ли введен старый пароль
        if (!passwordEncoder.matches(form.getOldPassword(), user.getPassword())) {
            ra.addFlashAttribute("passwordError", "Неверный старый пароль");
            return "redirect:/admin/settings";
        }

        // 4. Шифруем и сохраняем новый пароль
        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(user);

        ra.addFlashAttribute("passwordSuccess", "Пароль успешно изменен!");
        return "redirect:/admin/settings";
    }
}