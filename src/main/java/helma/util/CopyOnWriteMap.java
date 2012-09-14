package helma.util;

/*
 * #%L
 * HelmaObjectPublisher
 * %%
 * Copyright (C) 1998 - 2012 Helma Software
 * %%
 * Helma License Notice
 * 
 * The contents of this file are subject to the Helma License
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://adele.helma.org/download/helma/license.txt
 * #L%
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A Map that wraps another map and creates a new copy of the wrapped map if we
 * try to modify it. This class is wrapped as a native scripted object in
 * JavaScript rather than exposing them through Java reflection.
 * 
 * All methods in this class are synchronized in order not to miss the switch
 * between original and copied map.
 */
public class CopyOnWriteMap extends WrappedMap {

	boolean modified = false;

	/**
	 * Constructor
	 */
	public CopyOnWriteMap(Map<String, Object> map) {
		super(map);
	}

	public synchronized boolean wasModified() {
		return modified;
	}

	public synchronized int size() {
		return wrapped.size();
	}

	public synchronized boolean isEmpty() {
		return wrapped.isEmpty();
	}

	public synchronized boolean containsKey(Object key) {
		return wrapped.containsKey(key);
	}

	public synchronized boolean containsValue(Object value) {
		return wrapped.containsValue(value);
	}

	public synchronized Object get(Object key) {
		return wrapped.get(key);
	}

	// Modification Operations - check for readonly

	public synchronized Object put(String key, Object value) {
		if (!modified) {
			wrapped = new HashMap<String, Object>(wrapped);
			modified = true;
		}
		return wrapped.put(key, value);
	}

	public synchronized Object remove(String key) {
		if (!modified) {
			wrapped = new HashMap<String, Object>(wrapped);
			modified = true;
		}
		return wrapped.remove(key);
	}

	public synchronized void putAll(Map<? extends String, ? extends Object> t) {
		if (!modified) {
			wrapped = new HashMap<String, Object>(wrapped);
			modified = true;
		}
		wrapped.putAll(t);
	}

	public synchronized void clear() {
		if (!modified) {
			wrapped = new HashMap<String, Object>(wrapped);
			modified = true;
		}
		wrapped.clear();
	}

	// Views

	public synchronized Set<String> keySet() {
		return wrapped.keySet();
	}

	public synchronized Collection<Object> values() {
		return wrapped.values();
	}

	public synchronized Set<Entry<String, Object>> entrySet() {
		return wrapped.entrySet();
	}

	// Comparison and hashing

	public synchronized boolean equals(Object o) {
		return wrapped.equals(o);
	}

	public synchronized int hashCode() {
		return wrapped.hashCode();
	}

	// toString

	public synchronized String toString() {
		return wrapped.toString();
	}

}
