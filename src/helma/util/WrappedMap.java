/*
 * Helma License Notice
 *
 * The contents of this file are subject to the Helma License
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://adele.helma.org/download/helma/license.txt
 *
 * Copyright 1998-2003 Helma Software. All Rights Reserved.
 *
 * $RCSfile$
 * $Author$
 * $Revision$
 * $Date$
 */

package helma.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *  A Map that wraps another map. We use this class to be able to
 *  wrap maps as native objects within a scripting engine rather
 *  than exposing them through Java reflection.
 *  Additionally, instances of this class can be set to readonly
 *  so that the original map can't be modified.
 */
// FIXME: Typesafety
public class WrappedMap implements Map<Object, Object> {

    // the wrapped map
    protected Map<Object, Object> wrapped = null;

    // is this map readonly?
    protected boolean readonly = false;

    /**
     *  Constructor
     */
    public WrappedMap(Map<Object, Object> map) {
        this(map, false);
    }

    /**
     *  Constructor
     */
    public WrappedMap(Map<Object, Object> map, boolean readonly) {
        if (map == null) {
            throw new NullPointerException(
                "null Map passed to WrappedMap constructor");
        }
        wrapped = map;
        this.readonly = readonly;
    }

    public WrappedMap() {
    	this.readonly = true;
	}

	/**
     *  Set the readonly flag on or off
     */
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    /**
     *  Is this map readonly?
     */
    public boolean isReadonly() {
        return readonly;
    }

    // Methods from interface java.util.Map -
    // these are just proxies to the wrapped map, except for
    // readonly checks on modifiers.

    public int size() {
        return wrapped.size();
    }

    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    public boolean containsKey(Object key) {
        return wrapped.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return wrapped.containsValue(value);
    }

    public Object get(Object key) {
        return wrapped.get(key);
    }

    // Modification Operations - check for readonly

    public Object put(Object key, Object value) {
        if (readonly) {
            throw new RuntimeException("Attempt to modify readonly map");
        }
        return wrapped.put(key, value);
    }

    public Object remove(Object key) {
        if (readonly) {
            throw new RuntimeException("Attempt to modify readonly map");
        }
        return wrapped.remove(key);
    }

    public void putAll(Map<?, ?> t) {
        if (readonly) {
            throw new RuntimeException("Attempt to modify readonly map");
        }
        wrapped.putAll(t);
    }

    public void clear() {
        if (readonly) {
            throw new RuntimeException("Attempt to modify readonly map");
        }
        wrapped.clear();
    }


    // Views

    public Set<Object> keySet() {
        return wrapped.keySet();
    }

    public Collection<Object> values() {
        return wrapped.values();
    }

    public Set<java.util.Map.Entry<Object, Object>> entrySet() {
        return wrapped.entrySet();
    }


    // Comparison and hashing

    public boolean equals(Object o) {
        return wrapped.equals(o);
    }

    public int hashCode() {
        return wrapped.hashCode();
    }

    // toString

    public String toString() {
        return wrapped.toString();
    }

}
