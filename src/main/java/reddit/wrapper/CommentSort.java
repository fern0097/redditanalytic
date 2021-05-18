package reddit.wrapper;

/**
 * <p>A wrapper enum for {@link net.dean.jraw.models.CommentSort} in JRAW.</p>
 *
 * @see <a href="https://javadoc.jitpack.io/com/github/mattbdean/JRAW/master-fa1efa3372-1/javadoc/net/dean/jraw/models/CommentSort.html">net.dean.jraw.models.CommentSort JavaDoc</a><br>
 * 
 * @author Shariar (Shawn) Emami
 * @version November 2, 2020
 */
public enum CommentSort {
    CONTROVERSIAL(net.dean.jraw.models.CommentSort.CONTROVERSIAL),
    CONFIDENCE(net.dean.jraw.models.CommentSort.CONFIDENCE),
    NEW(net.dean.jraw.models.CommentSort.NEW),
    OLD(net.dean.jraw.models.CommentSort.OLD),
    RANDOM(net.dean.jraw.models.CommentSort.RANDOM),
    TOP(net.dean.jraw.models.CommentSort.TOP),
    QA(net.dean.jraw.models.CommentSort.QA),
    LIVE(net.dean.jraw.models.CommentSort.LIVE);

    private final net.dean.jraw.models.CommentSort value;

    CommentSort(net.dean.jraw.models.CommentSort value) {
        this.value = value;
    }

    public net.dean.jraw.models.CommentSort value() {
        return value;
    }
    
    static CommentSort convert( net.dean.jraw.models.CommentSort sort){
        switch(sort){
            case CONFIDENCE:
                return CONFIDENCE;
            case TOP:
                return TOP;
            case NEW:
                return NEW;
            case CONTROVERSIAL:
                return CONTROVERSIAL;
            case OLD:
                return OLD;
            case RANDOM:
                return RANDOM;
            case QA:
                return QA;
            case LIVE:
                return LIVE;
            default:
                throw new IllegalStateException(sort.name());
        }
    }
}
