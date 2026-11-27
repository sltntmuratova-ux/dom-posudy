package kg.shop.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class BackupService {

    private static final String BACKUP_DIR = "backups";

    // 1. АВТОМАТИЧЕСКИЙ БЭКАП (Запускается каждый день в 03:00 ночи)
    @Scheduled(cron = "0 0 3 * * ?")
    public void autoBackup() {
        System.out.println(">>> Запуск автоматического резервного копирования...");
        createBackup("auto_backup");
        cleanOldBackups(); // Удаляем старые архивы
    }

    // 2. РУЧНОЙ БЭКАП (Вызывается из контроллера)
    public String createBackup(String prefix) {
        File backupFolder = new File(BACKUP_DIR);
        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String zipFileName = BACKUP_DIR + "/" + prefix + "_" + timeStamp + ".zip";
        File zipFile = new File(zipFileName);

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            // Добавляем базу данных
            addToZip(new File("data/shop.db"), "data/shop.db", zos);

            // Добавляем папку uploads (фото и видео)
            File uploadsFolder = new File("uploads");
            if (uploadsFolder.exists()) {
                addFolderToZip(uploadsFolder, "uploads", zos);
            }

            System.out.println(">>> Бэкап успешно создан: " + zipFileName);
            return zipFileName;

        } catch (IOException e) {
            System.err.println("Ошибка при создании бэкапа: " + e.getMessage());
            return null;
        }
    }

    // Вспомогательный метод для добавления файла в архив
    private void addToZip(File file, String zipEntryName, ZipOutputStream zos) throws IOException {
        if (!file.exists()) return;

        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(zipEntryName);
            zos.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
            zos.closeEntry();
        }
    }

    // Вспомогательный метод для добавления папки целиком (рекурсивно)
    private void addFolderToZip(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                addFolderToZip(file, parentFolder + "/" + file.getName(), zos);
            } else {
                addToZip(file, parentFolder + "/" + file.getName(), zos);
            }
        }
    }

    // Очистка старых бэкапов (оставляем только 7 последних)
    private void cleanOldBackups() {
        File backupFolder = new File(BACKUP_DIR);
        File[] files = backupFolder.listFiles((dir, name) -> name.startsWith("auto_backup_") && name.endsWith(".zip"));

        if (files == null || files.length <= 7) return;

        // Сортируем по дате изменения (старые сверху)
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));

        // Удаляем самые старые, пока их не останется 7
        int filesToDelete = files.length - 7;
        for (int i = 0; i < filesToDelete; i++) {
            if (files[i].delete()) {
                System.out.println(">>> Удален старый бэкап: " + files[i].getName());
            }
        }
    }
}