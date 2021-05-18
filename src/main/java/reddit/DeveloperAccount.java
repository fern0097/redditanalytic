package reddit;

import java.util.Objects;

/**
 * this class is used to store users Reddit API credential. This information can be found on users Reddit account.
 * 
 * <pre>
   DeveloperAccount dev = new DeveloperAccount()
                .setClientID(clientID)
                .setClientSecret(clientSecret)
                .setRedditUser(redditUser)
                .setAlgonquinUser(algonquinUser);
 * </pre>
 * 
 * @author Shariar (Shawn) Emami
 * @version November 2, 2020
 */
public class DeveloperAccount {

    private static final String APPID = "com.algonquin.cst8288.s20.yz5HkdPl-ZkVbg";

    private String clientID;
    private String clientSecret;
    private String redditUser;
    private String algonquinUser;

    public void isComplete(){
        Objects.requireNonNull(clientID, "Value of \"clientID\" in DeveloperAccount object must be set using the setClientID(String)");
        Objects.requireNonNull(clientSecret, "Value of \"clientSecret\" in DeveloperAccount object must be set using the setClientSecret(String)");
        Objects.requireNonNull(redditUser, "Value of \"redditUser\" in DeveloperAccount object must be set using the setRedditUser(String)");
        Objects.requireNonNull(algonquinUser, "Value of \"algonquinUser\" in DeveloperAccount object must be set using the setAlgonquinUser(String)");
    }
    
    public String getClientID() {
        return clientID;
    }

    public DeveloperAccount setClientID(String clientID) {
        this.clientID = clientID;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public DeveloperAccount setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public String getRedditUser() {
        return redditUser;
    }

    public DeveloperAccount setRedditUser(String redditUser) {
        this.redditUser = redditUser;
        return this;
    }

    public String getAlgonquinUser() {
        return algonquinUser;
    }

    public DeveloperAccount setAlgonquinUser(String algonquinUser) {
        this.algonquinUser = algonquinUser;
        return this;
    }

    public String getAppID() {
        return APPID.concat(algonquinUser);
    }
}
