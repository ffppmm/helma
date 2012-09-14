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

import helma.objectmodel.INode;
import helma.objectmodel.db.NodeHandle;
import helma.objectmodel.db.PersistentNode;

import org.mozilla.javascript.Context;

/**
 * Serialization proxy for various flavors of HopObjects/Nodes
 */
class HopObjectProxy implements SerializationProxy {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4808579296683836009L;
	Object ref;
	boolean wrapped = false;

	HopObjectProxy(HopObject obj) {
		INode n = obj.getNode();
		if (n == null) {
			ref = obj.getClassName();
		} else {
			if (n instanceof PersistentNode) {
				ref = ((PersistentNode) n).getHandle();
			} else {
				ref = n;
			}
		}
		wrapped = true;
	}

	HopObjectProxy(PersistentNode node) {
		ref = node.getHandle();
	}

	/**
	 * Lookup the actual object in the current scope
	 * 
	 * @return the object represented by this proxy
	 */
	public Object getObject(RhinoEngine engine) {
		if (ref instanceof String)
			return engine.core.getPrototype((String) ref);
		else if (ref instanceof NodeHandle) {
			Object n = ((NodeHandle) ref).getNode(engine.app
					.getWrappedNodeManager());
			return wrapped ? Context.toObject(n, engine.global) : n;
		}
		return Context.toObject(ref, engine.global);
	}

}
