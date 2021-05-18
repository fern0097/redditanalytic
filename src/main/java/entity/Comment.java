package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
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
@Table( name = "comment", catalog = "redditanalytic", schema = "" )
@NamedQueries( {
    @NamedQuery( name = "Comment.findAll", query = "SELECT c FROM Comment c" ),
    @NamedQuery( name = "Comment.findById", query = "SELECT c FROM Comment c WHERE c.id = :id" ),
    @NamedQuery( name = "Comment.findByText", query = "SELECT c FROM Comment c WHERE c.text LIKE CONCAT('%', :text, '%')" ),
    @NamedQuery( name = "Comment.findByCreated", query = "SELECT c FROM Comment c WHERE c.created = :created" ),
    @NamedQuery( name = "Comment.findByPoints", query = "SELECT c FROM Comment c WHERE c.points = :points" ),
    @NamedQuery( name = "Comment.findByReplys", query = "SELECT c FROM Comment c WHERE c.replys = :replys" ),
    @NamedQuery( name = "Comment.findByUniqueId", query = "SELECT c FROM Comment c WHERE c.uniqueId = :uniqueId" ),
    @NamedQuery( name = "Comment.findByIsReply", query = "SELECT c FROM Comment c WHERE c.isReply = :isReply" ) } )
public class Comment implements Serializable {

    @Basic( optional = false )
    @NotNull
    @Size( min = 1, max = 10 )
    @Column( name = "unique_id" )
    private String uniqueId;
    @Basic( optional = false )
    @NotNull
    @Size( min = 1, max = 1000 )
    @Column( name = "text" )
    private String text;
    @Basic( optional = false )
    @NotNull
    @Column( name = "created" )
    @Temporal( TemporalType.TIMESTAMP )
    private Date created;
    @Basic( optional = false )
    @NotNull
    @Column( name = "points" )
    private int points;
    @Basic( optional = false )
    @NotNull
    @Column( name = "replys" )
    private int replys;
    @Basic( optional = false )
    @NotNull
    @Column( name = "is_reply" )
    private boolean isReply;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Basic( optional = false )
    @Column( name = "id" )
    private Integer id;
    @JoinColumn( name = "post_id", referencedColumnName = "id" )
    @ManyToOne( optional = false, fetch = FetchType.LAZY )
    private Post postId;
    @JoinColumn( name = "reddit_account_id", referencedColumnName = "id" )
    @ManyToOne( optional = false, fetch = FetchType.LAZY )
    private RedditAccount redditAccountId;

    public Comment() {
    }

    public Comment( Integer id ) {
        this.id = id;
    }

    public Comment( Integer id, String text, Date created, int points, int replys, boolean isReply ) {
        this.id = id;
        this.text = text;
        this.created = created;
        this.points = points;
        this.replys = replys;
        this.isReply = isReply;
    }

    public Integer getId() {
        return id;
    }

    public void setId( Integer id ) {
        this.id = id;
    }

    public Post getPostId() {
        return postId;
    }

    public void setPostId( Post postId ) {
        this.postId = postId;
    }

    public RedditAccount getRedditAccountId() {
        return redditAccountId;
    }

    public void setRedditAccountId( RedditAccount redditAccountId ) {
        this.redditAccountId = redditAccountId;
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
        if( !( object instanceof Comment ) ){
            return false;
        }
        Comment other = (Comment)object;
        if( ( this.id == null && other.id != null ) || ( this.id != null && !this.id.equals( other.id ) ) ){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Comment[ id=" + id + " ]";
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId( String uniqueId ) {
        this.uniqueId = uniqueId;
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated( Date created ) {
        this.created = created;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints( int points ) {
        this.points = points;
    }

    public int getReplys() {
        return replys;
    }

    public void setReplys( int replys ) {
        this.replys = replys;
    }

    public boolean getIsReply() {
        return isReply;
    }

    public void setIsReply( boolean isReply ) {
        this.isReply = isReply;
    }

}
