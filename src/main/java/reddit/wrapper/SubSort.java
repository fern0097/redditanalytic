package reddit.wrapper;

import net.dean.jraw.models.SubredditSort;

/**
 * <p>A wrapper enum for {@link net.dean.jraw.models.SubredditSort} in JRAW.</p>
 *
 * @see <a href="https://javadoc.jitpack.io/com/github/mattbdean/JRAW/master-fa1efa3372-1/javadoc/net/dean/jraw/models/SubredditSort.html">link net.dean.jraw.models.SubredditSort JavaDoc</a><br>
 * 
 * @author Shariar (Shawn) Emami
 * @version November 2, 2020
 */
public enum SubSort {
    HOT(SubredditSort.HOT),
    BEST(SubredditSort.BEST),
    NEW(SubredditSort.NEW),
    RISING(SubredditSort.RISING),
    CONTROVERSIAL(SubredditSort.CONTROVERSIAL),
    TOP(SubredditSort.TOP);

    private final SubredditSort value;

    SubSort(SubredditSort value) {
        this.value = value;
    }

    public SubredditSort value() {
        return value;
    }
    
    static SubSort convert( SubredditSort sort){
        switch(sort){
            case HOT:
                return HOT;
            case BEST:
                return BEST;
            case NEW:
                return NEW;
            case RISING:
                return RISING;
            case CONTROVERSIAL:
                return CONTROVERSIAL;
            case TOP:
                return TOP;
            default:
                throw new IllegalStateException(sort.name());
        }
    }
}
