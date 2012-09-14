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

/**
 * Thrown when more than one thrad tries to modify a Node. The evaluator will
 * normally catch this and try again after a period of time.
 */
public class ConcurrencyException extends Error {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4031542073544406467L;

	/**
	 * Creates a new ConcurrencyException object.
	 * 
	 * @param msg
	 *            ...
	 */
	public ConcurrencyException(String msg) {
		super(msg);
	}
}
