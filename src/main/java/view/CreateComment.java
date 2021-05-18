/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;


import entity.Comment;
import entity.Post;
import entity.RedditAccount;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.CommentLogic;
import logic.LogicFactory;
import logic.PostLogic;
import logic.RedditAccountLogic;


/**
 *
 * @author Adriano Reckziegel
 */

@WebServlet( name = "CreateComment", urlPatterns = { "/CreateComment" })
public class CreateComment extends HttpServlet {
    
    private String errorMessage = null;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        response.setContentType( "text/html;charset=UTF-8" );
        try( PrintWriter out = response.getWriter() ) {

            out.println( "<!DOCTYPE html>" );
            out.println( "<html>" );
            out.println( "<head>" );
            out.println( "<title>Create Comment</title>" );
            out.println( "</head>" );
            out.println( "<body>" );
            out.println( "<div style=\"text-align: center;\">" );
            out.println( "<div style=\"display: inline-block; text-align: left;\">" );
            out.println( "<form action=\"CreateComment\" method=\"post\">" );
            
            out.println( "Reddit Account ID:<br>" );
            out.printf( "<input type='text' name=\"%s\" value=\"\">", CommentLogic.REDDIT_ACCOUNT_ID );
            out.println( "<br>" );
            out.println( "Post ID:<br>" );
            out.printf( "<input type='text' name=\"%s\" value=\"\">", CommentLogic.POST_ID );
            out.println( "<br>" );
            out.println( "Unique ID:<br>" );
            out.printf( "<input type='text' name=\"%s\" value=\"\">", CommentLogic.UNIQUE_ID );
            out.println( "<br>" );
            out.println( "Text:<br>" );
            out.printf( "<input type='text' name=\"%s\" value=\"\">", CommentLogic.TEXT );
            out.println( "<br>" );
            out.println( "Date:<br>" );
            out.printf( "<input type=\"datetime-local\" name=\"%s\" min=\"2017-06-01 08:30:00\" max=\"2021-06-30 16:30:00\" pattern=\"[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\" required><br>", CommentLogic.CREATED );
            out.println( "<br>" );
            out.println( "Points:<br>" );
            out.printf( "<input type='text' name=\"%s\" value=\"\"><br>", CommentLogic.POINTS );
            out.println( "<br>" );
            out.println( "Replys:<br>" );
            out.printf( "<input type='text' name=\"%s\" value=\"\"><br>", CommentLogic.REPLYS );
            out.println( "<br>" );
            out.println( "Is Reply:<br>" );
            out.printf( "<input type='text' name=\"%s\" value=\"\"><br>", CommentLogic.IS_REPLY );
            out.println( "<br>" );

            out.println( "<input type=\"submit\" name=\"view\" value=\"Add and View\">" );
            out.println( "<input type=\"submit\" name=\"add\" value=\"Add\">" );
            out.println( "</form>" );
            if( errorMessage != null && !errorMessage.isEmpty() ){
                out.println( "<p color=red>" );
                out.println( "<font color=red size=4px>" );
                out.println( errorMessage );
                out.println( "</font>" );
                out.println( "</p>" );
                errorMessage = null;
            }
            out.println( "<pre>" );
            out.println( "Submitted keys and values:" );
            out.println( toStringMap( request.getParameterMap() ) );
            out.println( "</pre>" );
            out.println( "</div>" );
            out.println( "</div>" );
            out.println( "</body>" );
            out.println( "</html>" );
        }
    }
    
    
    private String toStringMap( Map<String, String[]> values ) {
    StringBuilder builder = new StringBuilder();
    values.forEach( ( k, v ) -> builder.append( "Key=" ).append( k )
            .append( ", " )
            .append( "Value/s=" ).append( Arrays.toString( v ) )
            .append( System.lineSeparator() ) );
    return builder.toString();
    }
    
    /**
     * Handles the HTTP <code>GET</code> method.
     * 
     * get method is called first when requesting a URL. since this servlet will create a host this method simple
     * delivers the html code. creation will be done in doPost method.
     * 
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs 
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        log( "GET" );
        processRequest( request, response );
    }

    static int connectionCount = 0;
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * this method will handle the creation of entity. as it is called by user submitting data through browser.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        log( "POST" );
        log( "POST: Connection=" + connectionCount );

        CommentLogic cLogic = LogicFactory.getFor( "Comment" );
        
        RedditAccountLogic raLogic = LogicFactory.getFor( "RedditAccount" );
        PostLogic postLogic = LogicFactory.getFor( "Post" );
        
        String uniqueId = request.getParameter( CommentLogic.UNIQUE_ID );
        if( cLogic.getCommentWithUniqueId( uniqueId ) == null ){
            try {
                Comment comment = cLogic.createEntity( request.getParameterMap() );
                RedditAccount ra = raLogic.getWithId(Integer.valueOf(request.getParameter(CommentLogic.REDDIT_ACCOUNT_ID)));
                Post post = postLogic.getWithId(Integer.valueOf(request.getParameter(CommentLogic.POST_ID)));

                // need to convert the date string generated from the input - from Rodrigo Tavares
                Map<String, String[]> map = new HashMap<>(request.getParameterMap());
                SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );
                String dateStr = map.get(CommentLogic.CREATED)[0];
                Date date = formatter.parse(dateStr);
                map.put(
                        CommentLogic.CREATED,
                        new String[] { cLogic.convertDateToString(date)}
                        );
                
                //create the two logics for post and reddit account
                //get the entities from logic using getWithId
                //set the entities on your comment object before adding comment to db
                comment.setUniqueId(uniqueId);
                comment.setRedditAccountId(ra);
                comment.setPostId(post);
                
                cLogic.add( comment );
                
            } catch( Exception ex ) {
                log("", ex);
                errorMessage = ex.getMessage();
            }
        } else {
            //if duplicate print the error message
            errorMessage = "UniqueID: \"" + uniqueId + "\" already exists";
        }
        if( request.getParameter( "add" ) != null ){
            //if add button is pressed return the same page
            processRequest( request, response );
        } else if( request.getParameter( "view" ) != null ){
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect( "CommentTable" );
        }
    }
    
    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */    
   @Override
    public String getServletInfo() {
        return "Create a Comment Entity";
    }

    private static final boolean DEBUG = true;

    @Override
    public void log( String msg ) {
        if( DEBUG ){
            String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
            getServletContext().log( message );
        }
    }

    @Override
    public void log( String msg, Throwable t ) {
        String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
        getServletContext().log( message, t );
    }
    
}
