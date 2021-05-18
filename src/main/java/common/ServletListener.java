package common;

import dal.EMFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * do not modify this class
 *
 * lazy initialize EntityManagerFactory when the first servlet is created. do not create again.
 *
 * @see https://www.deadcoderising.com/execute-code-on-webapp-startup-and-shutdown-using-servletcontextlistener/
 * @see https://javaee.github.io/javaee-spec/javadocs/javax/servlet/ServletContextListener.html
 *
 * @author Shariar (Shawn) Emami
 */
@WebListener
public class ServletListener implements ServletContextListener {

    /**
     * this method is triggered when the web application is starting the initialization. This will be invoked before any
     * of the filters and servlets are initialized.
     *
     * @param sce
     */
    @Override
    public void contextInitialized( ServletContextEvent sce ) {
        Logger.getLogger( getClass().getName() ).log( Level.INFO, "Initializing EMF" );
        EMFactory.initializeEMF();
        Logger.getLogger( getClass().getName() ).log( Level.INFO, "EMF initialized" );
    }

    /**
     * this method is triggered when the ServletContext is about to be destroyed. This will be invoked after all the
     * servlets and filters have been destroyed.
     *
     * @param sce
     */
    @Override
    public void contextDestroyed( ServletContextEvent sce ) {
        Logger.getLogger( getClass().getName() ).log( Level.INFO, "Destroying EMF" );
        EMFactory.closeEMF();
        Logger.getLogger( getClass().getName() ).log( Level.INFO, "EMF Destroyed" );
    }
}
