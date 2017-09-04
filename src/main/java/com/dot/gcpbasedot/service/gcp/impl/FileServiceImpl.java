/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.service.gcp.impl;

import com.dot.gcpbasedot.service.gcp.FileService;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.OutputSettings;
import com.google.appengine.api.images.Transform;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

/**
 *
 * @author desarrollador
 */
@Service
public class FileServiceImpl implements FileService {

    /**
     *
     * @param imageBytes
     * @param maxWidth
     * @param maxHeigth
     * @return
     * @throws IOException
     */
    @Override
    public Image resizeImage(byte[] imageBytes, int maxWidth, int maxHeigth) throws IOException {
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        OutputSettings settings = new OutputSettings(ImagesService.OutputEncoding.JPEG);
        settings.setQuality(80);
        
        Image image = ImagesServiceFactory.makeImage(imageBytes);
        Transform resize = ImagesServiceFactory.makeResize(maxWidth, maxHeigth);
        Image resizedImage = imagesService.applyTransform(resize, image, settings);

        return resizedImage;
    }

    @Override
    public byte[] getByteArrayFromURL(URL url) throws IOException {
        try (InputStream is = url.openStream()) {
            byte[] imageBytes = IOUtils.toByteArray(is);

            return imageBytes;
        } catch (IOException e) {
            System.err.printf("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
        }
        return null;
    }

}
