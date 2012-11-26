package helma.orm;

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

import helma.framework.IPathElement;
import helma.framework.core.Application;
import helma.orm.db.AbstractNode;
import helma.orm.db.DbMapping;
import helma.orm.db.Key;
import helma.orm.db.NodeHandle;
import helma.orm.db.PersistentNode;
import helma.orm.db.Relation;
import helma.orm.db.WrappedNodeManager;

import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * A transient implementation of INode. An instance of this class can't be made
 * persistent by reachability from a persistent node. To make a
 * persistent-capable object, class helma.objectmodel.db.Node has to be used.
 */
public class TransientNode extends AbstractNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6761318174723519138L;
	private static long idgen = 0;
	protected Hashtable<String, IProperty> propMap;
	protected Hashtable<String, INode> nodeMap;
	protected Vector<INode> nodes;
	protected INode parent;
	transient String prototype;
	protected String id;
	protected String name;
	private final Application app;

	transient DbMapping dbmap;
	INode cacheNode;

	/**
	 * Creates a new TransientNode object.
	 */
	public TransientNode(Application app) {
		id = generateID();
		name = id;
		created = lastmodified = System.currentTimeMillis();
		this.app = app;
	}

	/**
	 * Make a new TransientNode object with a given name
	 */
	public TransientNode(Application app, String n) {
		id = generateID();
		name = (n == null || n.length() == 0) ? id : n;
		// HACK - decrease creation and last-modified timestamp by 1 so we
		// notice
		// modifications that take place immediately after object creation
		created = lastmodified = System.currentTimeMillis() - 1;
		this.app = app;
	}

	public static String generateID() {
		// make transient ids differ from persistent ones
		// and are unique within on runtime session
		return "t" + idgen++;
	}

	public void setDbMapping(DbMapping dbmap) {
		this.dbmap = dbmap;
	}

	public DbMapping getDbMapping() {
		return dbmap;
	}

	public String getID() {
		return id;
	}

	public boolean isAnonymous() {
		return anonymous;
	}

	public String getName() {
		return name;
	}

	public String getElementName() {
		return anonymous ? id : name;
	}

	public int getState() {
		return TRANSIENT;
	}

	public void setState(int s) {
		// state always is TRANSIENT on this kind of node
	}

	public String getPath() {
		return getFullName(null);
	}

	public String getFullName(INode root) {
		String divider = null;
		StringBuffer b = new StringBuffer();
		INode p = this;

		while ((p != null) && (p.getParent() != null) && (p != root)) {
			if (divider != null) {
				b.insert(0, divider);
			} else {
				divider = "/";
			}

			b.insert(0, p.getElementName());
			p = p.getParent();
		}

		return b.toString();
	}

	public void setName(String name) {
		// if (name.indexOf('/') > -1)
		// throw new RuntimeException
		// ("The name of the node must not contain \"/\".");
		if ((name == null) || (name.trim().length() == 0)) {
			this.name = id;
		} else {
			this.name = name;
		}
	}

	public String getPrototype() {
		// if prototype is null, it's a vanilla HopObject.
		if (prototype == null) {
			return "HopObject";
		}

		return prototype;
	}

	public void setPrototype(String proto) {
		this.prototype = proto;
	}

	public INode getParent() {
		return parent;
	}

	public void setSubnodeRelation(String rel) {
		throw new UnsupportedOperationException(
				"Can't set subnode relation for non-persistent Node.");
	}

	public String getSubnodeRelation() {
		return null;
	}

	public int getNumberOfChildNodes() {
		return (nodes == null) ? 0 : nodes.size();
	}

	public INode addNode(INode elem) {
		return addNode(elem, getNumberOfChildNodes());
	}

	public INode addNode(INode elem, int where) {
		if ((where < 0) || (where > getNumberOfChildNodes())) {
			where = getNumberOfChildNodes();
		}

		String n = elem.getName();

		if (n.indexOf('/') > -1) {
			throw new RuntimeException(
					"The name of a node must not contain \"/\" (slash).");
		}

		if ((nodeMap != null) && (nodeMap.get(elem.getID()) != null)) {
			nodes.removeElement(elem);
			where = Math.min(where, getNumberOfChildNodes());
			nodes.insertElementAt(elem, where);

			return elem;
		}

		if (nodeMap == null) {
			nodeMap = new Hashtable<String, INode>();
		}

		if (nodes == null) {
			nodes = new Vector<INode>();
		}

		nodeMap.put(elem.getID(), elem);
		nodes.insertElementAt(elem, where);

		if (elem instanceof TransientNode) {
			TransientNode node = (TransientNode) elem;

			if (node.parent == null) {
				node.parent = this;
				node.anonymous = true;
			}
		}

		lastmodified = System.currentTimeMillis();
		return elem;
	}

	public INode createNode() {
		return createNode(null, 0); // where is ignored since this is an
									// anonymous node
	}

	public INode createNode(int where) {
		return createNode(null, where);
	}

	public INode createNode(String nm) {
		return createNode(nm, getNumberOfChildNodes()); // where is usually
														// ignored (if nm !=
														// null)
	}

	public INode createNode(String nm, int where) {
		boolean anon = false;

		if ((nm == null) || "".equals(nm.trim())) {
			anon = true;
		}

		INode n = new TransientNode(app, nm);

		if (anon) {
			addNode(n, where);
		} else {
			setNode(nm, n);
		}

		return n;
	}

	public IPathElement getParentElement() {
		return getParent();
	}

	public IPathElement getChildElement(String name) {
		return getNode(name);
	}

	public INode getChildNode(String name) {
		StringTokenizer st = new StringTokenizer(name, "/");
		TransientNode retval = this;
		TransientNode runner;

		while (st.hasMoreTokens() && (retval != null)) {
			runner = retval;

			String next = st.nextToken().trim().toLowerCase();

			if ("".equals(next)) {
				retval = this;
			} else {
				retval = (runner.nodeMap == null) ? null
						: (TransientNode) runner.nodeMap.get(next);
			}

			if (retval == null) {
				retval = (TransientNode) runner.getNode(next);
			}
		}

		return retval;
	}

	public INode getSubnodeAt(int index) {
		return (nodes == null) ? null : (INode) nodes.elementAt(index);
	}

	public int contains(INode n) {
		if ((n == null) || (nodes == null)) {
			return -1;
		}

		return nodes.indexOf(n);
	}

	public boolean remove() {
		if (anonymous) {
			parent.unset(name);
		} else {
			parent.removeNode(this);
		}

		return true;
	}

	public void removeNode(INode node) {
		// IServer.getLogger().log ("removing: "+ node);
		releaseNode(node);

		if ((node.getParent() == this) && node.isAnonymous()) {

			// remove all subnodes, giving them a chance to destroy themselves.
			Vector<INode> v = new Vector<INode>(); // removeElement modifies the
													// Vector we are
													// enumerating, so we are
													// extra careful.

			for (Enumeration<INode> e3 = node.getChildNodes(); e3
					.hasMoreElements();) {
				v.addElement(e3.nextElement());
			}

			int m = v.size();

			for (int i = 0; i < m; i++) {
				node.removeNode((TransientNode) v.elementAt(i));
			}
		}
	}

	/**
	 * "Physically" remove a subnode from the subnodes table. the logical stuff
	 * necessary for keeping data consistent is done elsewhere (in removeNode).
	 */
	protected void releaseNode(INode node) {
		if ((nodes == null) || (nodeMap == null)) {

			return;
		}

		int runner = nodes.indexOf(node);

		// this is due to difference between .equals() and ==
		while ((runner > -1) && (nodes.elementAt(runner) != node))
			runner = nodes
					.indexOf(node, Math.min(nodes.size() - 1, runner + 1));

		if (runner > -1) {
			nodes.removeElementAt(runner);
		}

		nodeMap.remove(node.getName().toLowerCase());
		lastmodified = System.currentTimeMillis();
	}

	/**
	 * 
	 * 
	 * @return ...
	 */
	public Enumeration<INode> getChildNodes() {
		return (nodes == null) ? new Vector<INode>().elements() : nodes
				.elements();
	}

	/**
	 * property-related
	 */
	public Enumeration<String> properties() {
		return (propMap == null) ? new Vector<String>().elements() : propMap
				.keys();
	}

	public IProperty getProperty(String propname) {
		IProperty prop = (propMap == null) ? null : propMap
				.get(correctPropertyName(propname));

		// check if we have to create a virtual node
		if ((prop == null) && (dbmap != null)) {
			Relation rel = dbmap.getPropertyRelation(propname);

			if ((rel != null) && rel.isVirtual()) {
				prop = makeVirtualNode(propname, rel);
			}
		}

		return prop;
	}

	private IProperty makeVirtualNode(String propname, Relation rel) {
		INode node = new PersistentNode(rel.getPropName(), rel.getPrototype(),
				dbmap.getWrappedNodeManager());

		node.setDbMapping(rel.getVirtualMapping());
		setNode(propname, node);

		return propMap.get(correctPropertyName(propname));
	}

	public IProperty get(String propname) {
		return getProperty(propname);
	}

	public String getString(String propname, String defaultValue) {
		String propValue = getString(propname);

		return (propValue == null) ? defaultValue : propValue;
	}

	public String getString(String propname) {
		IProperty prop = getProperty(propname);

		try {
			return prop.getStringValue();
		} catch (Exception ignore) {
		}

		return null;
	}

	public long getInteger(String propname) {
		IProperty prop = getProperty(propname);

		try {
			return prop.getIntegerValue();
		} catch (Exception ignore) {
		}

		return 0;
	}

	public double getFloat(String propname) {
		IProperty prop = getProperty(propname);

		try {
			return prop.getFloatValue();
		} catch (Exception ignore) {
		}

		return 0.0;
	}

	public Date getDate(String propname) {
		IProperty prop = getProperty(propname);

		try {
			return prop.getDateValue();
		} catch (Exception ignore) {
		}

		return null;
	}

	public boolean getBoolean(String propname) {
		IProperty prop = getProperty(propname);

		try {
			return prop.getBooleanValue();
		} catch (Exception ignore) {
		}

		return false;
	}

	public INode getNode(String propname) {
		IProperty prop = getProperty(propname);

		try {
			return prop.getNodeValue();
		} catch (Exception ignore) {
		}

		return null;
	}

	public Object getJavaObject(String propname) {
		IProperty prop = getProperty(propname);

		try {
			return prop.getJavaObjectValue();
		} catch (Exception ignore) {
		}

		return null;
	}

	// create a property if it doesn't exist for this name
	private IProperty initProperty(String propname) {
		if (propMap == null) {
			propMap = new Hashtable<String, IProperty>();
		}

		propname = propname.trim();
		String cpn = correctPropertyName(propname);
		IProperty prop = propMap.get(cpn);

		if (prop == null) {
			prop = new TransientProperty(propname, this);
			propMap.put(cpn, prop);
		}

		return prop;
	}

	public void setString(String propname, String value) {
		IProperty prop = initProperty(propname);
		prop.setStringValue(value);
		lastmodified = System.currentTimeMillis();
	}

	public void setInteger(String propname, long value) {
		IProperty prop = initProperty(propname);
		prop.setIntegerValue(value);
		lastmodified = System.currentTimeMillis();
	}

	public void setFloat(String propname, double value) {
		IProperty prop = initProperty(propname);
		prop.setFloatValue(value);
		lastmodified = System.currentTimeMillis();
	}

	public void setBoolean(String propname, boolean value) {
		IProperty prop = initProperty(propname);
		prop.setBooleanValue(value);
		lastmodified = System.currentTimeMillis();
	}

	public void setDate(String propname, Date value) {
		IProperty prop = initProperty(propname);
		prop.setDateValue(value);
		lastmodified = System.currentTimeMillis();
	}

	public void setJavaObject(String propname, Object value) {
		IProperty prop = initProperty(propname);
		prop.setJavaObjectValue(value);
		lastmodified = System.currentTimeMillis();
	}

	public void setNode(String propname, INode value) {
		IProperty prop = initProperty(propname);
		prop.setNodeValue(value);

		// check if the main identity of this node is as a named property
		// or as an anonymous node in a collection
		if (value instanceof TransientNode) {
			TransientNode n = (TransientNode) value;

			if (n.parent == null) {
				n.name = propname;
				n.parent = this;
				n.anonymous = false;
			}
		}

		lastmodified = System.currentTimeMillis();
	}

	public void unset(String propname) {
		if (propMap != null && propname != null) {
			propMap.remove(correctPropertyName(propname));
			lastmodified = System.currentTimeMillis();
		}
	}

	public long lastModified() {
		return lastmodified;
	}

	public long created() {
		return created;
	}

	public String toString() {
		return "TransientNode " + name;
	}

	/**
	 * Get the cache node for this node. This can be used to store transient
	 * cache data per node from Javascript.
	 */
	public synchronized INode getCacheNode() {
		if (cacheNode == null) {
			cacheNode = new TransientNode(app);
		}

		return cacheNode;
	}

	/**
	 * Reset the cache node for this node.
	 */
	public synchronized void clearCacheNode() {
		cacheNode = null;
	}

	private String correctPropertyName(String propname) {
		return app.correctPropertyName(propname);
	}

	public WrappedNodeManager getNodeManager() {
		return null;
	}

	public NodeHandle getHandle() {
		return null;
	}

	public Key getKey() {
		return null;
	}

	public long getLastSubnodeChange() {
		return 0;
	}

	public boolean hasNodeManager() {
		return false;
	}

	public void setParent(INode parent) {
		this.setParent(parent);
	}

	public INode getNonVirtualParent() {
		return null;
	}

	// TODO: check should do nothing?
	public void setProperty(String name, IProperty property) {
		IProperty prop = initProperty(name);
		prop.setJavaObjectValue(property.getValue());
	}

	public INode getGroupbySubnode(String kstr, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}
}
