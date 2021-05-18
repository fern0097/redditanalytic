package reddit.wrapper;

import java.util.Date;
import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.tree.CommentNode;

/**
 * <p>
 * Wrapper class for {@link net.dean.jraw.tree.CommentNode} in JRAW.<br>
 * It is only created by {@link reddit.wrapper.PostWrapper} class but can be used at any scope.</p>
 *
 * @see
 * <a href="https://javadoc.jitpack.io/com/github/mattbdean/JRAW/master-fa1efa3372-1/javadoc/net/dean/jraw/tree/CommentNode.html">net.dean.jraw.tree.CommentNode
 * JavaDoc</a><br>
 * @see
 * <a href="https://javadoc.jitpack.io/com/github/mattbdean/JRAW/master-fa1efa3372-1/javadoc/net/dean/jraw/models/PublicContribution.html">net.dean.jraw.models.PublicContribution
 * JavaDoc</a><br>
 *
 * @author Shariar (Shawn) Emami
 * @version November 2, 2020
 */
public class CommentWrapper {

    private CommentNode<PublicContribution<?>> comment;
    private AccountWrapper authorObj;
    private PostWrapper postObj;

    CommentWrapper( AccountWrapper author, PostWrapper post, CommentNode<PublicContribution<?>> comment ) {
        this.authorObj = author;
        this.postObj = post;
        this.comment = comment;
    }

    public String getUniqueID() {
        return comment.getSubject().getId();
    }

    public PostWrapper getPost() {
        return postObj;
    }

    public String getText() {
        return comment.getSubject().getBody();
    }

    public AccountWrapper getAuthor() {
        return authorObj;
    }

    public int getReplyCount() {
        return comment.totalSize();
    }

    public int getVotes() {
        return comment.getSubject().getScore();
    }

    public boolean isParrent() {
        return getDepth() == 1;
    }

    public Date getCreated() {
        return comment.getSubject().getCreated();
    }

    public boolean isPinned() {
        return comment.getSubject().isStickied();
    }

    public int getDepth() {
        return comment.getDepth();
    }
}
