package helma.framework;

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
 * The basic exception class used to tell when certain things go wrong in
 * evaluation of requests.
 */
public class FrameworkException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8477797850472128617L;

	/**
	 * Creates a new FrameworkException object.
	 * 
	 * @param msg
	 *            ...
	 */
	public FrameworkException(String msg) {
		super(msg);
	}
}
