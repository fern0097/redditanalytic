/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.CommentDAL;
import entity.Comment;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static logic.RedditAccountLogic.CREATED;
import static logic.RedditAccountLogic.ID;

/**
 *
 * @author Adriano Reckziegel
 */
public class CommentLogic extends GenericLogic<Comment, CommentDAL> {

    /**
     * create static final variables with proper name of each column. this way
     * you will never manually type it again, instead always refer to these
     * variables. */
    
    public static final String REPLYS = "replys";
    public static final String IS_REPLY = "is_reply";
    public static final String POINTS = "points";
    public static final String CREATED = "created";
    public static final String TEXT = "text";
    public static final String ID = "id";
    public static final String UNIQUE_ID = "unique_id";
    public static final String REDDIT_ACCOUNT_ID = "reddit_account_id";
    public static final String POST_ID = "post_id";

    CommentLogic() {
        super(new CommentDAL());
    }

    @Override
    public List<Comment> getAll() {
        return get(() -> dal().findAll());
    }

    @Override
    public Comment getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    public Comment getCommentWithUniqueId(String uniqueId) {
        return get(() -> dal().findByUniqueId(uniqueId));
    }

    public List<Comment> getCommentsWithText(String text) {
        return get(() -> dal().findByText(text));
    }

    public List<Comment> getCommentsWithCreated(Date created) {
        return get(() -> dal().findByCreated(created));
    }

    public List<Comment> getCommentsWithPoints(int points) {
        return get(() -> dal().findByPoints(points));
    }

    public List<Comment> getCommentsWithReplys(int replys) {
        return get(() -> dal().findByReplys(replys));
    }

    public List<Comment> getCommentsWithIsReply(boolean isReply) {
        return get(() -> dal().findByIsReply(isReply));
    }

    /**
     * Creation fo the Comment entity
     * @param parameterMap
     * @return entity
     */
    
    @Override
    public Comment createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");

        Comment entity = new Comment();
        if (parameterMap.containsKey(ID)) {
            try {
                entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }
        ObjIntConsumer<String> validator = (value, length) -> {
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
        // changed to test
//        int reddit_account_id = Integer.parseInt(parameterMap.get(REDDIT_ACCOUNT_ID)[0]);
//        int post_id = Integer.parseInt(parameterMap.get(POST_ID)[0]);
        
        
        String unique_id = parameterMap.get(UNIQUE_ID)[0];
        String text = parameterMap.get(TEXT)[0];
        String created = parameterMap.get(CREATED)[0];

        //trim text over 1000 characters
        if (text.length() > 1000) {
            text = text.substring(0, 1000);
        }

        try {
            Date date = convertStringToDate(created);
            entity.setCreated(date);
        } catch (ValidationException ex) {
            try {
                created = created.replace("T", " ");
                SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                Date date = FORMATTER.parse(created);
                entity.setCreated(date);
            } catch (ParseException ex1) {
                entity.setCreated(Date.from(Instant.now(Clock.systemDefaultZone())));
            }
        }

        
        //Date created = Date.from(Instant.parse(parameterMap.get(CREATED)[0]));
        
        //changed to test
        //int points = Integer.parseInt(parameterMap.get(POINTS)[0]);
        //int replys = Integer.parseInt(parameterMap.get(REPLYS)[0]);
        int points, replys;//, reddit_account_id, post_id;    
       
        
        try {
            points = Integer.parseInt(parameterMap.get(POINTS)[0]);
            replys = Integer.parseInt(parameterMap.get(REPLYS)[0]);
//            reddit_account_id = Integer.parseInt(parameterMap.get(REDDIT_ACCOUNT_ID)[0]);
//            post_id = Integer.parseInt(parameterMap.get(POST_ID)[0]);
        } catch( java.lang.NumberFormatException ex ) {
            throw new ValidationException( ex );
        }
       
        Boolean is_reply = Boolean.parseBoolean(parameterMap.get(IS_REPLY)[0]);

        
        
        
        //validate the data
        validator.accept(unique_id, 10);
        validator.accept(text, 1000);

        //set values on entity
//        entity.setRedditAccountId(reddit_account_id);
//        entity.setPostId( post_id );
        entity.setUniqueId(unique_id);
        entity.setText(text);
        entity.setPoints(points);
        entity.setReplys(replys);
        entity.setIsReply(is_reply);

        return entity;
    }

    /**
     * this method is used to send a list of all names to be used form table
     * column headers. by having all names in one location there is less chance
     * of mistakes.
     *
     * this list must be in the same order as getColumnCodes and
     * extractDataAsList
     *
     * @return list of all column names to be displayed.
     */
    
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "Reddit Account ID", "Post ID", "Unique ID", "Text", "Created", "Points", "Replys", "Is Reply");
    }

    /**
     * this method is used to send a list of all names to be used form table
     * column headers. by having all names in one location there is less chance
     * of mistakes.
     *
     * this list must be in the same order as getColumnCodes and
     * extractDataAsList
     *
     * @return list of all column names to be displayed.
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, REDDIT_ACCOUNT_ID, POST_ID, UNIQUE_ID, TEXT, CREATED, POINTS, REPLYS, IS_REPLY);
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
    public List<?> extractDataAsList(Comment e) {
        return Arrays.asList(e.getId(), e.getRedditAccountId(), e.getPostId(), e.getUniqueId(), e.getText(),
                e.getCreated(), e.getPoints(), e.getReplys(), e.getIsReply());
    }

}
