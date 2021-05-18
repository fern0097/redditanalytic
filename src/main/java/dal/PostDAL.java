/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import entity.Post;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PostDAL
 *
 * @author Ariane Nogueira
 * @version December 13, 2020
 */
public class PostDAL extends GenericDAL<Post> {

    /**
     * This method calls the Post class which contains all the tables, columns
     * and parameters needed.
     */
    public PostDAL() {
        super(Post.class);
    }

    /**
     * @return a String name from NamedQuery at Post class and the map from the
     * parameter searched. In the case of findAll(), map applies to all results
     * founded.
     */
    @Override
    public List<Post> findAll() {
        return findResults("Post.findAll", null);
    }

    /**
     * @param id is defined at Post class inside the NamedQuery and starts with
     * :[@param id]
     * @return a String name from NamedQuery at Post class and the map from the
     * parameter searched.
     */
    @Override
    public Post findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return findResult("Post.findById", map);
    }

    /**
     * @param uniqueId is defined at Post class inside the NamedQuery and starts
     * with :[@param uniqueId]
     * @return a String name from NamedQuery at Post class and the map from the
     * parameter searched.
     */
    public Post findByUniqueId(String uniqueId) {
        Map<String, Object> map = new HashMap<>();
        map.put("uniqueId", uniqueId);
        return findResult("Post.findByUniqueId", map);
    }

    /**
     * @param points is defined at Post class inside the NamedQuery and starts
     * with :[@param points]
     * @return a String name from NamedQuery at Post class and the map from the
     * parameter searched.
     */
    public List<Post> findByPoints(int points) {
        Map<String, Object> map = new HashMap<>();
        map.put("points", points);
        return  findResults("Post.findByPoints", map);
    }

    /**
     * @param commentCount is defined at Post class inside the NamedQuery and
     * starts with :[@param commentCount]
     * @return a String name from NamedQuery at Post class and the map from the
     * parameter searched.
     */
    public List<Post> findByCommentCount(int commentCount) {
        Map<String, Object> map = new HashMap<>();
        map.put("commentCount", commentCount);
        return findResults("Post.findByCommentCount", map);
    }

    /**
     * @param title is defined at Post class inside the NamedQuery and starts
     * with :[@param title]
     * @return a String name from NamedQuery at Post class and the map from the
     * parameter searched.
     */
    public List<Post> findByTitle(String title) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        return findResults("Post.findByTitle", map);
    }

    /**
     * @param created is defined at Post class inside the NamedQuery and starts
     * with :[@param created].
     * @return a String name from NamedQuery at Post class and the map from the
     * parameter searched.
     */
    public List<Post> findByCreated(Date created) {
        Map<String, Object> map = new HashMap<>();
        map.put("created", created);
        return findResults("Post.findByCreated", map);
    }

    /**
     * @param id is defined at Post class for Reddit Account author inside the NamedQuery and starts
     * with :[@param id].
     * @return a String name from NamedQuery at Post class and the map from the
     * parameter searched.
     */
    public List<Post> findByAuthor(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("reddit_account_id", id);
        return findResults("Post.findByAuthor", map);
    }
}
