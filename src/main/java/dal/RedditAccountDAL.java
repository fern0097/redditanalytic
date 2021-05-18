package dal;

import entity.RedditAccount;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RedditAccountDAL
 *
 * @author Rodrigo Tavares
 * @version December 02, 2020
 */
public class RedditAccountDAL extends GenericDAL<RedditAccount>{

    public RedditAccountDAL() {
        super(RedditAccount.class);
    }

    @Override
    public List<RedditAccount> findAll() {
         return findResults( "RedditAccount.findAll", null );
   }

    @Override
    public RedditAccount findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put( "id", id );
        return findResult( "RedditAccount.findById", map );
    }
    
    public RedditAccount findByName(String name) {
        Map<String, Object> map = new HashMap<>();
        map.put( "name", name );
        return findResult( "RedditAccount.findByName", map );
   }
    
    public List<RedditAccount> findByLinkPoints(int linkPoints) {
        Map<String, Object> map = new HashMap<>();
        map.put( "link_points", linkPoints );
        return findResults( "RedditAccount.findByLinkPoints", map );
    }
    
    public List<RedditAccount> findByCommentPoints(int commentPoints) {
        Map<String, Object> map = new HashMap<>();
        map.put( "comment_points", commentPoints );
        return findResults( "RedditAccount.findByCommentPoints", map );
    }
    
    public List<RedditAccount> findByCreated(Date created) {
        Map<String, Object> map = new HashMap<>();
        map.put( "created", created );
        return findResults( "RedditAccount.findByCreated", map );
    }
}
