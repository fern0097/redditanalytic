package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Rodrigo Tavares
 * @version December 02, 2020
 */

class RedditAccountLogicTest {

    private RedditAccountLogic logic;
    private RedditAccount expectedEntity;

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

        logic = LogicFactory.getFor( "RedditAccount" );
        /* **********************************
         * ***********IMPORTANT**************
         * **********************************/
        //we only do this for the test.
        //always create Entity using logic.
        //we manually make the account to not rely on any logic functionality , just for testing
        RedditAccount entity = new RedditAccount();
        entity.setName("Junit 5 Test");
        entity.setLinkPoints(1);
        entity.setCommentPoints(2);
        entity.setCreated(Date.from(Instant.EPOCH));

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

    /**
     * helper method for testing all redditAccount fields
     *
     * @param expected
     * @param actual
     */
    private void assertRedditAccountEquals( RedditAccount expected, RedditAccount actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getName(), actual.getName() );
        assertEquals( expected.getLinkPoints(), actual.getLinkPoints() );
        assertEquals( expected.getCommentPoints(), actual.getCommentPoints() );
        assertEquals( expected.getCreated(), actual.getCreated() );
    }

    // ***************
    // getAll
    // ***************
    @Test
    final void getAll() {
        //get all the accounts from the DB
        List<RedditAccount> list = logic.getAll();
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
        RedditAccount returnedAccount = logic.getWithId( expectedEntity.getId() );

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertRedditAccountEquals( expectedEntity, returnedAccount );
    }

    // ***************
    // getRedditAccountWithName
    // ***************
    @Test
    final void testGetRedditAccountWithName() {
        RedditAccount returnedAccount = logic.getRedditAccountWithName( expectedEntity.getName() );

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertRedditAccountEquals( expectedEntity, returnedAccount );
    }
    
    // ***************
    // getRedditAccountsWithLinkPoints
    // ***************
    @Test
    final void testGetRedditAccountsWithLinkPoints() {
        List<RedditAccount> returnedAccounts = logic.getRedditAccountsWithLinkPoints( expectedEntity.getLinkPoints() );
        returnedAccounts.forEach(account -> {
            assertRedditAccountEquals( expectedEntity, account );
        });
    }
    
    // ***************
    // getRedditAccountsWithCommentPoints
    // ***************
    @Test
    final void testGetRedditAccountsWithCommentPoints() {
        List<RedditAccount> returnedAccounts = logic.getRedditAccountsWithCommentPoints( expectedEntity.getCommentPoints() );
        returnedAccounts.forEach(account -> {
            assertRedditAccountEquals( expectedEntity, account );
        });
    }
    
    // ***************
    // getRedditAccountsWithCreated
    // ***************    
    @Test
    final void testGetRedditAccountsWithCreated() {
        List<RedditAccount> returnedAccounts = logic.getRedditAccountsWithCreated( expectedEntity.getCreated() );
        returnedAccounts.forEach(account -> {
            assertRedditAccountEquals( expectedEntity, account );
        });
    }
    
    // ***************
    // createEntity
    // *************** 
    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( RedditAccountLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
        sampleMap.put( RedditAccountLogic.NAME, new String[]{ expectedEntity.getName() } );
        sampleMap.put( RedditAccountLogic.LINK_POINTS, new String[]{ Integer.toString ( expectedEntity.getLinkPoints() ) } );
        sampleMap.put( RedditAccountLogic.COMMENT_POINTS, new String[]{ Integer.toString ( expectedEntity.getCommentPoints() ) } );
        sampleMap.put( RedditAccountLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );

        RedditAccount returnedAccount = logic.createEntity( sampleMap );

        assertRedditAccountEquals( expectedEntity, returnedAccount );
    }
    
    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( RedditAccountLogic.NAME, new String[]{ "Test Create Entity" } );
        sampleMap.put( RedditAccountLogic.LINK_POINTS, new String[]{ Integer.toString ( 1 ) } );
        sampleMap.put( RedditAccountLogic.COMMENT_POINTS, new String[]{ Integer.toString ( 2 ) } );
        Date date = Date.from( Instant.EPOCH);
        sampleMap.put( RedditAccountLogic.CREATED, new String[]{ logic.convertDateToString(date) } );
        
        RedditAccount returnedAccount = logic.createEntity( sampleMap );
        logic.add( returnedAccount );

        returnedAccount = logic.getRedditAccountWithName( returnedAccount.getName() );

        assertEquals( sampleMap.get( RedditAccountLogic.NAME )[ 0 ], returnedAccount.getName() );
        assertEquals( sampleMap.get( RedditAccountLogic.LINK_POINTS )[ 0 ], Integer.toString( returnedAccount.getLinkPoints() ) );
        assertEquals( sampleMap.get( RedditAccountLogic.COMMENT_POINTS )[ 0 ], Integer.toString( returnedAccount.getCommentPoints() ) );
        assertEquals( sampleMap.get( RedditAccountLogic.CREATED )[ 0 ], logic.convertDateToString(returnedAccount.getCreated()) );

        logic.delete( returnedAccount );
    }
    
    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( RedditAccountLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( RedditAccountLogic.NAME, new String[]{ expectedEntity.getName() } );
            map.put( RedditAccountLogic.LINK_POINTS, new String[]{ Integer.toString ( expectedEntity.getLinkPoints() ) } );
            map.put( RedditAccountLogic.COMMENT_POINTS, new String[]{ Integer.toString ( expectedEntity.getCommentPoints() ) } );
            map.put( RedditAccountLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( RedditAccountLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( RedditAccountLogic.ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( RedditAccountLogic.NAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( RedditAccountLogic.NAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( RedditAccountLogic.LINK_POINTS, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( RedditAccountLogic.LINK_POINTS, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( RedditAccountLogic.COMMENT_POINTS, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( RedditAccountLogic.COMMENT_POINTS, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( RedditAccountLogic.CREATED, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( RedditAccountLogic.CREATED, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( RedditAccountLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( RedditAccountLogic.NAME, new String[]{ expectedEntity.getName() } );
            map.put( RedditAccountLogic.LINK_POINTS, new String[]{ Integer.toString ( expectedEntity.getLinkPoints() ) } );
            map.put( RedditAccountLogic.COMMENT_POINTS, new String[]{ Integer.toString ( expectedEntity.getCommentPoints() ) } );
            map.put( RedditAccountLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );
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
        sampleMap.replace( RedditAccountLogic.ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( RedditAccountLogic.ID, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( RedditAccountLogic.NAME, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( RedditAccountLogic.NAME, new String[]{ generateString.apply( 101 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( RedditAccountLogic.LINK_POINTS, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( RedditAccountLogic.LINK_POINTS, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( RedditAccountLogic.COMMENT_POINTS, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( RedditAccountLogic.COMMENT_POINTS, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( RedditAccountLogic.CREATED, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( RedditAccountLogic.CREATED, new String[]{ "12b" } );
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
        sampleMap.put( RedditAccountLogic.ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( RedditAccountLogic.NAME, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( RedditAccountLogic.LINK_POINTS, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( RedditAccountLogic.COMMENT_POINTS, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( RedditAccountLogic.CREATED, new String[]{ "1000-01-01 00:00:00" } );

        //idealy every test should be in its own method
        RedditAccount returnedAccount = logic.createEntity( sampleMap );
        assertEquals( Integer.parseInt( sampleMap.get( RedditAccountLogic.ID )[ 0 ] ), returnedAccount.getId() );
        assertEquals( sampleMap.get( RedditAccountLogic.NAME )[ 0 ], returnedAccount.getName() );
        assertEquals( Integer.parseInt( sampleMap.get( RedditAccountLogic.LINK_POINTS )[ 0 ]), returnedAccount.getLinkPoints() );
        assertEquals( Integer.parseInt(sampleMap.get( RedditAccountLogic.COMMENT_POINTS )[ 0 ]), returnedAccount.getCommentPoints() );
        assertEquals( logic.convertStringToDate(sampleMap.get( RedditAccountLogic.CREATED )[ 0 ]), returnedAccount.getCreated() );

        sampleMap = new HashMap<>();
        sampleMap.put( RedditAccountLogic.ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( RedditAccountLogic.NAME, new String[]{ generateString.apply( 100 ) } );
        sampleMap.put( RedditAccountLogic.LINK_POINTS, new String[]{ Integer.toString( Integer.MAX_VALUE ) } );
        sampleMap.put( RedditAccountLogic.COMMENT_POINTS, new String[]{ Integer.toString( Integer.MAX_VALUE ) } );
        sampleMap.put( RedditAccountLogic.CREATED, new String[]{ "9999-12-31 23:59:59" } );
        
        //idealy every test should be in its own method
        returnedAccount = logic.createEntity( sampleMap );
        assertEquals( Integer.parseInt( sampleMap.get( RedditAccountLogic.ID )[ 0 ] ), returnedAccount.getId() );
        assertEquals( sampleMap.get( RedditAccountLogic.NAME )[ 0 ], returnedAccount.getName() );
        assertEquals( Integer.parseInt( sampleMap.get( RedditAccountLogic.LINK_POINTS )[ 0 ]), returnedAccount.getLinkPoints() );
        assertEquals( Integer.parseInt(sampleMap.get( RedditAccountLogic.COMMENT_POINTS )[ 0 ]), returnedAccount.getCommentPoints() );
        assertEquals( logic.convertStringToDate(sampleMap.get( RedditAccountLogic.CREATED )[ 0 ]), returnedAccount.getCreated() );
    }
    
    // ***************
    // getColumnNames
    // ***************     
    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList( "ID", "Name", "Link Points", "Comment Points", "Created" ), list );
    }
    
    // ***************
    // getColumnCodes
    // *************** 
    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( 
                RedditAccountLogic.ID, 
                RedditAccountLogic.NAME, 
                RedditAccountLogic.LINK_POINTS,
                RedditAccountLogic.COMMENT_POINTS,
                RedditAccountLogic.CREATED),
                list );
    }
    
    // ***************
    // extractDataAsList
    // *************** 
    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( expectedEntity );
        assertEquals( expectedEntity.getId(), list.get(0) );
        assertEquals( expectedEntity.getName(), list.get(1) );
        assertEquals( expectedEntity.getLinkPoints(), list.get(2) );
        assertEquals( expectedEntity.getCommentPoints(), list.get(3) );
        assertEquals( expectedEntity.getCreated(), list.get(4) );
    }
}