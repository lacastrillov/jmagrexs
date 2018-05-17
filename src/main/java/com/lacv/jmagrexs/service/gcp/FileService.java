/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.service.gcp;

import com.google.appengine.api.images.Image;
import java.io.IOException;
import java.net.URL;

/**
 *
 * @author desarrollador
 */
public interface FileService {
    
    Image resizeImage(byte[] imageBytes, int maxWidth, int maxHeigth) throws IOException;
    
    byte[] getByteArrayFromURL(URL url) throws IOException;
    
}
