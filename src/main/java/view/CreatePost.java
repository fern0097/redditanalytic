/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import entity.Account;
import entity.Post;
import entity.RedditAccount;
import entity.Subreddit;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.LogicFactory;
import logic.PostLogic;
import logic.RedditAccountLogic;
import logic.SubredditLogic;

/**
 * CreatePost
 * 
 * @author Shariar (Shawn) Emami | modified by Ariane Nogueira
 * @version December 13, 2020
 **/
@WebServlet(name = "CreatePost", urlPatterns = {"/CreatePost"})
public class CreatePost extends HttpServlet {
    
    private String errorMessage = null;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Create Post</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: left;\">");
            out.println("<form method=\"post\">");
            out.println("Title:<br>");
            //instead of typing the name of column manualy use the static vraiable in logic
            //use the same name as column id of the table. will use this name to get date
            //from parameter map.
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", PostLogic.TITLE);
            out.println("<br>");
            out.println("Author ID:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", PostLogic.REDDIT_ACCOUNT_ID);
            out.println("<br>");
            out.println("Subreddit ID:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", PostLogic.SUBREDDIT_ID);
            out.println("<br>");
            out.println("Unique ID:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", PostLogic.UNIQUE_ID);
            out.println("<br>");
            out.println("Comment Count:<br>");
            out.printf("<input type='text' name=\"%s\" value=\"\"><br>", PostLogic.COMMENT_COUNT);
            out.println("<br>");
            out.println("Points:<br>");
            out.printf("<input type='text' name=\"%s\" value=\"\"><br>", PostLogic.POINTS);
            out.println("<br>");
            out.println("Date:<br>");
            out.printf("<input type=\"date\" name=\"%s\" min=\"1900-01-01\" max=\"2099-12-30\"><br><br>", PostLogic.CREATED);
            out.println("<br>");
            out.println("<input type=\"submit\" name=\"view\" value=\"Add and View\">");
            out.println("<input type=\"submit\" name=\"add\" value=\"Add\">");
            out.println("</form>");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                out.println("<p color=red>");
                out.println("<font color=red size=4px>");
                out.println(errorMessage);
                out.println("</font>");
                out.println("</p>");
            }
            out.println("<pre>");
            out.println("Submitted keys and values:");
            out.println(toStringMap(request.getParameterMap()));
            out.println("</pre>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    private String toStringMap(Map<String, String[]> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach((k, v) -> builder.append("Key=").append(k)
                .append(", ")
                .append("Value/s=").append(Arrays.toString(v))
                .append(System.lineSeparator()));
        return builder.toString();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * get method is called first when requesting a URL. since this servlet will
     * create a host this method simple delivers the html code. creation will be
     * done in doPost method.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("GET");
        processRequest(request, response);
    }
    
    static int connectionCount = 0;

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * this method will handle the creation of entity. as it is called by user
     * submitting data through browser.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");
        log("POST: Connection=" + connectionCount);
        
        PostLogic pLogic = LogicFactory.getFor("Post");
        RedditAccountLogic raLogic = LogicFactory.getFor("RedditAccount");
        SubredditLogic subLogic = LogicFactory.getFor("Subreddit");
        
        String uniqueID = request.getParameter(PostLogic.UNIQUE_ID);
        if (pLogic.getPostWithUniqueId(uniqueID) == null) {
            try {
                Post post = pLogic.createEntity(request.getParameterMap());
                RedditAccount ra = raLogic.getWithId(Integer.valueOf(request.getParameter(PostLogic.REDDIT_ACCOUNT_ID)));
                Subreddit sub = subLogic.getWithId(Integer.valueOf(request.getParameter(PostLogic.SUBREDDIT_ID)));
                
                post.setRedditAccountId(ra);
                post.setSubredditId(sub);
                
                pLogic.add(post);
                
            } catch (Exception ex) {
               log("",ex);
                errorMessage = ex.getMessage();
            }
        } else {
            //if duplicate print the error message
            errorMessage = "UniqueID: \"" + uniqueID + "\" already exists";
        }
        if (request.getParameter("add") != null) {
            //if add button is pressed return the same page
            processRequest(request, response);
        } else if (request.getParameter("view") != null) {
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect("PostTable");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create a Post Entity";
    }
    
    private static final boolean DEBUG = true;
    
    public void log(String msg) {
        if (DEBUG) {
            String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log(message);
        }
    }
    
    public void log(String msg, Throwable t) {
        String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log(message, t);
    }
}
