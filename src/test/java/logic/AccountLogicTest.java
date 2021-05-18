package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Account;
import java.util.Arrays;
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
 *
 * @author Shariar
 */
class AccountLogicTest {

    private AccountLogic logic;
    private Account expectedEntity;

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

        logic = LogicFactory.getFor( "Account" );
        /* **********************************
         * ***********IMPORTANT**************
         * **********************************/
        //we only do this for the test.
        //always create Entity using logic.
        //we manually make the account to not rely on any logic functionality , just for testing
        Account entity = new Account();
        entity.setDisplayname( "Junit 5 Test" );
        entity.setUsername( "junit" );
        entity.setPassword( "junit5" );

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

    @Test
    final void testGetAll() {
        //get all the accounts from the DB
        List<Account> list = logic.getAll();
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

    /**
     * helper method for testing all account fields
     *
     * @param expected
     * @param actual
     */
    private void assertAccountEquals( Account expected, Account actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getDisplayname(), actual.getDisplayname() );
        assertEquals( expected.getUsername(), actual.getUsername() );
        assertEquals( expected.getPassword(), actual.getPassword() );
    }

    @Test
    final void testGetWithId() {
        //using the id of test account get another account from logic
        Account returnedAccount = logic.getWithId( expectedEntity.getId() );

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertAccountEquals( expectedEntity, returnedAccount );
    }

    @Test
    final void testGetAccountWithDisplayName() {
        Account returnedAccount = logic.getAccountWithDisplayname( expectedEntity.getDisplayname() );

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertAccountEquals( expectedEntity, returnedAccount );
    }

    @Test
    final void testGetAccountWIthUser() {
        Account returnedAccount = logic.getAccountWithUsername( expectedEntity.getUsername() );

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertAccountEquals( expectedEntity, returnedAccount );
    }

    @Test
    final void testGetAccountsWithPassword() {
        int foundFull = 0;
        List<Account> returnedAccounts = logic.getAccountsWithPassword( expectedEntity.getPassword() );
        for( Account account: returnedAccounts ) {
            //all accounts must have the same password
            assertEquals( expectedEntity.getPassword(), account.getPassword() );
            //exactly one account must be the same
            if( account.getId().equals( expectedEntity.getId() ) ){
                assertAccountEquals( expectedEntity, account );
                foundFull++;
            }
        }
        assertEquals( 1, foundFull, "if zero means not found, if more than one means duplicate" );
    }

    @Test
    final void testGetAccountWith() {
        Account returnedAccount = logic.isCredentialValid( expectedEntity.getUsername(), expectedEntity.getPassword() );
        assertAccountEquals( expectedEntity, returnedAccount );
    }

    @Test
    final void testSearch() {
        int foundFull = 0;
        //search for a substring of one of the fields in the expectedAccount
        String searchString = expectedEntity.getDisplayname().substring( 3 );
        //in account we only search for display name and user, this is completely based on your design for other entities.
        List<Account> returnedAccounts = logic.search( searchString );
        for( Account account: returnedAccounts ) {
            //all accounts must contain the substring
            assertTrue( account.getDisplayname().contains( searchString ) || account.getUsername().contains( searchString ) );
            //exactly one account must be the same
            if( account.getId().equals( expectedEntity.getId() ) ){
                assertAccountEquals( expectedEntity, account );
                foundFull++;
            }
        }
        assertEquals( 1, foundFull, "if zero means not found, if more than one means duplicate" );
    }

    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( AccountLogic.DISPLAYNAME, new String[]{ "Test Create Entity" } );
        sampleMap.put( AccountLogic.USERNAME, new String[]{ "testCreateAccount" } );
        sampleMap.put( AccountLogic.PASSWORD, new String[]{ "create" } );

        Account returnedAccount = logic.createEntity( sampleMap );
        logic.add( returnedAccount );

        returnedAccount = logic.getAccountWithUsername( returnedAccount.getUsername() );

