/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Comment;
import entity.Post;
import entity.RedditAccount;
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
import static logic.CommentLogic.CREATED;
import static logic.CommentLogic.ID;
import static logic.CommentLogic.IS_REPLY;
import static logic.CommentLogic.POINTS;
import static logic.CommentLogic.POST_ID;
import static logic.CommentLogic.REDDIT_ACCOUNT_ID;
import static logic.CommentLogic.REPLYS;
import static logic.CommentLogic.TEXT;
import static logic.CommentLogic.UNIQUE_ID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author reck0014
 */
public class CommentLogicTest {
    
    private Comment expectedEntity;
    private CommentLogic logic;
    
    
    private RedditAccountLogic ralogic;
    private PostLogic postlogic;
    
    
    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat( "/RedditAnalytic", "common.ServletListener" );
    }

    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }

    @BeforeEach
    final void setUp() throws Exception {

        logic = LogicFactory.getFor( "Comment" );
        ralogic = LogicFactory.getFor( "RedditAccount" );
        postlogic = LogicFactory.getFor("Post");
        /* **********************************
         * ***********IMPORTANT**************
         * **********************************/
        //we only do this for the test.
        //always create Entity using logic.
        //we manually make the account to not rely on any logic functionality , just for testing
        Comment entity = new Comment();
        entity.setRedditAccountId( ralogic.getWithId(1));
        entity.setPostId(postlogic.getWithId(1));
        entity.setUniqueId( "aaaaaa" );
        entity.setText("Test text");
        entity.setCreated(Date.from(Instant.EPOCH));
        entity.setPoints(5);
        entity.setReplys(1);
        entity.setIsReply(true);

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction
        em.getTransaction().begin();
        //add an account to hibernate, account is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedEntity = em.merge( entity );
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();
    }

    @AfterEach
    final void tearDown() throws Exception {
        if( expectedEntity != null ){
            logic.delete( expectedEntity );
        }
    }
    
    private void assertCommentEquals( Comment expected, Comment actual ) {
    //assert all field to guarantee they are the same
    assertEquals( expected.getId(), actual.getId() );
    assertEquals( expected.getRedditAccountId().getId(), actual.getRedditAccountId().getId() );
    assertEquals( expected.getPostId().getId(), actual.getPostId().getId() );
    assertEquals( expected.getUniqueId(), actual.getUniqueId() );
    assertEquals( expected.getText(), actual.getText() );
    assertEquals( expected.getCreated(), actual.getCreated() );
    assertEquals( expected.getPoints(), actual.getPoints() );
    assertEquals( expected.getReplys(), actual.getReplys() );
    assertEquals( expected.getIsReply(), actual.getIsReply() );
    }

    // ***************
    // getAll
    // *************** 
    @Test
    final void getAll() {
        //get all the accounts from the DB
        List<Comment> list = logic.getAll();
        //store the size of list, this way we know how many accounts exits in DB
        int originalSize = list.size();

        //make sure account was created successfully
        assertNotNull( expectedEntity );
        //delete the new account
        logic.delete( expectedEntity );

        //get all accounts again
        list = logic.getAll();
        //the new size of accounts must be one less
        assertEquals( originalSize - 1, list.size() );
    }
    
    
    // ***************
    // getWithId
    // ***************
    @Test
    final void testGetWithId() {
        //using the id of test account get another account from logic
        Comment returnedComment = logic.getWithId( expectedEntity.getId() );

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertCommentEquals( expectedEntity, returnedComment );
    }

    // ***************
    // getCommentWithUniqueId
    // ***************
    @Test
    final void testGetCommentWithUniqueId() {
        Comment returnedComment = logic.getCommentWithUniqueId( expectedEntity.getUniqueId());

        assertCommentEquals( expectedEntity, returnedComment );
    }
    
    // ***************
    // getCommentsWithText
    // ***************
    @Test
    final void testGetCommentsWithText() {
        List<Comment> returnedComments = logic.getCommentsWithText( expectedEntity.getText());

        returnedComments.forEach(comment -> {
        assertCommentEquals( expectedEntity, comment );
        });
    }
    
    // ***************
    // getCommentsWithCreated
    // ***************
    @Test
    final void testGetCommentsWithCreated() {
        List<Comment> returnedComments = logic.getCommentsWithCreated( expectedEntity.getCreated());

        returnedComments.forEach(comment -> {
        assertCommentEquals( expectedEntity, comment );
        });
    }
    
    // ***************
    // getCommentsWithPoints
    // ***************
    @Test
    final void testGetCommentsWithPoints() {
        List<Comment> returnedComments = logic.getCommentsWithPoints( expectedEntity.getPoints());

        returnedComments.forEach(comment -> {
        assertCommentEquals( expectedEntity, comment );
        });
    }
    
    // ***************
    // getCommentsWithReplys
    // ***************
    @Test
    final void testGetCommentsWithReplys() {
        List<Comment> returnedComments = logic.getCommentsWithReplys( expectedEntity.getReplys());

        returnedComments.forEach(comment -> {
        assertCommentEquals( expectedEntity, comment );
        });
    }
    
    // ***************
    // getCommentsWithIsReply
    // ***************
    @Test
    final void testGetCommentsWithIsReply() {
        List<Comment> returnedComments = logic.getCommentsWithIsReply( expectedEntity.getIsReply());

        returnedComments.forEach(comment -> {
        assertCommentEquals( expectedEntity, comment );
        });
    }
    
    
    // ***************
    // createEntity
    // *************** 
    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( CommentLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
        sampleMap.put( CommentLogic.REDDIT_ACCOUNT_ID, new String[]{ Integer.toString( expectedEntity.getRedditAccountId().getId()) } );
        sampleMap.put( CommentLogic.POST_ID, new String[]{ Integer.toString( expectedEntity.getPostId().getId()) } );
        sampleMap.put( CommentLogic.UNIQUE_ID, new String[]{ expectedEntity.getUniqueId() } );
        sampleMap.put( CommentLogic.TEXT, new String[]{ expectedEntity.getText() } );
        sampleMap.put( CommentLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );
        sampleMap.put( CommentLogic.POINTS, new String[]{ Integer.toString( expectedEntity.getPoints()) } );
        sampleMap.put( CommentLogic.REPLYS, new String[]{ Integer.toString( expectedEntity.getReplys()) } );
        sampleMap.put( CommentLogic.IS_REPLY, new String[]{ Boolean.toString( expectedEntity.getIsReply()) } );
        
        RedditAccount ra = ralogic.getWithId(expectedEntity.getRedditAccountId().getId());
        Post post = postlogic.getWithId(expectedEntity.getPostId().getId());        

        Comment returnedComment = logic.createEntity( sampleMap );
        returnedComment.setRedditAccountId(ra);
        returnedComment.setPostId(post);     

        assertCommentEquals( expectedEntity, returnedComment );
    }
    
    
    // ***************
    // createEntityAndAdd
    // *************** 
    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( CommentLogic.REDDIT_ACCOUNT_ID, new String[]{ "2" } );
        sampleMap.put( CommentLogic.POST_ID, new String[]{ "2" } );
        sampleMap.put( CommentLogic.UNIQUE_ID, new String[]{ "xxxxxx" } );
        sampleMap.put( CommentLogic.TEXT, new String[]{ "test Create and add" } );
        Date date = Date.from( Instant.EPOCH);
        sampleMap.put( CommentLogic.CREATED, new String[]{ logic.convertDateToString(date) } );
        sampleMap.put( CommentLogic.POINTS, new String[]{ "2" } );
        sampleMap.put( CommentLogic.REPLYS, new String[]{ "2" } );
        sampleMap.put( CommentLogic.IS_REPLY, new String[]{ "true" } );
        
        RedditAccount ra = ralogic.getWithId(expectedEntity.getRedditAccountId().getId());
        Post post = postlogic.getWithId(expectedEntity.getPostId().getId());
        
        Comment returnedComment = logic.createEntity( sampleMap );
        returnedComment.setRedditAccountId(ra);
        returnedComment.setPostId(post); 
        logic.add( returnedComment );

        returnedComment = logic.getCommentWithUniqueId(returnedComment.getUniqueId());

        assertEquals( sampleMap.get( CommentLogic.UNIQUE_ID )[ 0 ], returnedComment.getUniqueId() );
        assertEquals( sampleMap.get( CommentLogic.TEXT )[ 0 ], returnedComment.getText() );
        assertEquals( sampleMap.get( CommentLogic.CREATED )[ 0 ], logic.convertDateToString(returnedComment.getCreated()) );
        assertEquals( sampleMap.get( CommentLogic.POINTS )[ 0 ], Integer.toString(returnedComment.getPoints() ));
        assertEquals( sampleMap.get( CommentLogic.REPLYS )[ 0 ], Integer.toString(returnedComment.getReplys() ));
        assertEquals( sampleMap.get( CommentLogic.IS_REPLY )[ 0 ], Boolean.toString(returnedComment.getIsReply() ));
        
        logic.delete( returnedComment );
    }
    
    
    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( CommentLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( CommentLogic.REDDIT_ACCOUNT_ID, new String[]{ Integer.toString(expectedEntity.getRedditAccountId().getId())} );
            map.put( CommentLogic.POST_ID, new String[]{ Integer.toString(expectedEntity.getPostId().getId()) } );
            map.put( CommentLogic.UNIQUE_ID, new String[]{ expectedEntity.getUniqueId() } );
            map.put( CommentLogic.TEXT, new String[]{ expectedEntity.getText() } );
            Date date = Date.from( Instant.EPOCH);
            map.put( CommentLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );
            map.put( CommentLogic.POINTS, new String[]{ Integer.toString(expectedEntity.getPoints()) } );
            map.put( CommentLogic.REPLYS, new String[]{ Integer.toString(expectedEntity.getReplys()) } );
            map.put( CommentLogic.IS_REPLY, new String[]{ Boolean.toString(expectedEntity.getIsReply()) } );
        };
    
        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( CommentLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( CommentLogic.ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( CommentLogic.UNIQUE_ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( CommentLogic.UNIQUE_ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( CommentLogic.TEXT, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( CommentLogic.TEXT, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( CommentLogic.CREATED, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( CommentLogic.CREATED, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( CommentLogic.POINTS, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( CommentLogic.POINTS, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( CommentLogic.REPLYS, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( CommentLogic.REPLYS, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( CommentLogic.IS_REPLY, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( CommentLogic.IS_REPLY, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
    }
    
    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( CommentLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( CommentLogic.REDDIT_ACCOUNT_ID, new String[]{ Integer.toString(expectedEntity.getRedditAccountId().getId())} );
            map.put( CommentLogic.POST_ID, new String[]{ Integer.toString(expectedEntity.getPostId().getId()) } );
            map.put( CommentLogic.UNIQUE_ID, new String[]{ expectedEntity.getUniqueId() } );
            map.put( CommentLogic.TEXT, new String[]{ expectedEntity.getText() } );
            Date date = Date.from( Instant.EPOCH);
            map.put( CommentLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );
            map.put( CommentLogic.POINTS, new String[]{ Integer.toString(expectedEntity.getPoints()) } );
            map.put( CommentLogic.REPLYS, new String[]{ Integer.toString(expectedEntity.getReplys()) } );
            map.put( CommentLogic.IS_REPLY, new String[]{ Boolean.toString(expectedEntity.getIsReply()) } );
        };

        IntFunction<String> generateString = ( int length ) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            //from 97 inclusive to 123 exclusive
            return new Random().ints( 'a', 'z' + 1 ).limit( length )
                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                    .toString();
        };
        
        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( CommentLogic.ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( CommentLogic.ID, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( CommentLogic.UNIQUE_ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( CommentLogic.UNIQUE_ID, new String[]{ generateString.apply( 11 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( CommentLogic.TEXT, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( CommentLogic.POINTS, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( CommentLogic.POINTS, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( CommentLogic.REPLYS, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( CommentLogic.REPLYS, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        
        
    }    
    
    @Test
    final void testCreateEntityEdgeValues() {
        IntFunction<String> generateString = ( int length ) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints( 'a', 'z' + 1 ).limit( length )
                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                    .toString();
        };

        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( CommentLogic.ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( CommentLogic.REDDIT_ACCOUNT_ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( CommentLogic.POST_ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( CommentLogic.UNIQUE_ID, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( CommentLogic.TEXT, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( CommentLogic.CREATED, new String[]{ "1000-01-01 00:00:00" } );
        sampleMap.put( CommentLogic.POINTS, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( CommentLogic.REPLYS, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( CommentLogic.IS_REPLY, new String[]{ Boolean.toString(true) } );
        
        //idealy every test should be in its own method
        Comment returnedAccount = logic.createEntity( sampleMap );
        assertEquals( Integer.parseInt( sampleMap.get( CommentLogic.ID )[ 0 ] ), returnedAccount.getId() );
        assertEquals( sampleMap.get( CommentLogic.UNIQUE_ID )[ 0 ], returnedAccount.getUniqueId());
        assertEquals( sampleMap.get( CommentLogic.TEXT )[ 0 ], returnedAccount.getText());
        assertEquals( logic.convertStringToDate(sampleMap.get( CommentLogic.CREATED )[ 0 ]), returnedAccount.getCreated() );
        assertEquals( Integer.parseInt( sampleMap.get( CommentLogic.POINTS )[ 0 ] ), returnedAccount.getPoints());
        assertEquals( Integer.parseInt( sampleMap.get( CommentLogic.REPLYS )[ 0 ] ), returnedAccount.getReplys());
        assertEquals( Boolean.parseBoolean(sampleMap.get( CommentLogic.IS_REPLY )[ 0 ] ), returnedAccount.getIsReply());


        
    }
    
    // ***************
    // getColumnNames
    // ***************      
    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList( "ID", "Reddit Account ID", "Post ID", "Unique ID", "Text", "Created", "Points", "Replys", "Is Reply" ), list );
    }
    
    // ***************
    // getColumnCodes
    // ***************    
    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( CommentLogic.ID, CommentLogic.REDDIT_ACCOUNT_ID, CommentLogic.POST_ID, CommentLogic.UNIQUE_ID, CommentLogic.TEXT, CommentLogic.CREATED, CommentLogic.POINTS, CommentLogic.REPLYS, CommentLogic.IS_REPLY ), list );
    }

    // ***************
    // extractDataAsList
    // ***************     
    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( expectedEntity );
        assertEquals( expectedEntity.getId(), list.get( 0 ) );
        assertEquals( expectedEntity.getRedditAccountId().getId(), ((RedditAccount)list.get(1)).getId());
        assertEquals( expectedEntity.getPostId().getId(), ((Post)list.get(2)).getId());
        assertEquals( expectedEntity.getUniqueId(), list.get( 3 ) );
        assertEquals( expectedEntity.getText(), list.get( 4 ) );
        assertEquals( expectedEntity.getCreated(), list.get( 5 ) );
        assertEquals( expectedEntity.getPoints(), list.get( 6 ) );
        assertEquals( expectedEntity.getReplys(), list.get( 7 ) );
        assertEquals( expectedEntity.getIsReply(), list.get( 8 ) );
    }
    
}
