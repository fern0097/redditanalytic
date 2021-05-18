package reddit.wrapper;

import java.util.Date;
import java.util.Iterator;
import java.util.function.Consumer;
import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.models.Submission;
import net.dean.jraw.references.CommentsRequest;
import net.dean.jraw.references.SubmissionReference;
import net.dean.jraw.tree.CommentNode;
import net.dean.jraw.tree.RootCommentNode;

/**
 * <p>
 * Wrapper class for {@link net.dean.jraw.models.Submission} in JRAW.<br>
 * It is only created by {@link reddit.wrapper.RedditWrapper} class but can be used at any scope.</p>
 *
 * @see
 * <a href="https://javadoc.jitpack.io/com/github/mattbdean/JRAW/master-fa1efa3372-1/javadoc/net/dean/jraw/models/Submission.html">net.dean.jraw.models.Submission
 * JavaDoc</a><br>
 *
 * @author Shariar (Shawn) Emami
 * @version November 2, 2020
 */
public class PostWrapper {

    private CommentsRequest commentsReq;
    private SubmissionReference subRef;
    private Submission submission;
    private SubredditWrapper sub;
    private RedditWrapper reddit;
    private AccountWrapper acc;

    PostWrapper( AccountWrapper account, SubmissionReference reference, SubredditWrapper sub, RedditWrapper reddit ) {
        submission = reference.inspect();
        subRef = reference;
        acc = account;
        this.sub = sub;
        this.reddit = reddit;
    }

    public String getUniqueID() {
        return submission.getId();
    }

    /**
     *
     * @return true if this post does not contain a lint just, it is just a post on reddit.
     */
    public boolean isSelfPost() {
        return submission.isSelfPost();
    }

    public boolean isImage() {
        return "image".equalsIgnoreCase( submission.getPostHint() );
    }

    public String getPostHint() {
        return submission.getPostHint();
    }

    /**
     * start processing the comments using the callback lambda. callback lambda uses the Comment class to access
     * comments in each post. this lambda will be called for every single comment, one at a time.
     *
     * @param callback - callback lambda of type Comment class
     * @return current object of Post, this
     */
    public PostWrapper processComments( Consumer<CommentWrapper> callback ) {
        RootCommentNode rootComment = subRef.comments( commentsReq );
        Iterator<CommentNode<PublicContribution<?>>> it = rootComment.walkTree().iterator();
        while( it.hasNext() ) {
            CommentNode<PublicContribution<?>> node = it.next();
            AccountWrapper acc = reddit.getAccountFor( node.getSubject().getAuthor() );
            callback.accept( new CommentWrapper( acc, this, node ) );
        }
        return this;
    }

    /**
     * configure how the comments in this post should be viewed.
     *
     * @param replyDepth - number of reply depths for each comment to be loaded.
     * @param totalCountLimit - total number of comments to read.
     * @param sort - order in which to sort the comments.
     * @return current object of Post, this
     */
    public PostWrapper configComments( int replyDepth, int totalCountLimit, CommentSort sort ) {
        commentsReq = new CommentsRequest( null, null, replyDepth, totalCountLimit, sort.value() );
        return this;
    }

    /**
     *
     * @return true if the post is pinned to the top of subreddit by the admin.
     */
    public boolean isPinned() {
        return submission.isStickied();
    }

    /**
     *
     * @return get sorting of comments.
     */
    public CommentSort getCommentSort() {
        return CommentSort.convert( submission.getSuggestedSort() );
    }

    /**
     *
     * @return get the account responsible for making this post
     */
    public AccountWrapper getAuthor() {
        return acc;
    }

    /**
     *
     * @return Title of the submission
     */
    public String getTitle() {
        return submission.getTitle();
    }

    /**
     *
     * @return An absolute URL to the comments for a self post, otherwise an absolute URL to the Submission content
     */
    public String getUrl() {
        return submission.getUrl();
    }

    /**
     *
     * @return URL relative to reddit.com to access this Submission from a web browser
     */
    public String getPostUrl() {
        return submission.getPermalink();
    }

    /**
     *
     * @return If this Submission contains adult content
     */
    public boolean isNsfw() {
        return submission.isNsfw();
    }

    /**
     *
     * @return date this post was created
     */
    public Date getCreated() {
        return submission.getCreated();
    }

    /**
     *
     * @return Upvotes minus downvotes
     */
    public int getVoteCount() {
        return submission.getScore();
    }

    /**
     *
     * @return The subreddit where this submission was posted to
     */
    public SubredditWrapper getSubreddit() {
        return sub;
    }

    /**
     *
     * @return The number of comments posted in this post. Includes removed comments
     */
    public int getCommentCount() {
        return submission.getCommentCount();
    }

    /**
     *
     * @return The number of reports this post has received, or null if not one of the subreddit's moderators
     */
    public int getReports() {
        return submission.getReports();
    }
}
