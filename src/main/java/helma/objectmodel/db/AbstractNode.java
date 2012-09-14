package helma.objectmodel.db;

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
import helma.objectmodel.IProperty;

import java.util.Hashtable;

public abstract class AbstractNode implements INode {
	protected long created;

	protected long lastmodified;

	/**
	 * Ordered list of subnodes of this node
	 */
	protected SubnodeList children = null;

	/**
	 * Named subnodes (properties) of this node
	 */
	protected Hashtable<String, IProperty> properties;

	/**
	 * is the main identity a named property or an anonymous node in a
	 * collection?
	 */
	protected boolean anonymous = false;

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public long getLastmodified() {
		return lastmodified;
	}

	public void setLastmodified(long lastmodified) {
		this.lastmodified = lastmodified;
	}

	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	public boolean isParentOf(INode n) {
		return contains(n) > -1;
	}

	/**
	 * Called by the transactor on registered parent nodes to mark the child
	 * index as changed
	 */
	public void markSubnodesChanged() {
		if (children != null) {
			children.markAsChanged();
		}
	}

	/**
	 * 
	 * 
	 * @return ...
	 */
	public Hashtable<String, IProperty> getProperties() {
		return properties;
	}

	public SubnodeList getChildren() {
		return getChildren(false);
	}

	public SubnodeList getChildren(boolean create) {
		if (create) {
			Relation subrel = this.getDbMapping() == null ? null : this
					.getDbMapping().getSubnodeRelation();
			children = subrel == null || !subrel.lazyLoading ? new SubnodeList(
					this) : new SegmentedSubnodeList(this);
		}
		return children;
	}

}
