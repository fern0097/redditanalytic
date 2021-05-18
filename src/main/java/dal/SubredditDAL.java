package dal;

import entity.Subreddit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Wilker Fernandes de Sousa
 * @version 1.0
 * 
 */
public class SubredditDAL extends GenericDAL<Subreddit> {

    public SubredditDAL() {
        super( Subreddit.class );
    }

    @Override
    public List<Subreddit> findAll() {
        //first argument is a name given to a named query defined in appropriate entity
        //second argument is map used for parameter substitution.
        //parameters are names starting with : in named queries, :[name]
        return findResults( "Subreddit.findAll", null );
    }

    @Override
    public Subreddit findById( int id ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "id", id );
        //first argument is a name given to a named query defined in appropriate entity
        //second argument is map used for parameter substitution.
        //parameters are names starting with : in named queries, :[name]
        //in this case the parameter is named "id" and value for it is put in map
        return findResult( "Subreddit.findById", map );
    }

    public Subreddit findByName( String displayname ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "name", displayname );
        return findResult( "Subreddit.findByName", map );
    }

    public Subreddit findByUrl( String url ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "url", url );
        return findResult( "Subreddit.findByUrl", map );
    }

    public List<Subreddit> findBySubscribers( int subscribers ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "subscribers", subscribers  );
        return findResults( "Subreddit.findBySubscribers", map );
    }

}