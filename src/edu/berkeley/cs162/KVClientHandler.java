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
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
		//System.out.println("handle called");
		ConnectionHandler newTask = new ConnectionHandler(client);
		//System.out.println("Time to add to ThreadPool");
		if(newTask.failed == false){
			try {
				threadpool.addToQueue(newTask);
				} catch (InterruptedException e) {
					e.printStackTrace();
			}		
		} else {
			System.out.println("xml parsing error");
		}
	}
	
	private class ConnectionHandler implements Runnable{
		Socket s1;
		KVMessage message;
		public boolean failed = false;
		
		public ConnectionHandler(Socket client) throws IOException{
			//System.out.println("ConnectionHandler constructor called");
			this.s1 = client;
			KVMessage response = new KVMessage("resp", null, null);
			String xml = null;
			
			try {
				//System.out.println("Getting inputstream");
				message = new KVMessage(s1.getInputStream());
				//System.out.println("Got inputstream and parsed message");
			} catch (KVException e) {
				FilterOutputStream fos = new FilterOutputStream(s1.getOutputStream());
				fos.flush();
				System.out.println("KVException caught line 94");
				response.setMessage(e.getMsg().getMessage());
				response.setKey(e.getMsg().getKey());
				response.setValue(e.getMsg().getValue());
				response.setStatus(e.getMsg().getStatus());
				try {
					xml = response.toXML();
				} catch (KVException e1) {
					//System.out.println(e1.getMsg().getMessage());
					xml = "xml parsing error line 104";
				}
				
				byte[] xmlBytes = xml.getBytes();
				try{
						fos.write(xmlBytes);
						fos.flush();
					} catch (IOException e2){
						System.out.println("IO Error line 111");
					}
				s1.close();
				failed = true;
			}
			System.out.println("KVMessage from client:");
			System.out.println(message);
		}
		
		public void run() {
			//System.out.println("run called");
			FilterOutputStream fos = null;
			try {
				fos = new FilterOutputStream(s1.getOutputStream());
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			KVMessage response = null;
			String xml = "xml parsing error line 129";
			if(message.getMsgType().equals("getreq")) {
				try {
					String value = KVMessage.marshal(keyserver.get(
							(K) KVMessage.unmarshal(message.getKey())));
					response = new KVMessage("resp" , message.getKey(), value,
							false, "Success");
				} catch (KVException e) {
					response = new KVMessage("resp", e.getMsg().getKey(), 
							e.getMsg().getValue(), e.getMsg().getStatus(), e.getMsg().getMessage());		
				} catch (IOException e) {
					response = new KVMessage("resp", null, null
							, false, "IO Error");
				} catch (ClassNotFoundException e) {
					response = new KVMessage("resp", null, null
							, false, "Unkown Error: Class Not Found");
				} finally {
					try {
						xml = response.toXML();
						System.out.println("XML RESPONSE: " + xml);
					} catch (KVException e1) {
						System.out.println("Fail XML conversion");
					}
					byte[] xmlBytes = xml.getBytes();
					try{
						fos.write(xmlBytes);
						fos.flush();
					} catch (IOException e){
						System.out.println("IO Error");
					}
					try {
						s1.shutdownOutput();
					} catch (IOException e) {
						
					}
				} 
				
			} else if (message.getMsgType().equals("putreq")) {
				 try {
					 //System.out.println("message.getKey(): " + message.getKey());
					K k1 = (K) KVMessage.unmarshal(message.getKey());
					//System.out.println("k1: " + k1);
					//System.out.println("message.getValue(): " + message.getValue());
					V v1 = (V) KVMessage.unmarshal(message.getValue());
					//System.out.println("v1: " + v1);
					boolean result = keyserver.put(k1,v1);
					String resultString; if (result) resultString = "True"; else resultString = "False";
					response = new KVMessage("resp" , null, null, resultString, "Success");

					
				} catch (KVException e) {
					response = new KVMessage("resp", e.getMsg().getKey(), 
							e.getMsg().getValue(), e.getMsg().getStatus(), e.getMsg().getMessage());
				} catch (IOException e) {
					response = new KVMessage("resp", null, null
							, false, "IO Error");
				} catch (ClassNotFoundException e) {
					response = new KVMessage("resp", null, null
							, false, "Unkown Error: Class Not Found");
				} finally {
					try {
						xml = response.toXML();
						System.out.println("XML RESPONSE: " + xml);
					} catch (KVException e1) {
						System.out.println("Fail XML conversion");
					}
					byte[] xmlBytes = xml.getBytes();
					//System.out.println("Beginning response send");
					try{
						fos.write(xmlBytes);
						fos.flush();
					} catch (IOException e){
						System.out.println("IO Error");
					}
					try {
						s1.shutdownOutput();
					} catch (IOException e) {
						
					}
				}
				 System.out.println("response sent");
				 System.out.println("******************************");
				 
			} else if (message.getMsgType().equals("delreq")) {
				try {
					keyserver.del((K) KVMessage.unmarshal(message.getKey()));
					response = new KVMessage("resp" , message.getKey() , null, false, "Success");
				} catch (KVException e) {
					response = new KVMessage("resp", e.getMsg().getKey(), 
							e.getMsg().getValue(), e.getMsg().getStatus(), e.getMsg().getMessage());
				} catch (IOException e) {
					response = new KVMessage("resp", null, null
							, false, "IO Error");
				} catch (ClassNotFoundException e) {
					response = new KVMessage("resp", null, null
							, false, "Unkown Error: Class Not Found");
				} finally {
					try {
						xml = response.toXML();
					} catch (KVException e1) {
						System.out.println("Fail XML conversion");
					}
					byte[] xmlBytes = xml.getBytes();
					try{
						fos.write(xmlBytes);
						fos.flush();
					} catch (IOException e){
						System.out.println("IO Error");
					}
					try {
						s1.shutdownOutput();
					} catch (IOException e) {
						
					}
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
