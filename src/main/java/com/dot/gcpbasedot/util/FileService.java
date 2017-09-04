/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.util;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 *
 * @author lacastrillov
 */
public class FileService {

    public static Map<String, Object> saveFile(String name, String contentType, InputStream inputStream, String location) {
        try {
            File targetFile = new File(location + name);
            FileUtils.copyInputStreamToFile(inputStream, targetFile);

            Map<String, Object> result = new HashMap<>();
            result.put("name", name);
            result.put("location", location);
            result.put("contentType", contentType);
            result.put("size", (int) targetFile.length());

            return result;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean saveFile(CommonsMultipartFile fichero, String nombreArchivo, String pathLocal) {
        FileOutputStream os;

        try {
            os = new FileOutputStream(new File(pathLocal + nombreArchivo));
            os.write(fichero.getBytes());
            os.close();

            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    
    public static void addPartToFile(String fileName, String location, int fileSize, InputStream inputStream) throws FileNotFoundException, IOException {
        try(OutputStream out = new FileOutputStream(new File(location + fileName), true)) {
            int read;
            byte[] bytes = new byte[fileSize];
            while ((read = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.close();
            inputStream.close();
        }
    }
    
    public static boolean createFile(String pathFile) {
        File targetFile = new File(pathFile);
        FileOutputStream os;

        try {
            os = new FileOutputStream(targetFile);
            os.write("".getBytes());
            os.close();

            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static boolean createFolder(String pathFile) {
        File targetFile = new File(pathFile);
        return targetFile.mkdirs();
    }
    
    public static boolean renameFile(String pathFile, String pathRenamedFile) {
        File targetFile = new File(pathFile);
        File renamedFile = new File(pathRenamedFile);

        // Rename file (or directory)
        return targetFile.renameTo(renamedFile);
    }

    public static boolean deleteFile(String pathFile) {
        try {
            File targetFile = new File(pathFile);
            if(targetFile.isDirectory()){
                FileUtils.deleteDirectory(targetFile);
            }else{
                targetFile.delete();
            }
            return true;
        } catch (IOException ex) {
            Logger.getLogger(FileService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static boolean move(File sourceFile, File destFile) throws IOException{
        if (sourceFile.isDirectory()){
            FileUtils.moveDirectoryToDirectory(sourceFile, destFile, true);
        }else{
            FileUtils.moveFileToDirectory(sourceFile, destFile, true);
        }
        return true;
    }

    public static BufferedReader readFile(String fileName) throws FileNotFoundException {
        // FileReader reads text files in the default encoding.
        FileReader fileReader = new FileReader(fileName);

        // Always wrap FileReader in BufferedReader.
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        return bufferedReader;
    }
    
    public static boolean existsFile(String fileName) {
        File f = new File(fileName);
        return f.exists();
    }

    public static BufferedWriter writeFile(String fileName) throws IOException {
        // Assume default encoding.
        FileWriter fileWriter = new FileWriter(fileName);

        // Always wrap FileWriter in BufferedWriter.
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        return bufferedWriter;
    }

    public static String getTextFile(String pathFile) throws IOException {
        String text="";
        try (FileInputStream inputStream = new FileInputStream(pathFile)) {
            text = IOUtils.toString(inputStream);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return text;
    }
    
    public static void setTextFile(String text, String pathFile) throws IOException{
        File file = new File(pathFile);
        FileUtils.writeStringToFile(file, text);
    }
    
    public static BufferedImage resizeImage(BufferedImage resizeMe, int maxWidth, int maxHeight) throws IOException{
        Dimension newMaxSize = new Dimension(maxWidth, maxHeight);
        BufferedImage resizedImg = Scalr.resize(resizeMe, Method.QUALITY, newMaxSize.width, newMaxSize.height);
        
        return resizedImg;
    }
    
    public static InputStream bufferedImageToInputStream(BufferedImage image, String contentType) throws IOException{
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, contentType.split("/")[1], os);
        
        return new ByteArrayInputStream(os.toByteArray());
    }
    
    
}
