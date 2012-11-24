package helma.objectmodel;

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

import helma.objectmodel.db.Relation;

import java.sql.Timestamp;
import java.util.Date;

final public class NullProperty implements IProperty {
	private static NullProperty instance = new NullProperty();

	private NullProperty() {

	}

	public static NullProperty getInstance() {
		return instance;
	}

	public String getName() {
		return null;
	}

	public int getType() {
		// TODO this works?
		return IProperty.NULL;
	}

	public Object getValue() {
		return null;
	}

	public INode getNodeValue() {
		return null;
	}

	public String getStringValue() {
		return null;
	}

	public boolean getBooleanValue() {
		// TODO this works?
		return false;
	}

	public long getIntegerValue() {
		// TODO this works?
		return 0L;
	}

	public double getFloatValue() {
		// TODO this works?
		return 0.0;
	}

	public Date getDateValue() {
		return null;
	}

	public Object getJavaObjectValue() {
		return null;
	}

	public void setStringValue(String value) {
		// Do nothing
	}

	public void setIntegerValue(long value) {
		// Do nothing
	}

	public void setFloatValue(double value) {
		// Do nothing
	}

	public void setBooleanValue(boolean value) {
		// Do nothing
	}

	public void setDateValue(Date value) {
		// Do nothing
	}

	public void setJavaObjectValue(Object value) {
		// Do nothing
	}

	public void setNodeValue(INode value) {
		// Do nothing
	}

	public Timestamp getTimestampValue() {
		return null;
	}

	public void convertToNodeReference(Relation rel) {
		// do nothing
	}

}
