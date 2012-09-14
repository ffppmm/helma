package helma.util;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 */
public class XmlUtils {
	private static DocumentBuilderFactory domBuilderFactory = null;

	/**
	 * 
	 * 
	 * @param obj
	 *            ...
	 * 
	 * @return ...
	 * 
	 * @throws SAXException
	 *             ...
	 * @throws IOException
	 *             ...
	 * @throws ParserConfigurationException
	 *             ...
	 */
	public static Document parseXml(Object obj) throws SAXException,
			IOException, ParserConfigurationException {
		if (domBuilderFactory == null) {
			domBuilderFactory = javax.xml.parsers.DocumentBuilderFactory
					.newInstance();
		}

		DocumentBuilder parser = domBuilderFactory.newDocumentBuilder();
		Document doc;

		if (obj instanceof String) {
			try {
				// first try to interpret string as URL
				new URL(obj.toString());

				doc = parser.parse(obj.toString());
			} catch (MalformedURLException nourl) {
				// if not a URL, maybe it is the XML itself
				doc = parser.parse(new InputSource(new StringReader(obj
						.toString())));
			}
		} else if (obj instanceof InputStream) {
			doc = parser.parse(new InputSource((InputStream) obj));
		} else if (obj instanceof Reader) {
			doc = parser.parse(new InputSource((Reader) obj));
		} else {
			throw new RuntimeException("Unrecognized argument to parseXml: "
					+ obj);
		}

		doc.normalize();
		return doc;
	}
}
