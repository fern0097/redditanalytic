package view;

import entity.RedditAccount;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
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
import logic.LogicFactory;
import logic.RedditAccountLogic;

/**
 * @author Rodrigo Tavares
 * @version December 02, 2020
 */
@WebServlet( name = "CreateRedditAccount", urlPatterns = { "/CreateRedditAccount" })
public class CreateRedditAccount extends HttpServlet {
    
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
            /* TODO output your page here. You may use following sample code. */
            out.println( "<!DOCTYPE html>" );
            out.println( "<html>" );
            out.println( "<head>" );
            out.println( "<title>Create Reddit Account</title>" );
            out.println( "</head>" );
            out.println( "<body>" );
            out.println( "<div style=\"text-align: center;\">" );
            out.println( "<div style=\"display: inline-block; text-align: left;\">" );
            out.println( "<form action=\"CreateRedditAccount\" method=\"post\">" );
            out.println( "Name:<br>" );
            out.printf( "<input type='text' name=\"%s\" value=\"\">", RedditAccountLogic.NAME );
            out.println( "<br>" );
            out.println( "Link Points:<br>" );
            out.printf( "<input type='text' name=\"%s\" value=\"\">", RedditAccountLogic.LINK_POINTS );
            out.println( "<br>" );
            out.println( "Comment Points:<br>" );
            out.printf( "<input type='text' name=\"%s\" value=\"\"><br>", RedditAccountLogic.COMMENT_POINTS );
            out.println( "<br>" );
            out.printf( "<input type=\"date\" name=\"%s\" min=\"1900-01-01\" max=\"2099-12-31\"><br>", RedditAccountLogic.CREATED );
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


    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        log( "POST" );

        RedditAccountLogic aLogic = LogicFactory.getFor( "RedditAccount" );
        String name = request.getParameter( RedditAccountLogic.NAME );
        if( aLogic.getRedditAccountWithName( name ) == null ){
            try {                
                // need to convert the date string generated from the input
                Map<String, String[]> map = new HashMap<>(request.getParameterMap());
                SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );
                String dateStr = map.get(RedditAccountLogic.CREATED)[0];
                Date date = formatter.parse(dateStr);
                map.put(
                        RedditAccountLogic.CREATED,
                        new String[] { aLogic.convertDateToString(date)}
                        );
                
                RedditAccount account = aLogic.createEntity( map );
                aLogic.add( account );
            } catch( ParseException ex ) {
                errorMessage = ex.getMessage();
            }
        } else {
            //if duplicate print the error message
            errorMessage = "Name: \"" + name + "\" already exists";
        }
        if( request.getParameter( "add" ) != null ){
            //if add button is pressed return the same page
            processRequest( request, response );
        } else if( request.getParameter( "view" ) != null ){
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect( "RedditAccountTable" );
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create a Reddit Account Entity";
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
