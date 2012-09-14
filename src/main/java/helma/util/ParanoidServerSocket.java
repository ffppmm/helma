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
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A server socket that can allow connections to only a few selected hosts.
 */
public class ParanoidServerSocket extends ServerSocket {
	private InetAddressFilter filter;

	/**
	 * Creates a new ParanoidServerSocket object.
	 * 
	 * @param port
	 *            ...
	 * 
	 * @throws IOException
	 *             ...
	 */
	public ParanoidServerSocket(int port) throws IOException {
		super(port);
	}

	/**
	 * Creates a new ParanoidServerSocket object.
	 * 
	 * @param port
	 *            ...
	 * @param filter
	 *            ...
	 * 
	 * @throws IOException
	 *             ...
	 */
	public ParanoidServerSocket(int port, InetAddressFilter filter)
			throws IOException {
		super(port);
		this.filter = filter;
	}

	/**
	 * 
	 * 
	 * @return ...
	 * 
	 * @throws IOException
	 *             ...
	 */
	public Socket accept() throws IOException {
		Socket s = null;

		while (s == null) {
			s = super.accept();

			if ((filter != null) && !filter.matches(s.getInetAddress())) {
				System.err.println("Refusing connection from "
						+ s.getInetAddress());

				try {
					s.close();
				} catch (IOException ignore) {
				}

				s = null;
			}
		}

		return s;
	}

	/**
	 * 
	 * 
	 * @param filter
	 *            ...
	 */
	public void setFilter(InetAddressFilter filter) {
		this.filter = filter;
	}

	/**
	 * 
	 * 
	 * @return ...
	 */
	public InetAddressFilter getFilter() {
		return this.filter;
	}
}
