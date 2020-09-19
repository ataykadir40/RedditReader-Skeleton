/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author abdulkadir
 */
@WebServlet(name = "ImageDelivery", urlPatterns = {"/image/*"})
public class ImageDelivery extends HttpServlet{
         
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String imageDirectory = System.getProperty("user.home");
        
        String filename = request.getPathInfo();
        
        File file = new File(imageDirectory+"/My Documents/Reddit Images", filename) ;
        
        response.setHeader("Content-Type", getServletContext().getMimeType(filename));
        
        response.setHeader("Content-Length", String.valueOf(file.length()));
        
        response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
        
        Files.copy(file.toPath(), response.getOutputStream());
}
}


