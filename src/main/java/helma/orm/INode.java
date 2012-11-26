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
import helma.orm.db.DbMapping;
import helma.orm.db.Key;
import helma.orm.db.NodeHandle;
import helma.orm.db.SubnodeList;
import helma.orm.db.WrappedNodeManager;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Interface that all Nodes implement. Currently, there are two implementations:
 * Transient nodes which only exist in memory, and persistent Nodes, which are
 * stored in a database (either the internal Object DB or an external relational
 * DB).
 */
public interface INode extends INodeState, IPathElement {

	/**
	 * Get the node's ID.
	 */
	public String getID();

	/**
	 * Get the node's name.
	 */
	public String getName();

	/**
	 * Set the node's {@link DbMapping}.
	 */
	public void setDbMapping(DbMapping dbmap);

	/**
	 * Get the node's {@link DbMapping}.
	 */
	public DbMapping getDbMapping();

	/**
	 * Get the node's state flag.
	 * 
	 * @return one of the constants defined in the {@link INodeState} interface.
	 */
	public int getState();

	/**
	 * Set the node's state flag.
	 * 
	 * @param s
	 *            one of the constants defined in the {@link INodeState}
	 *            interface.
	 */
	public void setState(int s);

	/**
	 * Set the node's name.
	 */
	public void setName(String name);

	/**
	 * Get the node's last modification timestamp.
	 */
	public long lastModified();

	/**
	 * Get the node's creation timestamp.
	 */
	public long created();

	/**
	 * Returns true if this node is an unnamed node.
	 */
	public boolean isAnonymous();

	public void setAnonymous(boolean anonymous);

	/**
	 * Return the node's prototype name.
	 */
	public String getPrototype();

	/**
	 * Set the node's prototype name.
	 */
	public void setPrototype(String prototype);

	/**
	 * Get the cache node associated with this node.
	 */
	public INode getCacheNode();

	/**
	 * Clear the cache node associated with this node.
	 */
	public void clearCacheNode();

	/**
	 * Get the node's path.
	 */
	public String getPath();

	/**
	 * Get the node's parent node.
	 */
	public INode getParent();

	/**
	 * Set an explicit select clause for the node's subnodes
	 */
	public void setSubnodeRelation(String clause);

	/**
	 * Get the node's explicit subnode select clause if one was set, or null
	 */
	public String getSubnodeRelation();

	/**
	 * Get the number the node's direct child nodes.
	 */
	public int getNumberOfChildNodes();

	/**
	 * Add a child node to this node.
	 */
	public INode addNode(INode node);

	/**
	 * Add a child node to this node at the given position
	 */
	public INode addNode(INode node, int where);

	/**
	 * Create a new named property with a node value
	 */
	public INode createNode(String name);

	/**
	 * Create a new unnamed child node at the given position.
	 */
	public INode createNode(String name, int where);

	/**
	 * Get an enumeration of this node's unnamed child nodes
	 */
	public Enumeration<INode> getChildNodes();

	/**
	 * Get a named child node with the given name or id.
	 */
	public INode getChildNode(String name);

	/**
	 * GEt an unnamed child node at the given position
	 */
	public INode getSubnodeAt(int index);

	/**
	 * Returns the position of the child or -1.
	 */
	public int contains(INode node);

	/**
	 * Remove this node from the database.
	 */
	public boolean remove();

	/**
	 * Remove the given node from this node's child nodes.
	 */
	public void removeNode(INode node);

	/**
	 * Get an enumeration over the node's properties.
	 */
	public Enumeration<String> properties();

	/**
	 * Get a property with the given name.
	 */
	public IProperty get(String name);

	/**
	 * Get a string property with the given name.
	 */
	public String getString(String name);

	/**
	 * Get a boolean property with the given name.
	 */
	public boolean getBoolean(String name);

	/**
	 * Get a date property with the given name.
	 */
	public Date getDate(String name);

	/**
	 * Get an integer property with the given name.
	 */
	public long getInteger(String name);

	/**
	 * Get a float property with the given name.
	 */
	public double getFloat(String name);

	/**
	 * Get a node property with the given name.
	 */
	public INode getNode(String name);

	/**
	 * Get a Java object property with the given name.
	 */
	public Object getJavaObject(String name);

	/**
	 * Set the property with the given name to the given string value.
	 */
	public void setString(String name, String value);

	/**
	 * Set the property with the given name to the given boolean value.
	 */
	public void setBoolean(String name, boolean value);

	/**
	 * Set the property with the given name to the given date value.
	 */
	public void setDate(String name, Date value);

	/**
	 * Set the property with the given name to the given integer value.
	 */
	public void setInteger(String name, long value);

	/**
	 * Set the property with the given name to the given float value.
	 */
	public void setFloat(String name, double value);

	/**
	 * Set the property with the given name to the given node value.
	 */
	public void setNode(String name, INode value);

	/**
	 * Set the property with the given name to the given Java object value.
	 */
	public void setJavaObject(String name, Object value);

	/**
	 * Unset the property with the given name..
	 */
	public void unset(String name);

	/**
	 * Return the node manager of the node
	 */
	public WrappedNodeManager getNodeManager();

	/**
	 * 
	 * @return the {@link NodeHandle} of the Node
	 */
	public NodeHandle getHandle();

	/**
	 * 
	 * @return the internal ObjectKey
	 */
	public Key getKey();

	public long getLastSubnodeChange();

	public void setLastmodified(long lastmodified);

	public long getLastmodified();

	public void setCreated(long created);

	public long getCreated();

	public boolean hasNodeManager();

	public void setParent(INode parent);

	/**
	 * Directly set a property on this node
	 * 
	 * @param name
	 *            the name of the property
	 * @param property
	 *            the Property itself
	 */
	public void setProperty(String name, IProperty property);

	/**
	 * Check if the given node is contained in this node's child list. This is
	 * similar to <code>contains(INode)</code> but does not load the child index
	 * for relational nodes.
	 * 
	 * @param n
	 *            a node
	 * @return true if the given node is contained in this node's child list
	 */
	public boolean isParentOf(INode n);

	public INode getNonVirtualParent();

	public IProperty getProperty(String propName);

	public void markSubnodesChanged();

	public Hashtable<String, IProperty> getProperties();

	public INode getGroupbySubnode(String kstr, boolean b);

	public SubnodeList getChildren();

	public SubnodeList getChildren(boolean create);
}
