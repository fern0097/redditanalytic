package reddit.wrapper;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.SubredditReference;
import reddit.DeveloperAccount;

/**
 * A wrapper class for JRAW library to access Reddit API.
 *
 * @see <a href="https://www.reddit.com/dev/api/oauth">Documentation for the Reddit API</a><br>
 * @see <a href="https://github.com/mattbdean/JRAW">JRAW GitHub repository</a><br>
 * @see <a href="https://mattbdean.gitbooks.io/jraw/content/">JRAW Instructions and CookBook</a><br>
 * @see
 * <a href="https://javadoc.jitpack.io/com/github/mattbdean/JRAW/master-fa1efa3372-1/javadoc/overview-summary.html">JRAW
 * Documentation</a><br>
 *
 * @author Shariar (Shawn) Emami
 * @version November 2, 2020
 */
public class RedditWrapper {

    private static final String PLATFORM = "RedditAnalyticWebBot";
    private static final String VERSION = "v0.1";

    private RedditClient reddit;
    private DefaultPaginator<Submission> paginator;
    private List<Listing<Submission>> pages;
    private Listing<Submission> currentPage;
    private SubredditReference currentSubreddit;

    private void hasAuthenticated() {
        if( reddit == null ){
            throw new IllegalStateException( "authenticate() Method in "
                    + "Scrapper must be called ONCE before everything "
                    + "regarding Scraper" );
        }
    }

    private void hasPagesBeenConfiged() {
        if( paginator == null ){
            throw new IllegalStateException( "buildRedditPagesConfig() Method "
                    + "in Scrapper must be called ONCE before requestNumberOfPages()" );
        }
    }

    private void hasPagesBeenRequested() {
        if( pages == null ){
            throw new IllegalStateException( "requestNumberOfPages() Method "
                    + "in Scrapper must be called ONCE before proccessPageNumber()" );
        }
    }

    private void hasNextPageRequested() {
        if( currentPage == null ){
            throw new IllegalStateException( "requestNextPage() Method "
                    + "in Scrapper must be called ONCE before proccessNextPage()" );
        }
    }

    /**
     * This method is to expose the internal mechanics of JRAW.
     *
     * @return raw RedditClient object provided by JRAW.
     */
    public RedditClient getRawReddit() {
        hasAuthenticated();
        return reddit;
    }

    /**
     * @return A wrapper object of current subreddit being browsed.
     */
    public SubredditWrapper getCurrentSubreddit() {
        hasAuthenticated();
        hasPagesBeenConfiged();
        return new SubredditWrapper( currentSubreddit.about() );
    }

    /**
     *
     * @param username - unique username on reddit
     * @return Account object for the given username.
     */
    public AccountWrapper getAccountFor( String username ) {
        hasAuthenticated();
        return new AccountWrapper( reddit.user( username ).query().getAccount() );
    }

    /**
     * get permission from Reddit to access their API
     *
     * @param dev - a developed account with Reddit API credential set in it.
     *
     * @return current object of Scraper, this
     */
    public RedditWrapper authenticate( DeveloperAccount dev ) {
        Objects.requireNonNull( dev, "A none null and complete developer object must be set" );
        dev.isComplete();
        UserAgent userAgent = new UserAgent( PLATFORM, dev.getAppID(), VERSION, dev.getRedditUser() );
        Credentials credentials = Credentials.userless( dev.getClientID(), dev.getClientSecret(), UUID.randomUUID() );
        NetworkAdapter adapter = new OkHttpNetworkAdapter( userAgent );
        reddit = OAuthHelper.automatic( adapter, credentials );
        return this;
    }

    public RedditWrapper setLogger( boolean log ) {
        hasAuthenticated();
        reddit.setLogHttp( false );
        return this;
    }

    /**
     * configure what subreddit to be downloaded
     *
     * @param subreddit - name of subreddit to access
     * @param postsPerPage - number of post per page to download, max 10
     * @param sort - in what order to sort the posts, ex. Sort.HOT
     *
     * @return current object of Scraper, this
     */
    public RedditWrapper configureCurentSubreddit( String subreddit, int postsPerPage, SubSort sort ) {
        hasAuthenticated();

        currentSubreddit = reddit.subreddit( subreddit );
        paginator = currentSubreddit
                .posts()
                .limit( postsPerPage )
                .sorting( sort.value() )
                .build();
        return this;
    }

    /**
     * get the next Reddit page. first page if next hasn't been called yet.
     *
     * @return current object of Scraper, this
     */
    public RedditWrapper requestNextPage() {
        hasAuthenticated();
        hasPagesBeenConfiged();

        currentPage = paginator.next();
        return this;
    }

    /**
     * start processing current page using the callback lambda. callback lambda uses the Post class to access data in
     * each post. this lambda will be called for every single post, one at a time.
     *
     * @param callback - callback lambda of type Post class
     *
     * @return current object of Scraper, this
     */
    public RedditWrapper proccessCurrentPage( Consumer<PostWrapper> callback ) {
        hasAuthenticated();
        hasPagesBeenConfiged();
        hasNextPageRequested();

        currentPage.forEach( submission -> {
            callback.accept( new PostWrapper( getAccountFor( submission.getAuthor() ), submission.toReference( reddit ), getCurrentSubreddit(), this ) );
        } );
        return this;
    }
}
