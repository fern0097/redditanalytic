/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.PostDAL;
import entity.Post;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 * PostLogic
 *
 * @author Ariane Nogueira
 * @version December 13, 2020
 */
public class PostLogic extends GenericLogic<Post, PostDAL> {

     /**
     * create static final variables with proper name of each column of the Post Table.
     */
    public static final String CREATED = "created";
    public static final String TITLE = "title";
    public static final String COMMENT_COUNT = "comment_count";
    public static final String POINTS = "points";
    public static final String ID = "id";
    public static final String UNIQUE_ID = "unique_id";
    public static final String REDDIT_ACCOUNT_ID = "reddit_account_id";
    public static final String SUBREDDIT_ID = "subreddit_id";

    /**
    * No-args constructor
    * */
    PostLogic() {
        super(new PostDAL());
    }

    /**
    * Overrides the getAll method from super class.
    * */
    @Override
    public List<Post> getAll() {
        return get(() -> dal().findAll());
    }

    /**
    * Overrides the getWithId method from super class and takes an integer @param id.
    * */
    @Override
    public Post getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    /**
    * Overrides the getPostWithUniqueId method from super class and takes an String @param uniqueId
    * */
    public Post getPostWithUniqueId(String uniqueId) {
        return get(() -> dal().findByUniqueId(uniqueId));
    }

    /**
    * Overrides the getPostWithPoints method from super class and takes an integer @param points
    * */
    public List<Post> getPostWithPoints(int points) {
        return get(() -> dal().findByPoints(points));
    }

    /**
    * Overrides the getPostsWithCommentCount method from super class and takes an integer @param commentCount
    * */
    public List<Post> getPostsWithCommentCount(int commentCount) {
        return get(() -> dal().findByCommentCount(commentCount));
    }

    /**
    * Overrides the getPostsWithAuthorID method from super class and takes an integer @param id
    * */
    public List<Post> getPostsWithAuthorID(int id) {
        return get(() -> dal().findByAuthor(id));
    }

    /**
    * Overrides the getPostsWithTitle method from super class and takes an String @param title
    * */    
    public List<Post> getPostsWithTitle(String title) {
        return get(() -> dal().findByTitle(title));
    }

    /**
    * Overrides the getPostsWithCreated method from super class and takes an Date @param created
    * */   
    public List<Post> getPostsWithCreated(Date created) {
        return get(() -> dal().findByCreated(created));
    }

    /**
    * Overrides the createEntity method from super class and takes an Map<String, String[]> @param parameterMap.
    * Method constructed over example provided by Shawn Emami, 2020.
    * */ 
    @Override
    public Post createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");

         /**
         * creates a new Entity object.
         **/
        Post entity = new Post();

         /**
         * ID is generated, so if it exists add it to the entity object otherwise it does not matter as MySQL will create an if for it.
         * The only time that we will have id is for update behavior.
         **/
        if (parameterMap.containsKey(ID)) {
            try {
                entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }

        ObjIntConsumer< String> validator = (value, length) -> {
            if (value == null || value.trim().isEmpty() || value.length() > length) {
                String error = "";
                if (value == null || value.trim().isEmpty()) {
                    error = "value cannot be null or empty: " + value;
                }
                if (value.length() > length) {
                    error = "string length is " + value.length() + " > " + length;
                }
                throw new ValidationException(error);
            }
        };

        
       /**
        * everything in the parameterMap is String type and values are
        * stored in an Array of Strings, the value is at index zero. 
        * validates integers for points and comment count.
        **/
        String uniqueid = parameterMap.get(UNIQUE_ID)[0];
        String title = parameterMap.get(TITLE)[0];
        String created = parameterMap.get(CREATED)[0];
        
        int commentcount; 
        int points;
        
       /**
        * everything in the parameterMap is string so it must first be converted to appropriate type and values are
        * stored in an Array of integers, the value is at index zero. 
        * validates integers for points and comment count.
        **/
        try {
            points = Integer.parseInt(parameterMap.get(POINTS)[0]);
            commentcount = Integer.parseInt(parameterMap.get(COMMENT_COUNT)[0]);
        } catch( java.lang.NumberFormatException ex ) {
            throw new ValidationException( ex );
        }

        validator.accept(uniqueid, 10);
        validator.accept(title, 255);

        entity.setUniqueId(uniqueid);
        entity.setTitle(title);
        
         /**
        * extracts the Date from the map and set the Date converted.
        * Validation Exception catch and set the date created from the static method of Clock class, 
        * then, it returns a clock that returns the current instant of the clock.
        **/
        try{
            Date d = convertStringToDate(created);
            entity.setCreated(d);
        }catch(ValidationException ex){
            entity.setCreated(Date.from( Instant.now( Clock.systemDefaultZone() ) ));
        }
        
        
        entity.setCommentCount(commentcount);
        entity.setPoints(points);

        return entity;
    }

     /**
     * this method is used to send a list of all names to be used form table column headers. by having all names in one
     * location there is less chance of mistakes.
     *
     * this list must be in the same order as getColumnCodes and extractDataAsList
     *
     * @return list of all column names to be displayed.
     */
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "Reddit Account ID", "SubReddit ID", "Unique ID", "Points", "Comment Count", "Title", "Created ");
    }

     /**
     * this method returns a list of column names that match the official column names in the db. by having all names in
     * one location there is less chance of mistakes.
     *
     * this list must be in the same order as getColumnNames and extractDataAsList
     *
     * @return list of all column names in DB.
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, REDDIT_ACCOUNT_ID, SUBREDDIT_ID, UNIQUE_ID, POINTS, COMMENT_COUNT, TITLE, CREATED);
    }

    /**
     * return the list of values of all columns (variables) in given entity.
     *
     * this list must be in the same order as getColumnNames and getColumnCodes
     *
     * @param e - given Entity to extract data from.
     *
     * @return list of extracted values
     */ 
    @Override
    public List<?> extractDataAsList(Post e) {
        return Arrays.asList(e.getId(), e.getRedditAccountId(), e.getSubredditId(), e.getUniqueID(), e.getPoints(), e.getCommentCount(), e.getTitle(), e.getCreated());
    }
} 