package entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Shariar
 */
@Entity
@Table( name = "post", catalog = "redditanalytic", schema = "" )
@NamedQueries( {
    @NamedQuery( name = "Post.findAll", query = "SELECT p FROM Post p" ),
    @NamedQuery( name = "Post.findById", query = "SELECT p FROM Post p WHERE p.id = :id" ),
    @NamedQuery( name = "Post.findByPoints", query = "SELECT p FROM Post p WHERE p.points = :points" ),
    @NamedQuery( name = "Post.findByCommentCount", query = "SELECT p FROM Post p WHERE p.commentCount = :commentCount" ),
    @NamedQuery( name = "Post.findByTitle", query = "SELECT p FROM Post p WHERE p.title = :title" ),
    @NamedQuery( name = "Post.findByAuthor", query = "SELECT p FROM Post p WHERE p.redditAccountId.id = :id" ),
    @NamedQuery( name = "Post.findByUniqueId", query = "SELECT p FROM Post p WHERE p.uniqueId = :uniqueId" ),
    @NamedQuery( name = "Post.findByCreated", query = "SELECT p FROM Post p WHERE p.created = :created" ) } )
public class Post implements Serializable {

    @Basic( optional = false )
    @NotNull
    @Size( min = 1, max = 10 )
    @Column( name = "unique_id" )
    private String uniqueId;
    @Basic( optional = false )
    @NotNull
    @Column( name = "points" )
    private int points;
    @Basic( optional = false )
    @NotNull
    @Column( name = "comment_count" )
    private int commentCount;
    @Basic( optional = false )
    @NotNull
    @Size( min = 1, max = 255 )
    @Column( name = "title" )
    private String title;
    @Basic( optional = false )
    @NotNull
    @Column( name = "created" )
    @Temporal( TemporalType.TIMESTAMP )
    private Date created;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Basic( optional = false )
    @Column( name = "id" )
    private Integer id;
    @JoinColumn( name = "reddit_account_id", referencedColumnName = "id" )
    @ManyToOne( optional = false, fetch = FetchType.LAZY )
    private RedditAccount redditAccountId;
    @JoinColumn( name = "subreddit_id", referencedColumnName = "id" )
    @ManyToOne( optional = false, fetch = FetchType.LAZY )
    private Subreddit subredditId;
    @OneToMany( cascade = CascadeType.ALL, mappedBy = "postId", fetch = FetchType.LAZY )
    private List<Comment> commentList;

    public Post() {
    }

    public Post( Integer id ) {
        this.id = id;
    }

    public Post( Integer id, int points, int commentCount, String title, Date created ) {
        this.id = id;
        this.points = points;
        this.commentCount = commentCount;
        this.title = title;
        this.created = created;
    }

    public Integer getId() {
        return id;
    }

    public void setId( Integer id ) {
        this.id = id;
    }

    public RedditAccount getRedditAccountId() {
        return redditAccountId;
    }

    public void setRedditAccountId( RedditAccount redditAccountId ) {
        this.redditAccountId = redditAccountId;
    }

    public Subreddit getSubredditId() {
        return subredditId;
    }

    public void setSubredditId( Subreddit subredditId ) {
        this.subredditId = subredditId;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList( List<Comment> commentList ) {
        this.commentList = commentList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += ( id != null ? id.hashCode() : 0 );
        return hash;
    }

    @Override
    public boolean equals( Object object ) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if( !( object instanceof Post ) ){
            return false;
        }
        Post other = (Post)object;
        if( ( this.id == null && other.id != null ) || ( this.id != null && !this.id.equals( other.id ) ) ){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Post[ id=" + id + " ]";
    }

    public String getUniqueID() {
        return uniqueId;
    }

    public void setUniqueId( String uniqueId ) {
        this.uniqueId = uniqueId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints( int points ) {
        this.points = points;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount( int commentCount ) {
        this.commentCount = commentCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated( Date created ) {
        this.created = created;
    }

}
