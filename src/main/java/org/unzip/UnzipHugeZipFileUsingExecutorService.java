package org.unzip;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnzipHugeZipFileUsingExecutorService {

    private void processZipFileUsingParallelExecution(String zipFilePath)  {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        startZipFileExtraction(zipFilePath, executorService);
    }

    private void startZipFileExtraction(String tempZipFilePath, ExecutorService executorService) {
        List<String> filesFoundInZip;
        Set<String> extractedFiles = new HashSet<>();

        try (ZipFile zipFile = new ZipFile(tempZipFilePath)) {
            filesFoundInZip = zipFile.stream().map(ZipEntry::getName).collect(Collectors.toList());

            zipFile.stream().forEach(zipEntry -> executorService.submit(() -> {
                try {
                    Files.copy(zipFile.getInputStream(zipEntry),
                            new File("" + zipEntry.getName()).toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                    extractedFiles.add(zipEntry.getName());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));

            shutdownExecutorServiceAndAwait(executorService);
            checkIfAllFilesGotExtracted(filesFoundInZip, extractedFiles);
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }

    private void checkIfAllFilesGotExtracted(List<String> filesFoundInZip, Set<String> extractedFiles) {
        if (filesFoundInZip.size() != extractedFiles.size()) {
            filesFoundInZip.removeAll(extractedFiles);
            throw new RuntimeException("Not able to extract these files - " + filesFoundInZip);
        }
     }

    private void shutdownExecutorServiceAndAwait(ExecutorService executorService) {
        executorService.shutdown();

        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            throw new RuntimeException(e);
        }
    }

}
