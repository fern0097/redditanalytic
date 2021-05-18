/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Post;
import entity.RedditAccount;
import entity.Subreddit;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * PostLogicTest
 * 
 * @author Shariar (Shawn) Emami | modified by Ariane Nogueira
 * @version December 13, 2020
 **/
public class PostLogicTest {

    private PostLogic logic;
    private RedditAccountLogic rlogic;
    private SubredditLogic slogic;
    private Post expectedEntity;

    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat("/RedditAnalytic", "common.ServletListener");
    }

    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }

    @BeforeEach
    final void setUp() throws Exception {

        logic = LogicFactory.getFor("Post");
        rlogic = LogicFactory.getFor("RedditAccount");
        slogic = LogicFactory.getFor("Subreddit");

        Post entity = new Post();
        entity.setCreated(Date.from(Instant.now(Clock.systemDefaultZone())));
        entity.setTitle("Junit 5 Test");
        entity.setRedditAccountId(rlogic.getWithId(1));
        entity.setSubredditId(slogic.getWithId(1));
        entity.setCommentCount(1);
        entity.setPoints(1);
        entity.setUniqueId("test");

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction
        em.getTransaction().begin();
        //add an account to hibernate, account is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedEntity = em.merge(entity);
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();
    }

    @AfterEach
    final void tearDown() throws Exception {
        if (expectedEntity != null) {
            logic.delete(expectedEntity);
        }
    }

    @Test
    final void testGetAll() {
        //get all the accounts from the DB
        List<Post> list = logic.getAll();
        //store the size of list, this way we know how many accounts exits in DB
        int originalSize = list.size();

        //make sure account was created successfully
        assertNotNull(expectedEntity);
        //delete the new account
        logic.delete(expectedEntity);

        //get all accounts again
        list = logic.getAll();
        //the new size of accounts must be one less
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * helper method for testing all account fields
     *
     * @param expected
     * @param actual
     */
    private void assertPostEquals(Post expected, Post actual) {
        //assert all field to guarantee they are the same
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getRedditAccountId().getId(), actual.getRedditAccountId().getId());
        assertEquals(expected.getSubredditId().getId(), actual.getSubredditId().getId());
        assertEquals(expected.getUniqueID(), actual.getUniqueID());
        assertEquals(expected.getPoints(), actual.getPoints());
        assertEquals(expected.getCommentCount(), actual.getCommentCount());
        assertEquals(expected.getTitle(), actual.getTitle());

//        to compare dates
        long timeInMilliSeconds1 = expected.getCreated().getTime();
        long timeInMilliSeconds2 = actual.getCreated().getTime();

        long errorRangeInMilliSeconds = 10000;//10 seconds

        assertTrue(Math.abs(timeInMilliSeconds1 - timeInMilliSeconds2) < errorRangeInMilliSeconds);
    }
    
    //new method created
    private void assertDateEquals(Post expected, Post actual) {
            long timeInMilliSeconds1 = expected.getCreated().getTime();
        long timeInMilliSeconds2 = actual.getCreated().getTime();

        long errorRangeInMilliSeconds = 10000;//10 seconds

        assertTrue(Math.abs(timeInMilliSeconds1 - timeInMilliSeconds2) < errorRangeInMilliSeconds);
    }

    
    @Test
    final void testGetWithId() {
        //using the id of test account get another account from logic
        Post returnedPost = logic.getWithId(expectedEntity.getId());

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertPostEquals(expectedEntity, returnedPost);
    }

    @Test
    final void testGetPostWithUniqueId() {
        Post returnedPost = logic.getPostWithUniqueId(expectedEntity.getUniqueID());

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertPostEquals(expectedEntity, returnedPost);
    }

    @Test
    final void testGetPostWithPoints() {
        List<Post> returnedPost = logic.getPostWithPoints(expectedEntity.getPoints());

        for (Post post : returnedPost) {
            assertEquals(expectedEntity.getPoints(), post.getPoints());
        }
    }

    @Test
    final void testGetPostsWithCommentCount() {
        int foundFull = 0;
        List<Post> returnedPost = logic.getPostsWithCommentCount(expectedEntity.getCommentCount());

        for (Post post : returnedPost) {
            //all accounts must have the same password
            assertEquals(expectedEntity.getCommentCount(), post.getCommentCount());
            //exactly one account must be the same
            if (post.getId().equals(expectedEntity.getId())) {
                assertPostEquals(expectedEntity, post);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }

    @Test
    final void testGetPostsWithAuthorID() {
        List<Post> returnedPost = logic.getPostsWithAuthorID(expectedEntity.getId());
        returnedPost.forEach(post -> {
            assertPostEquals(expectedEntity, post);
        });
    }

    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(PostLogic.CREATED, new String[]{"2020-12-12 12:12:12"});
        sampleMap.put(PostLogic.TITLE, new String[]{"Test Post Title"});
        sampleMap.put(PostLogic.UNIQUE_ID, new String[]{Integer.toString(13)});
        sampleMap.put(PostLogic.POINTS, new String[]{Integer.toString(131313)});
        sampleMap.put(PostLogic.COMMENT_COUNT, new String[]{Integer.toString(131313)});

        //add dependency
        RedditAccount ra = rlogic.getWithId(expectedEntity.getRedditAccountId().getId());
        Subreddit sub = slogic.getWithId(expectedEntity.getSubredditId().getId());

        Post returnedPost = logic.createEntity(sampleMap);

        returnedPost.setRedditAccountId(ra);
        returnedPost.setSubredditId(sub);

        logic.add(returnedPost);

        returnedPost = logic.getPostWithUniqueId(returnedPost.getUniqueID());
        logic.delete(returnedPost);

        assertEquals(sampleMap.get(PostLogic.CREATED)[0], logic.convertDateToString(returnedPost.getCreated()) );
        assertEquals(sampleMap.get(PostLogic.TITLE)[0], returnedPost.getTitle());
        assertEquals(ra.getId(), returnedPost.getRedditAccountId().getId());
        assertEquals(sampleMap.get(PostLogic.UNIQUE_ID)[0], returnedPost.getUniqueID());
        assertEquals(sub.getId(), returnedPost.getSubredditId().getId());
        assertEquals(sampleMap.get(PostLogic.POINTS)[0], Integer.toString(returnedPost.getPoints()));
        assertEquals(sampleMap.get(PostLogic.COMMENT_COUNT)[0], Integer.toString(returnedPost.getCommentCount()));

    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();

        sampleMap.put(PostLogic.ID, new String[]{Integer.toString(expectedEntity.getId())});
        sampleMap.put(PostLogic.CREATED, new String[]{logic.convertDateToString(expectedEntity.getCreated())});
        sampleMap.put(PostLogic.TITLE, new String[]{expectedEntity.getTitle()});
        sampleMap.put(PostLogic.REDDIT_ACCOUNT_ID, new String[]{Integer.toString(expectedEntity.getRedditAccountId().getId())});
        sampleMap.put(PostLogic.UNIQUE_ID, new String[]{expectedEntity.getUniqueID()});
        sampleMap.put(PostLogic.SUBREDDIT_ID, new String[]{Integer.toString(expectedEntity.getSubredditId().getId())});
        sampleMap.put(PostLogic.POINTS, new String[]{Integer.toString(expectedEntity.getPoints())});
        sampleMap.put(PostLogic.COMMENT_COUNT, new String[]{Integer.toString(expectedEntity.getCommentCount())});

        RedditAccount ra = rlogic.getWithId(expectedEntity.getRedditAccountId().getId());
        Subreddit sub = slogic.getWithId(expectedEntity.getSubredditId().getId());

        Post returnedPost = logic.createEntity(sampleMap);
        returnedPost.setRedditAccountId(ra);
        returnedPost.setSubredditId(sub);

        assertPostEquals(expectedEntity, returnedPost);
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(PostLogic.ID, new String[]{Integer.toString(expectedEntity.getId())});
            map.put(PostLogic.CREATED, new String[]{logic.convertDateToString(expectedEntity.getCreated())});
            map.put(PostLogic.TITLE, new String[]{expectedEntity.getTitle()});
            map.put(PostLogic.REDDIT_ACCOUNT_ID, new String[]{Integer.toString(expectedEntity.getRedditAccountId().getId())});
            map.put(PostLogic.UNIQUE_ID, new String[]{expectedEntity.getUniqueID()});
            map.put(PostLogic.SUBREDDIT_ID, new String[]{Integer.toString(expectedEntity.getSubredditId().getId())});
            map.put(PostLogic.POINTS, new String[]{Integer.toString(expectedEntity.getPoints())});
            map.put(PostLogic.COMMENT_COUNT, new String[]{Integer.toString(expectedEntity.getCommentCount())});
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.CREATED, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.CREATED, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.TITLE, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.TITLE, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.UNIQUE_ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.UNIQUE_ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));


        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.POINTS, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.POINTS, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.COMMENT_COUNT, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.COMMENT_COUNT, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(PostLogic.ID, new String[]{Integer.toString(131)});
            map.put(PostLogic.CREATED, new String[]{logic.convertDateToString(expectedEntity.getCreated())});
            map.put(PostLogic.TITLE, new String[]{expectedEntity.getTitle()});
            map.put(PostLogic.REDDIT_ACCOUNT_ID, new String[]{Integer.toString(expectedEntity.getRedditAccountId().getId())});
            map.put(PostLogic.UNIQUE_ID, new String[]{expectedEntity.getUniqueID()});
            map.put(PostLogic.SUBREDDIT_ID, new String[]{Integer.toString(expectedEntity.getSubredditId().getId())});
            map.put(PostLogic.POINTS, new String[]{Integer.toString(expectedEntity.getPoints())});
            map.put(PostLogic.COMMENT_COUNT, new String[]{Integer.toString(expectedEntity.getCommentCount())});
        };

        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            //from 97 inclusive to 123 exclusive
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.ID, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.ID, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.TITLE, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.TITLE, new String[]{generateString.apply( 256 )});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.UNIQUE_ID, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.UNIQUE_ID, new String[]{generateString.apply( 11 )});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.POINTS, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.POINTS, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.COMMENT_COUNT, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.COMMENT_COUNT, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityEdgeValues() {
        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(PostLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(PostLogic.CREATED, new String[]{"1000-01-01 00:00:00"});
        sampleMap.put(PostLogic.TITLE, new String[]{Integer.toString(1)});
        sampleMap.put(PostLogic.UNIQUE_ID, new String[]{generateString.apply(1)});
        sampleMap.put(PostLogic.POINTS, new String[]{Integer.toString(1)});
        sampleMap.put(PostLogic.COMMENT_COUNT, new String[]{Integer.toString(1)});

        //idealy every test should be in its own method
        Post returnedPost = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(PostLogic.ID)[0]), returnedPost.getId());
        assertEquals(logic.convertStringToDate(sampleMap.get(PostLogic.CREATED)[0]), returnedPost.getCreated());
        assertEquals(sampleMap.get(PostLogic.TITLE)[0], returnedPost.getTitle());
        assertEquals(sampleMap.get( PostLogic.UNIQUE_ID )[ 0 ] , returnedPost.getUniqueID() );
        assertEquals(Integer.parseInt(sampleMap.get(PostLogic.POINTS)[0]), returnedPost.getPoints());
        assertEquals(Integer.parseInt(sampleMap.get(PostLogic.COMMENT_COUNT)[0]), returnedPost.getCommentCount());

        sampleMap = new HashMap<>();
        sampleMap.put(PostLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(PostLogic.CREATED, new String[]{"9999-12-31 23:59:59"});
        sampleMap.put(PostLogic.TITLE, new String[]{generateString.apply(255)});
        sampleMap.put(PostLogic.UNIQUE_ID, new String[]{Integer.toString(Integer.MAX_VALUE)});
        sampleMap.put(PostLogic.POINTS, new String[]{Integer.toString(Integer.MAX_VALUE)});
        sampleMap.put(PostLogic.COMMENT_COUNT, new String[]{Integer.toString(Integer.MAX_VALUE)});

        //idealy every test should be in its own method
        returnedPost = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(PostLogic.ID)[0]), returnedPost.getId());
        assertEquals(logic.convertStringToDate(sampleMap.get(PostLogic.CREATED)[0]), returnedPost.getCreated());
        assertEquals(sampleMap.get(PostLogic.TITLE)[0], returnedPost.getTitle());
        assertEquals(sampleMap.get(PostLogic.UNIQUE_ID)[0], returnedPost.getUniqueID());
        assertEquals(Integer.parseInt(sampleMap.get(PostLogic.POINTS)[0]), returnedPost.getPoints());
        assertEquals(Integer.parseInt(sampleMap.get(PostLogic.COMMENT_COUNT)[0]), returnedPost.getCommentCount());
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("ID", "Reddit Account ID", "SubReddit ID", "Unique ID", "Points", "Comment Count", "Title", "Created "), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(PostLogic.ID, PostLogic.REDDIT_ACCOUNT_ID, PostLogic.SUBREDDIT_ID, PostLogic.UNIQUE_ID, PostLogic.POINTS, PostLogic.COMMENT_COUNT, PostLogic.TITLE, PostLogic.CREATED), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedEntity);
        assertEquals(expectedEntity.getId(), list.get(0));
        assertEquals(expectedEntity.getRedditAccountId().getId(), ((RedditAccount)list.get(1)).getId()); 
        assertEquals(expectedEntity.getSubredditId().getId(), ((Subreddit)list.get(2)).getId()); 
        assertEquals(expectedEntity.getUniqueID(), list.get(3));
        assertEquals(expectedEntity.getPoints(), list.get(4));
        assertEquals(expectedEntity.getCommentCount(), list.get(5));
        assertEquals(expectedEntity.getTitle(), list.get(6));
        assertEquals(expectedEntity.getCreated(), list.get(7));
    }
}