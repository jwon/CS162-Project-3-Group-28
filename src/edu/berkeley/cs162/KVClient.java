/**
 * Client component for generating load for the KeyValue store. 
 * This is also used by the Master server to reach the slave nodes.
 * 
 * @author Prashanth Mohan (http://www.cs.berkeley.edu/~prmohan)
 *
 * Copyright (c) 2011, University of California at Berkeley
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of University of California, Berkeley nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *    
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL PRASHANTH MOHAN BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.berkeley.cs162;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

import java.net.*;
/**
 * This class is used to communicate with (appropriately marshalling and unmarshalling) 
 * objects implementing the {@link KeyValueInterface}.
 *
 * @param <K> Java Generic type for the Key
 * @param <V> Java Generic type for the Value
 */
public class KVClient<K extends Serializable, V extends Serializable> implements KeyValueInterface<K, V> {

	private String server = null;
	private int port = 0;
	
	private InputStream inStream;
	private OutputStream outStream;
	
	/**
	 * @param server is the DNS reference to the Key-Value server
	 * @param port is the port on which the Key-Value server is listening
	 */
	public KVClient(String server, int port) {
		this.server = server;
		this.port = port;

		try{
			this.socket = new Socket(server, port);
		} catch (UnknownHostException e){
			System.out.println("Network Error: Could not connect");
			exit();
		} catch (IOException e){
			System.out.println("Network Error: Could not create socket");
			exit();
		}

		outStream = new ByteArrayOutputStream(socket.getOutputStream());
		inStream = new ByteArrayInputStream(socket.getInputStream());
	}
	
	@Override
	public boolean put(K key, V value) throws KVException {
		byte[] keyByteArray;
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(keyByteArray);
		ObjectOutputStream marshaller = new ObjectOutputStream(byteStream);

		//TODO: write key to marshaller
		//TODO: check that byte array isn't longer than 256 bytes

		String mKey = new String(keyByteArray);
		//is this right? Or am I supposed to use javax?
		

		//TODO: Same process as above but for value, into a String mValue
		//TODO: close byteStream and marshaller

		//TODO: try/catch network errors?

		KVMessage reqMessage = new KVMessage("putreq", mKey, mValue);

		//TODO: write reqMessage.toXML() to outStream but convert to byte[] first

		try{
			KVMessage respMessage = new KVMessage(inStream);
		} catch (KVException e){
			System.out.println("XML Error: Received unparsable message");
			exit();
		}

		System.out.println(respMessage.msgType);

		return respMessage.status;

		 
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) throws KVException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream marshaller = new ObjectOutputStream(byteStream);

		//TODO: write key to marshaller

		//TODO: byte[] keyByteArray = byteStream's byte array

		String mKey = new String(keyByteArray);

		//TODO: close byteStream and marshaller
		
		reqMessage = new KVMessage("getreq", mKey, "");

		//TODO: write reqMessage.toXML() to outStream
		
		try{
			KVMessage respMessage = new KVMessage(inStream);
		} catch (KVException e){
			System.out.println("XML Error: Received unparsable message");
			exit();
		}

		System.out.println(respMessage.msgType);

		return respMessage.status;
	}

	@Override
	public void del(K key) throws KVException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream marshaller = new ObjectOutputStream(byteStream);

		//TODO: write key to marshaller
		//TODO: byte[] keyByteArray = byteStream's byte array

		String mKey = new String(keyByteArray);

		//TODO: close byteStream and marshaller

		reqMessage = new KVMessage("getreq", mKey, "");

		//TODO: write reqMessage.toXML() to outStream

		try{
			KVMessage respMessage = new KVMessage(inStream);
		} catch (KVException e) {
			System.out.println("XML Error: Received unparsable message");
			exit();
		}

		System.out.println(respMessage.msgType);
	}
}
