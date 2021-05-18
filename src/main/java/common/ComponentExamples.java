package common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Shariar (Shawn) Emami
 */
public class ComponentExamples {

    public static void main( String[] args ) throws ParseException {
        regexExample();
        dateConversionExample();
    }

    public static void dateConversionExample() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" );

        Date now = Date.from( Instant.now( Clock.systemDefaultZone() ) );
        String strNow = formatter.format( now );
        System.out.println( strNow );

        Date now2 = formatter.parse( strNow );
        System.out.println( now2 );
    }

    public static void regexExample() {
        String text = "/BoardTableJSP";

        String regex = "(/)?(Account|Board|Host|Image){1}(TableJSP)?";
        Pattern pattern = Pattern.compile( regex );

        Matcher matcher = pattern.matcher( text );
        if( matcher.find() ){
            System.out.println( matcher.group( 2 ) );
        }
    }
}
