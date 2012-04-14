/**
 * Handle client connections over a socket interface
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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.zip.DataFormatException;

/**
 * This NetworkHandler will asynchronously handle the socket connections. 
 * It uses a threadpool to ensure that none of it's methods are blocking.
 *
 * @param <K> Java Generic type for the Key
 * @param <V> Java Generic type for the Value
 */
public class KVClientHandler<K extends Serializable, V extends Serializable> implements NetworkHandler {
	private KeyServer<K, V> keyserver = null;
	private ThreadPool threadpool = null;
	
	public KVClientHandler(KeyServer<K, V> keyserver) {
		initialize(keyserver, 1);
	}

	public KVClientHandler(KeyServer<K, V> keyserver, int connections) {
		initialize(keyserver, connections);
	}

	private void initialize(KeyServer<K, V> keyserver, int connections) {
		this.keyserver = keyserver;
		threadpool = new ThreadPool(connections);	
	}
	
	/* (non-Javadoc)
	 * @see edu.berkeley.cs162.NetworkHandler#handle(java.net.Socket)
	 */
	@Override
	public void handle(Socket client) throws IOException {
		ConnectionHandler newTask = new ConnectionHandler(client);
		try {
			threadpool.addToQueue(newTask);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
	private class ConnectionHandler implements Runnable{
		Socket s1;
		KVMessage message;
		
		public ConnectionHandler(Socket client){
			this.s1 = client;
			try {
				message = new KVMessage(s1.getInputStream());
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		
		//Should I have the the runnable return a response based on the result of
		//the appropriate request to keyserver?
		public void run() {
			OutputStream os = null;
			try {
				os = s1.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			PrintWriter pw = new PrintWriter(os);
			KVMessage response = null;
			
			if(message.getMsgType() == "getreq") {
				try {
					String value = (String) keyserver.get((K)message.getKey());
					response = new KVMessage("resp" , message.getKey(), value,
							false, "Success");
					String xml = response.toXML();
					pw.write(xml);
				} catch (KVException e) {
						response = new KVMessage("resp", e.getMsg().getKey(), 
								e.getMsg().getValue(), e.getMsg().getStatus(), e.getMsg().getMessage());
						String xml = response.toXML();
						pw.write(xml);
				} 
			} else if (message.getMsgType() == "putreq") {
				 try {
					boolean result = keyserver.put((K)message.getKey(), (V) message.getValue());
					response = new KVMessage("resp" , null, null, result, "Success");
					String xml = response.toXML();
					pw.write(xml);
				} catch (KVException e) {
					response = new KVMessage("resp", e.getMsg().getKey(), 
							e.getMsg().getValue(), e.getMsg().getStatus(), e.getMsg().getMessage());
					String xml = response.toXML();
					pw.write(xml);

				}
			} else if (message.getMsgType() == "delreq") {
				try {
					keyserver.del((K)message.getKey());
					response = new KVMessage("resp" , message.getKey() , null, false, "Success");
					String xml = response.toXML();
					pw.write(xml);
				} catch (KVException e) {
					response = new KVMessage("resp", e.getMsg().getKey(), 
							e.getMsg().getValue(), e.getMsg().getStatus(), e.getMsg().getMessage());
					String xml = response.toXML();
					pw.write(xml);
				}
			}
			
			try {
				s1.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
