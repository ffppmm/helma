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
import java.util.Map;
import java.util.Set;

/**
 * A Map that wraps another map. We use this class to be able to wrap maps as
 * native objects within a scripting engine rather than exposing them through
 * Java reflection. Additionally, instances of this class can be set to readonly
 * so that the original map can't be modified.
 */
// FIXME: Typesafety
public class WrappedCronJobMap implements Map<String, CronJob> {

	// the wrapped map
	protected Map<String, CronJob> wrapped = null;

	// is this map readonly?
	protected boolean readonly = false;

	/**
	 * Constructor
	 */
	public WrappedCronJobMap(Map<String, CronJob> map) {
		this(map, false);
	}

	/**
	 * Constructor
	 */
	public WrappedCronJobMap(Map<String, CronJob> map, boolean readonly) {
		if (map == null) {
			throw new NullPointerException(
					"null Map passed to WrappedMap constructor");
		}
		wrapped = map;
		this.readonly = readonly;
	}

	public WrappedCronJobMap() {
		this.readonly = true;
	}

	/**
	 * Set the readonly flag on or off
	 */
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	/**
	 * Is this map readonly?
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

	public CronJob get(Object key) {
		return wrapped.get(key);
	}

	// Modification Operations - check for readonly

	public CronJob put(String key, CronJob value) {
		if (readonly) {
			throw new RuntimeException("Attempt to modify readonly map");
		}
		return wrapped.put(key, value);
	}

	public CronJob remove(Object key) {
		if (readonly) {
			throw new RuntimeException("Attempt to modify readonly map");
		}
		return wrapped.remove(key);
	}

	public void putAll(Map<? extends String, ? extends CronJob> t) {
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

	public Set<String> keySet() {
		return wrapped.keySet();
	}

	public Collection<CronJob> values() {
		return wrapped.values();
	}

	public Set<Map.Entry<String, CronJob>> entrySet() {
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
