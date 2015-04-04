package com.halboom.pgt.pgutil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/13/13
 * Time: 2:47 PM
 * Helper methods for dealing with primitives.
 */
public final class Primitives {
    /**
     * Prevent instantiation.
     */
    private Primitives() {
    }

    /**
     * Map for primitive types to wrappers.
     */
    private static final Map<String, Class> PRIMITIVE_TYPES = createPrimitiveMap();

    /**
     * Create a primitive to wrapper type map.
     * @return an immutable primitive to wrapper type map.
     */
    private static Map<String, Class> createPrimitiveMap() {
        Map<String, Class> types = new HashMap<String, Class>();
        types.put("int", Integer.TYPE);
        types.put("long", Long.TYPE);
        types.put("double", Double.TYPE);
        types.put("float", Float.TYPE);
        types.put("bool", Boolean.TYPE);
        types.put("char", Character.TYPE);
        types.put("byte", Byte.TYPE);
        types.put("void", Void.TYPE);
        types.put("short", Short.TYPE);
        return Collections.unmodifiableMap(types);
    }

    /**
     * Checks if a wrapper is of the primitive type.
     * @param primitive the primitive class.
     * @param wrapper the wrapper class.
     * @return true if the wrapper is of the primitive type.
     */
    public static boolean isPrimitiveType(Class primitive, Class wrapper) {
        Class type = PRIMITIVE_TYPES.get(primitive.getName());
        if (type == null) {
            return false;
        }
        return type == wrapper;
    }
}
