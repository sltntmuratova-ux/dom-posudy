package kg.shop.controller.admin;

import kg.shop.dto.ChangePasswordForm;
import kg.shop.dto.ShopSettings;
import kg.shop.model.User;
import kg.shop.repository.UserRepository;
import kg.shop.service.BackupService;
import kg.shop.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;

@Controller
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class AdminSettingsController {

    private final SettingsService settingsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BackupService backupService; // ДОБАВЛЕНО: для работы бэкапа

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

    // --- Смена пароля ---
    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("passwordForm") ChangePasswordForm form,
                                 Authentication authentication,
                                 RedirectAttributes ra) {

        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            ra.addFlashAttribute("passwordError", "Новые пароли не совпадают");
            return "redirect:/admin/settings";
        }

        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            ra.addFlashAttribute("passwordError", "Пользователь не найден");
            return "redirect:/admin/settings";
        }

        if (!passwordEncoder.matches(form.getOldPassword(), user.getPassword())) {
            ra.addFlashAttribute("passwordError", "Неверный старый пароль");
            return "redirect:/admin/settings";
        }

        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(user);

        ra.addFlashAttribute("passwordSuccess", "Пароль успешно изменен!");
        return "redirect:/admin/settings";
    }

    // --- Скачивание резервной копии ---
    @GetMapping("/backup/download")
    public ResponseEntity<Resource> downloadBackup() {
        // Создаем бэкап и получаем путь к нему
        String zipPath = backupService.createBackup("manual");

        if (zipPath == null) {
            return ResponseEntity.notFound().build();
        }

        File file = new File(zipPath);
        Resource resource = new FileSystemResource(file);

        // Настраиваем заголовки, чтобы браузер начал скачивание файла, а не открывал его
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}