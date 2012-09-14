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
 * AbortException is thrown internally when a response is aborted. Although this
 * is not an Error, it subclasses java.lang.Error because it's not meant to be
 * caught by application code (similar to java.lang.ThreadDeath).
 */
public class AbortException extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7536693051844908815L;

}
