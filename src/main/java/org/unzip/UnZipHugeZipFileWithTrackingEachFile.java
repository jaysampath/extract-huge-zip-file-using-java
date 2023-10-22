package org.unzip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnZipHugeZipFileWithTrackingEachFile {
    public void processZipFile(String zipFilePath)  {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        startZipFileExtraction(zipFilePath, executorService);
    }

    private void startZipFileExtraction(String tempZipFilePath, ExecutorService executorService) {
        List<Future> futures = new ArrayList<>();

        try (ZipFile zipFile = new ZipFile(tempZipFilePath)) {

            zipFile.stream().forEach(zipEntry -> {
                Future future = executorService.submit(() -> {
                    try {
                        Files.copy(zipFile.getInputStream(zipEntry),
                                new File("" + zipEntry.getName()).toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                futures.add(future);
            });
            executorService.shutdown();
            for (Future f : futures) {
                try {
                   f.get();
                } catch (Exception e) {
                    executorService.shutdownNow();
                    executorService.awaitTermination(10, TimeUnit.SECONDS);
                    throw new RuntimeException("Something went wrong while extracting", e);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
