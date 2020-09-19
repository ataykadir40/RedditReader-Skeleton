/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import common.FileUtility;
import entity.Board;
import entity.Image;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.BoardLogic;
import logic.ImageLogic;
import logic.LogicFactory;
import reddit.Post;
import reddit.Reddit;
import reddit.Sort;

/**
 *
 * @author abdulkadir
 * 
 * Note 1 : I have done this work myself, not with a group. 
 * Note 2 : I have admitted some bonus parts as well.
 */
@WebServlet(name = "ImageView", urlPatterns = {"/ImageView"})
public class ImageView extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>ImageViewNormal</title>");            
            out.println("</head>");
            out.println("<body>");
            
            ImageLogic logic = LogicFactory.getFor("Image");
            List<Image> lists = logic.getAll();
            
            out.println("<div align=\"center\">");
            out.println("<div align=\"center\" class=\"imageContainer\">");
            
            for(Image element: lists){
                
                String image_name= FileUtility.getFileName(element.getUrl());               
       
                out.println("<img class=\"imageThumb\" src=\"image/"+image_name+"\" width=\"300\" height=\"400\"/>");
            }
            out.println("</body>");
            out.println("</html>");
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String localPath = System.getProperty("user.home") + "/My Documents/Reddit Images/";
        
        FileUtility.createDirectory(localPath);
        
        ImageLogic iLogic = LogicFactory.getFor("Image");
                
        BoardLogic bLogic = LogicFactory.getFor("Board");
        
        for(int i=1;i<=4;i++){
            
        Board board = bLogic.getWithId(i);
        
        Consumer<Post> saveImage = (Post post) -> {
            //if post is an image and SFW
            if (post.isImage() && !post.isOver18()) {
                if(iLogic.getImagesWithUrl(post.getUrl())==null){
                     //get the path for the image which is unique
                    String path = post.getUrl();
                    //save it in img directory
                    FileUtility.downloadAndSaveFile(path, localPath);
                    String name = FileUtility.getFileName(path);
                    
                    Map<String, String[]> parameterMap = new HashMap<>();
                    parameterMap.put(ImageLogic.TITLE,new String[]{post.getTitle()});
                    parameterMap.put(ImageLogic.URL,new String[]{post.getUrl()});
                    parameterMap.put(ImageLogic.LOCAL_PATH,new String[]{localPath+name});
                    parameterMap.put(ImageLogic.DATE,new String[]{iLogic.convertDate(post.getDate())});
                    
                    Image image = iLogic.createEntity(parameterMap);
                    image.setBoard(board);
                    iLogic.add(image);
                }
            }
        };

        //create a new scraper
        Reddit scrap = new Reddit();
        //authenticate and set up a page for wallpaper subreddit with 5 posts soreted by HOT order
        scrap.authenticate().buildRedditPagesConfig(board.getName(), 5, Sort.BEST);
        //get the next page 3 times and save the images.
        scrap.requestNextPage().proccessNextPage(saveImage);
        }
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

