package reddit.wrapper;

import java.util.Date;
import net.dean.jraw.models.Account;

/**
 * <p>
 * Wrapper class for {@link net.dean.jraw.models.Account} in JRAW. <br>
 * It is only created by {@link reddit.wrapper.Reddit} class but can be used at any scope.</p>
 *
 * @see
 * <a href="https://javadoc.jitpack.io/com/github/mattbdean/JRAW/master-fa1efa3372-1/javadoc/net/dean/jraw/models/Account.html">net.dean.jraw.models.Account
 * JavaDoc</a><br>
 *
 * @author Shariar (Shawn) Emami
 * @version November 2, 2020
 */
public class AccountWrapper {

    private Account account;

    AccountWrapper( Account account ) {
        this.account = account;
    }

    /**
     *
     * @return The amount of Karma this user has acquired through comment
     */
    public int getCommentKarma() {
        return account.getCommentKarma();
    }

    public Date getCreated() {
        return account.getCreated();
    }

    /**
     *
     * @return True if this user has verified ownership of the email address used to create their account. May be null.
     */
    public Boolean getHasVerifiedEmail() {
        return account.getHasVerifiedEmail();
    }

    /**
     *
     * @return The amount of karma gained from submitting links
     */
    public int getLinkKarma() {
        return account.getLinkKarma();
    }

    /**
     *
     * @return The name chosen for this account by a real person
     */
    public String getName() {
        return account.getName();
    }
}
