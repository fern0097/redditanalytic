package dal;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * do not modify this class
 *
 * lazy initialize EntityManagerFactory.
 *
 * @author Shariar (Shawn) Emami
 */
public final class EMFactory {

    private static EntityManagerFactory emFactory;

    private EMFactory() {
    }

    public static void initializeEMF() {
        if( emFactory == null ){
            emFactory = Persistence.createEntityManagerFactory( "JPA-Tomcat-RedditAnalytic" );
        }
    }

    public static void closeEMF() {
        if( emFactory != null ){
            emFactory.close();
        }
    }

    public static EntityManagerFactory getEMF() {
        initializeEMF();
        return emFactory;
    }
}
