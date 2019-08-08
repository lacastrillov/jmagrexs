/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author e11001a
 */
public class ZipFileService {
    
    public static void addToZip(String sourceFile, String compressedFile) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(compressedFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();
    }
    
    public static void addToZip(String[] sourceFiles, String compressedFile) throws FileNotFoundException, IOException{
        List<String> srcFiles = Arrays.asList(sourceFiles);
        FileOutputStream fos = new FileOutputStream(compressedFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (String srcFile : srcFiles) {
            File fileToZip = new File(srcFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);
 
            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        fos.close();
    }
    
    public static void addDirectoryToZip(String sourceDirectory, String compressedFile) throws FileNotFoundException, IOException{
        FileOutputStream fos = new FileOutputStream(compressedFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceDirectory);
 
        zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
    }
    
    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
    
    public static void unzip(String compressedFile, String uncompressedDirectory) throws FileNotFoundException, IOException {
        //Open the file
        ZipFile file = new ZipFile(compressedFile);
        FileSystem fileSystem = FileSystems.getDefault();
        //Get file entries
        Enumeration<? extends ZipEntry> entries = file.entries();

        //We will unzip files in this folder
        Files.createDirectory(fileSystem.getPath(uncompressedDirectory));

        //Iterate over entries
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            //If directory then create a new directory in uncompressed folder
            if (entry.isDirectory()) {
                System.out.println("Creating Directory:" + uncompressedDirectory + entry.getName());
                Files.createDirectories(fileSystem.getPath(uncompressedDirectory + entry.getName()));
            } //Else create the file
            else {
                InputStream is = file.getInputStream(entry);
                BufferedInputStream bis = new BufferedInputStream(is);
                String uncompressedFileName = uncompressedDirectory + entry.getName();
                Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
                Files.createFile(uncompressedFilePath);
                FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);
                while (bis.available() > 0) {
                    fileOutput.write(bis.read());
                }
                fileOutput.close();
                System.out.println("Written :" + entry.getName());
            }
        }
    }
    
    /*public static void unzip(String compressedFile, String destinationDir) throws FileNotFoundException, IOException{
        File destDir = new File(destinationDir);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(compressedFile));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }
    
    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
         
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
         
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
         
        return destFile;
    }*/
    
    public static void main(String[] args){
        try {
            /*String[] sourceFiles= {
                "C:\\Users\\e11001a\\Downloads\\LIBS\\db2jcc-1.4.2.jar",
                "C:\\Users\\e11001a\\Downloads\\LIBS\\classmate-1.4.0.jar",
                "C:\\Users\\e11001a\\Downloads\\LIBS\\hibernate-validator-5.3.6.Final.jar"};
            ZipFileService.addToZip(sourceFiles, "C:\\Users\\e11001a\\Downloads\\LIBS\\jmgzip.zip");*/
            String uncompressedFile= "C:\\Users\\e11001a\\Downloads\\LIBS\\lib.oktasdk-java";
            String compressedFile= "C:\\Users\\e11001a\\Downloads\\LIBS\\ZIP_FILE\\03-Proyectos.zip";
            //ZipFile.addDirectoryToZip(uncompressedFile, compressedFile);
            ZipFileService.unzip(compressedFile, "C:\\Users\\e11001a\\Downloads\\LIBS\\ZIP_FILE\\03-Proyectos-Run\\");
        } catch (IOException ex) {
            Logger.getLogger(ZipFileService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
