/*
 * The imageio integration is inspired by the package org.freehep.graphicsio.gif
 */

package helma.image.imageio.gif;

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

import java.util.Locale;
import java.util.Properties;

import javax.imageio.ImageWriteParam;

public class GIFImageWriteParam extends ImageWriteParam {

	public GIFImageWriteParam(Locale locale) {
		super(locale);
		canWriteProgressive = true;
		progressiveMode = MODE_DEFAULT;
	}

	public ImageWriteParam getWriteParam(Properties properties) {
		return this;
	}
}