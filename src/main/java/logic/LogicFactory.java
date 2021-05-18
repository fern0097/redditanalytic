package logic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Logic Factory
 *
 * @author Rodrigo Tavares
 * @version November 23, 2020
 */
public abstract class LogicFactory {

    private static final String PACKAGE = "logic.";
    private static final String SUFFIX = "Logic";

    private LogicFactory() {
    }

    public static <T> T getFor(String entityName) {
        try {
            T newInstance = getFor((Class< T>) Class.forName(PACKAGE + entityName + SUFFIX));
            return newInstance;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T getFor(Class<T> type) {
        try {

            Constructor<T> declaredConstructor = type.getDeclaredConstructor();
            T newInstance = declaredConstructor.newInstance();
            return newInstance;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException(e);
        }
    }
}