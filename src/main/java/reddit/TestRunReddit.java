package reddit;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import reddit.wrapper.CommentSort;
import reddit.wrapper.PostWrapper;
import reddit.wrapper.RedditWrapper;
import reddit.wrapper.SubSort;
import reddit.wrapper.SubredditWrapper;

/**
 * this is just a test class to see if your Reddit credentials are correct.
 *
 * @author Shariar (Shawn) Emami
 * @version November 2, 2020
 */
public class TestRunReddit {

    /**
     * use this example for your code.
     *
     * @throws IOException
     */
    private static void exampleForReadingComemnts() throws IOException {
        //TODO fill in your reddit infromation here
        String clientID = "agVSwD1WixJT9Q";
        String clientSecret = "-Y1qbx4brQ9iuW_ySxmxkic6sUs";
        String redditUser = "adrianoreck";
        String algonquinUser = "reck0014";

        DeveloperAccount dev = new DeveloperAccount()
                .setClientID( clientID )
                .setClientSecret( clientSecret )
                .setRedditUser( redditUser )
                .setAlgonquinUser( algonquinUser );

        //create a new scraper
        RedditWrapper scrap = new RedditWrapper();
        //authenticate and set up a page for wallpaper subreddit with 5 posts soreted by HOT order
        scrap.authenticate( dev ).setLogger( false );
        scrap.configureCurentSubreddit( "politics", 1, SubSort.BEST );

        SubredditWrapper sub   = scrap.getCurrentSubreddit();
        
        //create a lambda that accepts post
        Consumer<PostWrapper> saveData = ( PostWrapper post ) -> {
            if( post.isPinned() ){
                return;
            }
            System.out.println( "************" + post.getTitle() );
            System.out.println( "************" + post.getUrl() );
            post.configComments( 1, 1, CommentSort.CONFIDENCE );
            post.processComments( comment -> {
                if( comment.isPinned() || comment.getDepth() == 0 ){
                    return;
                }
                System.out.println( ( comment.isParrent() ? "----" : comment.getDepth() + ")" ) + "(" + comment.getAuthor().getName() + ")" + comment.getText() );
            } );
        };
        //get the next page and process every post
        scrap.requestNextPage().proccessCurrentPage( saveData );
    }

    /**
     * only run this for testing the scraper it will not run the project.
     *
     * @param args
     *
     * @throws IOException
     */
    public static void main( String[] args ) throws IOException {
        System.out.println( "Working Directory = " + System.getProperty( "user.dir" ) );

        exampleForReadingComemnts();

        Logger.getLogger( TestRunReddit.class.getName() ).log( Level.INFO, "leaving main" );
    }
}
