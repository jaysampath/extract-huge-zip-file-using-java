package org.unzip;

import java.util.concurrent.TimeUnit;

public class UnZipMain {
    public static void main(String[] args) {
        String zipFilePath = "path/to/zip/file";
        UnzipHugeZipFileUsingExecutorService trackingAtTheEnd = new UnzipHugeZipFileUsingExecutorService();
        UnZipHugeZipFileWithTrackingEachFile trackingEachFile = new UnZipHugeZipFileWithTrackingEachFile();

        long startTime = System.currentTimeMillis();
        trackingAtTheEnd.processZipFile(zipFilePath);
        System.out.println("Tracking at the end. Completion time= "
                + (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - startTime)) + " minutes");

        startTime = System.currentTimeMillis();
        trackingEachFile.processZipFile(zipFilePath);
        System.out.println("Tracking each file. Completion time= "
                + (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - startTime)) + " minutes");
    }
}
