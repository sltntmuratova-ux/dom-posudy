package kg.shop.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать папку uploads", e);
        }
    }

    /**
     * Сохраняет файл и возвращает URL вида /uploads/abc.jpg
     */
    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf(".")).toLowerCase();
        }

        // Проверка расширения
        if (!ext.matches("\\.(jpg|jpeg|png|webp|gif|mp4|webm|mov)")) {
            throw new RuntimeException("Недопустимый формат файла: " + ext);
        }

        String fileName = UUID.randomUUID() + ext;
        try {
            Files.copy(file.getInputStream(),
                    root.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл", e);
        }

        return "/uploads/" + fileName;
    }

    /** Удаляет файл по URL вида /uploads/abc.jpg */
    public void delete(String url) {
        if (url == null || !url.startsWith("/uploads/")) return;
        try {
            Path p = root.resolve(url.substring("/uploads/".length()));
            Files.deleteIfExists(p);
        } catch (IOException ignored) {}
    }
}