package reddit.wrapper;

import net.dean.jraw.models.Subreddit;

/**
 * <p>
 * Wrapper class for {@link net.dean.jraw.models.Subreddit} in JRAW. <br>
 * It is only created by {@link reddit.wrapper.Reddit} class but can be used at any scope.</p>
 *
 * @see
 * <a href="https://javadoc.jitpack.io/com/github/mattbdean/JRAW/master-fa1efa3372-1/javadoc/net/dean/jraw/models/Subreddit.html">net.dean.jraw.models.Subreddit
 * JavaDoc</a><br>
 *
 * @author Shariar (Shawn) Emami
 * @version November 2, 2020
 */
public class SubredditWrapper {

    private net.dean.jraw.models.Subreddit sub;

    SubredditWrapper( Subreddit sub ) {
        this.sub = sub;
    }

    /**
     * @return if this Subreddit is NSFW;
     */
    public boolean isNsfw() {
        return sub.isNsfw();
    }

    /**
     *
     * @return The URL to access this subreddit relative to reddit.com. For example, "/r/pics"
     */
    public String getReletiveUrl() {
        return sub.getUrl();
    }

    /**
     *
     * @return Name without the "/r/" prefix: "pics", "funny", etc.
     */
    public String getName() {
        return sub.getName();
    }

    /**
     *
     * @return The title of the tab when visiting this subreddit on a web browser
     */
    public String getTitle() {
        return sub.getTitle();
    }

    /**
     *
     * @return The amount of subscribers this subreddit has. Returns -1 if the subreddit is inaccessible to the current
     * user.
     */
    public int getSubscribers() {
        return sub.getSubscribers();
    }
}
