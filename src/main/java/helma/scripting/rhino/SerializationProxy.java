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

import java.io.Serializable;

/**
 * Serialization proxy/placeholder interface. This is used for for various Helma
 * and Rhino related classes..
 */
public interface SerializationProxy extends Serializable {
	public Object getObject(RhinoEngine engine);
}
