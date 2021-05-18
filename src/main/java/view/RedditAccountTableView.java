package view;

import entity.RedditAccount;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.LogicFactory;
import logic.RedditAccountLogic;

/**
 * RedditAccountTableView
 *
 * @author Rodrigo Tavares
 * @version November 23, 2020
 */
@WebServlet( name = "RedditAccountTable", urlPatterns = { "/RedditAccountTable" } )
public class RedditAccountTableView extends HttpServlet {
    
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
            out.println( "<title>RedditAccountViewNormal</title>" );
            out.println( "</head>" );
            
            out.println( "<body>" );
            out.println( "<table style=\"margin-left: auto; margin-right: auto;\" border=\"1\">" );
            out.println( "<caption>RedditAccount</caption>" );
            
            RedditAccountLogic logic = LogicFactory.getFor( "RedditAccount" );
            List<String> columns = logic.getColumnNames();

            // Table Header
            out.println( "<tr>" );
            for (String c: columns) {
                out.println("<th>" + c + "</th>");
            }
            out.println( "</tr>" );
            
            List<RedditAccount> entities = logic.getAll();
            for( RedditAccount e: entities ) {
                List<?> list = logic.extractDataAsList(e);
                out.println( "<tr>" );
                for (Object data: list) {
                    out.printf("<td>%s</td>", data.toString());
                }
                out.println( "</tr>" );
            }
            
            // Table Footer
            out.println( "<tr>" );
            for (String c: columns) {
                out.println("<th>" + c + "</th>");
            }
            out.println( "</tr>" ); 
            
            out.println( "</table>" );
            
            out.printf( "<div style=\"text-align: center;\"><pre>%s</pre></div>", toStringMap( request.getParameterMap() ) );
            out.println( "</body>" );
            out.println( "</html>" );
        }
    }

    private String toStringMap( Map<String, String[]> m ) {
        StringBuilder builder = new StringBuilder();
        for( String k: m.keySet() ) {
            builder.append( "Key=" ).append( k )
                    .append( ", " )
                    .append( "Value/s=" ).append( Arrays.toString( m.get( k ) ) )
                    .append( System.lineSeparator() );
        }
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
        RedditAccountLogic logic = LogicFactory.getFor( "RedditAccount" );
        RedditAccount account = logic.updateEntity( request.getParameterMap() );
        logic.update( account );
        processRequest( request, response );
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Sample of RedditAccount View Normal";
    }

    private static final boolean DEBUG = true;

    public void log( String msg ) {
        if( DEBUG ){
            String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
            getServletContext().log( message );
        }
    }

    public void log( String msg, Throwable t ) {
        String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
        getServletContext().log( message, t );
    }    
    
}