package entity;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author  Shariar
 */
@Entity
@Table( name = "subreddit", catalog = "redditanalytic", schema = "" )
@NamedQueries( {
    @NamedQuery( name = "Subreddit.findAll", query = "SELECT s FROM Subreddit s" ),
    @NamedQuery( name = "Subreddit.findById", query = "SELECT s FROM Subreddit s WHERE s.id = :id" ),
    @NamedQuery( name = "Subreddit.findByName", query = "SELECT s FROM Subreddit s WHERE s.name = :name" ),
    @NamedQuery( name = "Subreddit.findByUrl", query = "SELECT s FROM Subreddit s WHERE s.url = :url" ),
    @NamedQuery( name = "Subreddit.findBySubscribers", query = "SELECT s FROM Subreddit s WHERE s.subscribers = :subscribers" ) } )
public class Subreddit implements Serializable {

    @Basic( optional = false )
    @NotNull
    @Size( min = 1, max = 100 )
    @Column( name = "name" )
    private String name;
    @Basic( optional = false )
    @NotNull
    @Size( min = 1, max = 255 )
    @Column( name = "url" )
    private String url;
    @Basic( optional = false )
    @NotNull
    @Column( name = "subscribers" )
    private int subscribers;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Basic( optional = false )
    @Column( name = "id" )
    private Integer id;
    @OneToMany( cascade = CascadeType.ALL, mappedBy = "subredditId", fetch = FetchType.LAZY )
    private List<Post> postList;

    public Subreddit() {
    }

    public Subreddit( Integer id ) {
        this.id = id;
    }

    public Subreddit( Integer id, String name, String url, int subscribers ) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.subscribers = subscribers;
    }

    public Integer getId() {
        return id;
    }

    public void setId( Integer id ) {
        this.id = id;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList( List<Post> postList ) {
        this.postList = postList;
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
        if( !( object instanceof Subreddit ) ){
            return false;
        }
        Subreddit other = (Subreddit)object;
        if( ( this.id == null && other.id != null ) || ( this.id != null && !this.id.equals( other.id ) ) ){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Subreddit[ id=" + id + " ]";
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public void setSubscribers( int subscribers ) {
        this.subscribers = subscribers;
    }

}
