package helma.scripting.rhino;

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

/**
 * Serialization proxy for app, req, res, path objects.
 */
class ScriptBeanProxy implements SerializationProxy {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1002489933060844917L;
	String name;

	ScriptBeanProxy(String name) {
		this.name = name;
	}

	/**
	 * Lookup the actual object in the current scope
	 * 
	 * @return the object represented by this proxy
	 */
	public Object getObject(RhinoEngine engine) {
		return engine.global.get(name, engine.global);
	}

}