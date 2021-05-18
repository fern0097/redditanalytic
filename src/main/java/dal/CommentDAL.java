/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import entity.Comment;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Adriano Reckziegel
 * Stud. Numb: 040977738
 * 
 */
public class CommentDAL extends GenericDAL<Comment>{
    
        public CommentDAL() {
        super( Comment.class );
    }

    /**
     * first argument is a name given to a named query defined in appropriate entity
     * second argument is map used for parameter substitution.
     * @return findResults
     */    
        
    @Override
    public List<Comment> findAll() {
        return findResults ("Comment.findAll", null);
    }
    
    /**
     * first argument is a name given to a named query defined in appropriate entity
     * second argument is map used for parameter substitution.
     * parameters are names starting with : in named queries, :[name]
     * in this case the parameter is named "id" and value for it is put in map
     * 
     * @param id
     * @return findResult
     */

    @Override
    public Comment findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put( "id", id );
        return findResult ( "Comment.findById" , map);
    }
    
    public Comment findByUniqueId(String uniqueid) {
        Map<String, Object> map = new HashMap<>();
        map.put ("uniqueId", uniqueid);
        return findResult ("Comment.findByUniqueId", map); 
    }
    
    public List<Comment> findByText(String text){
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
        return findResults ("Comment.findByText", map);
    }
    
    public List<Comment> findByCreated (Date created) {
        Map<String, Object> map = new HashMap<>();
        map.put ("created", created);
        return findResults ("Comment.findByCreated", map);
    }
    
    public List<Comment> findByPoints (int points) {
        Map<String, Object> map = new HashMap<>();
        return findResults ("Comment.findByPoints", map);
    }
    
    public List<Comment> findByReplys (int replys) {
        Map<String, Object> map = new HashMap<>();
        return findResults ("Comment.findByReplys", map);
    }
    
    public List<Comment> findByIsReply (Boolean isReply) {
        Map<String, Object> map = new HashMap<>();
        return findResults ("Comment.findByIsReply", map);
    }   
}
