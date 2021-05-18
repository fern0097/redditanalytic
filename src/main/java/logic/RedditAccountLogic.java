package logic;

import common.ValidationException;
import dal.RedditAccountDAL;
import entity.RedditAccount;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * RedditAccountLogic
 *
 * @author Rodrigo Tavares
 * @version December 02, 2020
 */
public class RedditAccountLogic extends GenericLogic<RedditAccount, RedditAccountDAL> {
    
    public static final String COMMENT_POINTS   = "comment_points";
    public static final String LINK_POINTS      = "link_points";
    public static final String CREATED          = "created";
    public static final String NAME             = "name";
    public static final String ID               = "id";
    
    RedditAccountLogic() {
        super( new RedditAccountDAL() );
    }

    @Override
    public List<RedditAccount> getAll() {
        return get( () -> dal().findAll() );
     }

    @Override
    public RedditAccount getWithId(int id) {
        return get( () -> dal().findById( id ) );
    }
    
    public RedditAccount getRedditAccountWithName(String name) {
        return get( () -> dal().findByName( name ) );
   }
    
    public List<RedditAccount> getRedditAccountsWithLinkPoints(int link_points) {
        return get( () -> dal().findByLinkPoints(link_points));
     }
    
    public List<RedditAccount> getRedditAccountsWithCommentPoints(int comment_points) {
        return get( () -> dal().findByCommentPoints(comment_points));
    }
    
    public List<RedditAccount> getRedditAccountsWithCreated(Date created) {
        return get( () -> dal().findByCreated(created));
     }
    
    @Override
    public RedditAccount createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        
        RedditAccount entity = new RedditAccount();
        
        //ID is generated, so if it exists add it to the entity object
        //otherwise it does not matter as mysql will create an if for it.
        //the only time that we will have id is for update behaviour.
        if( parameterMap.containsKey( ID ) ){
            try {
                entity.setId( Integer.parseInt( parameterMap.get( ID )[ 0 ] ) );
            } catch( java.lang.NumberFormatException ex ) {
                throw new ValidationException( ex );
            }
        }
        
        // extracts (and validates) the date using parent class method
        Date created = convertStringToDate(parameterMap.get(CREATED)[0]);
        
        String name = parameterMap.get(NAME)[0];
        
        // validates the string using parent class method
        validateString(parameterMap, NAME, 100);

        int link_points, comment_points;
        
        // validates the integers
        try {
            link_points = Integer.parseInt(parameterMap.get(LINK_POINTS)[0]);
            comment_points = Integer.parseInt(parameterMap.get(COMMENT_POINTS)[0]);
        } catch( java.lang.NumberFormatException ex ) {
            throw new ValidationException( ex );
        }
        
        entity.setName(name);
        entity.setLinkPoints(link_points);
        entity.setCommentPoints(comment_points);
        entity.setCreated(created);
        
        return entity;
    }

    @Override
    public List<String> getColumnNames() {
        return Arrays.asList( "ID", "Name", "Link Points", "Comment Points", "Created");
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( ID, NAME, LINK_POINTS, COMMENT_POINTS, CREATED );
    }

    @Override
    public List<?> extractDataAsList(RedditAccount e) {
        return Arrays.asList (e.getId(), e.getName(), e.getLinkPoints(), e.getCommentPoints(), e.getCreated());
    }    
}