        assertEquals( sampleMap.get( AccountLogic.DISPLAYNAME )[ 0 ], returnedAccount.getDisplayname() );
        assertEquals( sampleMap.get( AccountLogic.USERNAME )[ 0 ], returnedAccount.getUsername() );
        assertEquals( sampleMap.get( AccountLogic.PASSWORD )[ 0 ], returnedAccount.getPassword() );

        logic.delete( returnedAccount );
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( AccountLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
        sampleMap.put( AccountLogic.DISPLAYNAME, new String[]{ expectedEntity.getDisplayname() } );
        sampleMap.put( AccountLogic.USERNAME, new String[]{ expectedEntity.getUsername() } );
        sampleMap.put( AccountLogic.PASSWORD, new String[]{ expectedEntity.getPassword() } );

        Account returnedAccount = logic.createEntity( sampleMap );

        assertAccountEquals( expectedEntity, returnedAccount );
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( AccountLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( AccountLogic.DISPLAYNAME, new String[]{ expectedEntity.getDisplayname() } );
            map.put( AccountLogic.USERNAME, new String[]{ expectedEntity.getUsername() } );
            map.put( AccountLogic.PASSWORD, new String[]{ expectedEntity.getPassword() } );
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.DISPLAYNAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.DISPLAYNAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.USERNAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.USERNAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.PASSWORD, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.PASSWORD, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( AccountLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( AccountLogic.DISPLAYNAME, new String[]{ expectedEntity.getDisplayname() } );
            map.put( AccountLogic.USERNAME, new String[]{ expectedEntity.getUsername() } );
            map.put( AccountLogic.PASSWORD, new String[]{ expectedEntity.getPassword() } );
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
        sampleMap.replace( AccountLogic.ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.ID, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.DISPLAYNAME, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.DISPLAYNAME, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.USERNAME, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.USERNAME, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.PASSWORD, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.PASSWORD, new String[]{ generateString.apply( 46 ) } );
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
        sampleMap.put( AccountLogic.ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( AccountLogic.DISPLAYNAME, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( AccountLogic.USERNAME, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( AccountLogic.PASSWORD, new String[]{ generateString.apply( 1 ) } );

        //idealy every test should be in its own method
        Account returnedAccount = logic.createEntity( sampleMap );
        assertEquals( Integer.parseInt( sampleMap.get( AccountLogic.ID )[ 0 ] ), returnedAccount.getId() );
        assertEquals( sampleMap.get( AccountLogic.DISPLAYNAME )[ 0 ], returnedAccount.getDisplayname() );
        assertEquals( sampleMap.get( AccountLogic.USERNAME )[ 0 ], returnedAccount.getUsername() );
        assertEquals( sampleMap.get( AccountLogic.PASSWORD )[ 0 ], returnedAccount.getPassword() );

        sampleMap = new HashMap<>();
        sampleMap.put( AccountLogic.ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( AccountLogic.DISPLAYNAME, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( AccountLogic.USERNAME, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( AccountLogic.PASSWORD, new String[]{ generateString.apply( 45 ) } );

        //idealy every test should be in its own method
        returnedAccount = logic.createEntity( sampleMap );
        assertEquals( Integer.parseInt( sampleMap.get( AccountLogic.ID )[ 0 ] ), returnedAccount.getId() );
        assertEquals( sampleMap.get( AccountLogic.DISPLAYNAME )[ 0 ], returnedAccount.getDisplayname() );
        assertEquals( sampleMap.get( AccountLogic.USERNAME )[ 0 ], returnedAccount.getUsername() );
        assertEquals( sampleMap.get( AccountLogic.PASSWORD )[ 0 ], returnedAccount.getPassword() );
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList( "ID", "Displayname", "Username", "Password" ), list );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( AccountLogic.ID, AccountLogic.DISPLAYNAME, AccountLogic.USERNAME, AccountLogic.PASSWORD ), list );
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( expectedEntity );
        assertEquals( expectedEntity.getId(), list.get( 0 ) );
        assertEquals( expectedEntity.getDisplayname(), list.get( 1 ) );
        assertEquals( expectedEntity.getUsername(), list.get( 2 ) );
        assertEquals( expectedEntity.getPassword(), list.get( 3 ) );
    }
}
