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

import helma.orm.db.Relation;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Interface that is implemented by node properties.
 */
public interface IProperty {
	public static final int STRING = 1;
	public static final int BOOLEAN = 2;
	public static final int DATE = 3;
	public static final int INTEGER = 4;
	public static final int FLOAT = 5;
	public static final int NODE = 6;
	public static final int JAVAOBJECT = 7;
	public static final int NULL = 0;

	/**
	 * 
	 * 
	 * @return ...
	 */
	public String getName();

	/**
	 * 
	 * 
	 * @return ...
	 */
	public int getType();

	/**
	 * 
	 * 
	 * @return ...
	 */
	public Object getValue();

	/**
	 * 
	 * 
	 * @return ...
	 */
	public INode getNodeValue();

	/**
	 * 
	 * 
	 * @return ...
	 */
	public String getStringValue();

	/**
	 * 
	 * 
	 * @return ...
	 */
	public boolean getBooleanValue();

	/**
	 * 
	 * 
	 * @return ...
	 */
	public long getIntegerValue();

	/**
	 * 
	 * 
	 * @return ...
	 */
	public double getFloatValue();

	/**
	 * 
	 * 
	 * @return ...
	 */
	public Date getDateValue();

	/**
	 * 
	 * 
	 * @return ...
	 */
	public Object getJavaObjectValue();

	public void setStringValue(String value);

	public void setIntegerValue(long value);

	public void setFloatValue(double value);

	public void setBooleanValue(boolean value);

	public void setDateValue(Date value);

	public void setJavaObjectValue(Object value);

	public void setNodeValue(INode value);

	public Timestamp getTimestampValue();

	public void convertToNodeReference(Relation rel);
}
