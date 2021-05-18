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
@Table( name = "reddit_account", catalog = "redditanalytic", schema = "" )
@NamedQueries( {
    @NamedQuery( name = "RedditAccount.findAll", query = "SELECT r FROM RedditAccount r" ),
    @NamedQuery( name = "RedditAccount.findById", query = "SELECT r FROM RedditAccount r WHERE r.id = :id" ),
    @NamedQuery( name = "RedditAccount.findByName", query = "SELECT r FROM RedditAccount r WHERE r.name = :name" ),
    @NamedQuery( name = "RedditAccount.findByLinkPoints", query = "SELECT r FROM RedditAccount r WHERE r.linkPoints = :linkPoints" ),
    @NamedQuery( name = "RedditAccount.findByCommentPoints", query = "SELECT r FROM RedditAccount r WHERE r.commentPoints = :commentPoints" ),
    @NamedQuery( name = "RedditAccount.findByCreated", query = "SELECT r FROM RedditAccount r WHERE r.created = :created" ) } )
public class RedditAccount implements Serializable {

    @Basic( optional = false )
    @NotNull
    @Size( min = 1, max = 100 )
    @Column( name = "name" )
    private String name;
    @Basic( optional = false )
    @NotNull
    @Column( name = "link_points" )
    private int linkPoints;
    @Basic( optional = false )
    @NotNull
    @Column( name = "comment_points" )
    private int commentPoints;
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
    @OneToMany( cascade = CascadeType.ALL, mappedBy = "redditAccountId", fetch = FetchType.LAZY )
    private List<Post> postList;
    @OneToMany( cascade = CascadeType.ALL, mappedBy = "redditAccountId", fetch = FetchType.LAZY )
    private List<Comment> commentList;

    public RedditAccount() {
    }

    public RedditAccount( Integer id ) {
        this.id = id;
    }

    public RedditAccount( Integer id, String name, int linkPoints, int commentPoints, Date created ) {
        this.id = id;
        this.name = name;
        this.linkPoints = linkPoints;
        this.commentPoints = commentPoints;
        this.created = created;
    }

    public Integer getId() {
        return id;
    }

    public void setId( Integer id ) {
        this.id = id;
    }

    public int getLinkPoints() {
        return linkPoints;
    }

    public void setLinkPoints( int linkPoints ) {
        this.linkPoints = linkPoints;
    }

    public int getCommentPoints() {
        return commentPoints;
    }

    public void setCommentPoints( int commentPoints ) {
        this.commentPoints = commentPoints;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList( List<Post> postList ) {
        this.postList = postList;
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
        if( !( object instanceof RedditAccount ) ){
            return false;
        }
        RedditAccount other = (RedditAccount)object;
        if( ( this.id == null && other.id != null ) || ( this.id != null && !this.id.equals( other.id ) ) ){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RedditAccount[ id=" + id + " ]";
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated( Date created ) {
        this.created = created;
    }

}
