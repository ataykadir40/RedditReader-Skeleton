package logic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Shariar (Shawn) Emami
 */
public abstract class LogicFactory {

    private static final String PACKAGE = "logic.";
    private static final String SUFFIX = "Logic";
    private LogicFactory() {
    }
    
    public static <T> T getFor(String entityName) {
        try {
            //getFor(type : Class<T> = (Class< T>) Class.forName(PACKAGE + entityName + SUFFIX)) : T
            Class<?> getClass = Class.forName(PACKAGE + entityName + SUFFIX);
            return (T) getFor(getClass);
            //return getFor((Class<T>) Class.forName(PACKAGE + entityName + SUFFIX));
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static <E> E getFor(Class<E> type) {
        try {
            //3.1: getDeclaredConstructor()
            Constructor<E> construct = type.getDeclaredConstructor();
            E resultInstance = construct.newInstance();
            return resultInstance;
            //3.2: declaredConstructor : Constructor<T>
        } catch (NoSuchMethodException | SecurityException | InstantiationException
                | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {

            throw new IllegalArgumentException(ex);
        }
    }
}

