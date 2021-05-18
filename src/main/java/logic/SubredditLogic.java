package logic;

import common.ValidationException;
import dal.SubredditDAL;
import entity.Subreddit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 *
 * @author Wilker Fernandes de Sousa
 * @version 1.0
 *
 */
public class SubredditLogic extends GenericLogic<Subreddit, SubredditDAL> {

    /**
     * create static final variables with proper name of each column. this way
     * you will never manually type it again, instead always refer to these
     * variables.
     *
     * by using the same name as column id and HTML element names we can make
     * our code simpler. this is not recommended for proper production project.
     */
    public static final String SUBSCRIBERS = "subscribers";
    public static final String NAME = "name";
    public static final String URL = "url";
    public static final String ID = "id";

    SubredditLogic() {
        super(new SubredditDAL());
    }

    @Override
    public List<Subreddit> getAll() {
        return get(() -> dal().findAll());
    }

    @Override
    public Subreddit getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    public Subreddit getSubredditWithName(String name) {
        return get(() -> dal().findByName(name));
    }

    public Subreddit getSubredditWithUrl(String url) {
        return get(() -> dal().findByUrl(url));
    }

    public List<Subreddit> getSubredditsWithSubscribers(int subscribers) {
        return get(() -> dal().findBySubscribers(subscribers));
    }

    @Override
    public Subreddit createEntity(Map<String, String[]> parameterMap) {
        //do not create any logic classes in this method.

        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        //same as if condition below
//        if (parameterMap == null) {
//            throw new NullPointerException("parameterMap cannot be null");
//        }

        //create a new Entity object
        Subreddit entity = new Subreddit();

        //ID is generated, so if it exists add it to the entity object
        //otherwise it does not matter as mysql will create an if for it.
        //the only time that we will have id is for update behaviour.
        if (parameterMap.containsKey(ID)) {
            try {
                entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }

        //before using the values in the map, make sure to do error checking.
        //simple lambda to validate a string, this can also be place in another
        //method to be shared amoung all logic classes.
        ObjIntConsumer< String> validator = (value, length) -> {
            if (value == null || value.trim().isEmpty() || value.length() > length) {
                String error = "";
                if (value == null || value.trim().isEmpty()) {
                    error = "value cannot be null or empty: " + value;
                }
                if (value.length() > length) {
                    error = "string length is " + value.length() + " > " + length;
                }
                throw new ValidationException(error);
            }
        };

        //extract the date from map first.
        //everything in the parameterMap is string so it must first be
        //converted to appropriate type. have in mind that values are
        //stored in an array of String; almost always the value is at
        //index zero unless you have used duplicated key/name somewhere.
        String name = parameterMap.get(NAME)[0];
        String url = parameterMap.get(URL)[0];
        int subscribers = Integer.valueOf(parameterMap.get(SUBSCRIBERS)[0]);

        //validate the data
        validator.accept(name, 100);
        validator.accept(url, 255);

        //set values on entity
        entity.setName(name);
        entity.setUrl(url);
        entity.setSubscribers(subscribers);

        return entity;
    }

    /**
     * this method is used to send a list of all names to be used form table
     * column headers. by having all names in one location there is less chance
     * of mistakes.
     *
     * this list must be in the same order as getColumnCodes and
     * extractDataAsList
     *
     * @return list of all column names to be displayed.
     */
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "Name", "Url", "Subscribers");
    }

    /**
     * this method returns a list of column names that match the official column
     * names in the db. by having all names in one location there is less chance
     * of mistakes.
     *
     * this list must be in the same order as getColumnNames and
     * extractDataAsList
     *
     * @return list of all column names in DB.
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, NAME, URL, SUBSCRIBERS);
    }

    /**
     * return the list of values of all columns (variables) in given entity.
     *
     * this list must be in the same order as getColumnNames and getColumnCodes
     *
     * @param e - given Entity to extract data from.
     *
     * @return list of extracted values
     */
    @Override
    public List<?> extractDataAsList(Subreddit e) {
        return Arrays.asList(e.getId(), e.getName(), e.getUrl(), e.getSubscribers());
    }
}
