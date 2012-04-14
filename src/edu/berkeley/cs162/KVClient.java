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


import java.io.*;
import java.net.*;
import javax.xml.bind.DatatypeConverter;

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
	
	/**
	 * @param server is the DNS reference to the Key-Value server
	 * @param port is the port on which the Key-Value server is listening
	 */
	public KVClient(String server, int port) {
		this.server = server;
		this.port = port;
	}
	
	@Override
	public boolean put(K key, V value) throws KVException {
		String keyAsString = KVMessage.marshall(key);
		if (keyAsString.getBytes().length > 256){
			throw new KVException(new KVMessage("error", null, null, false, "Over sized key"));
		}
		
		String valueAsString = KVMessage.marshall(value);
		if(valueAsString.getBytes().length > 131072){
			throw new KVException(new KVMessage("error", null, null, false, "Over sized value"));
		}
		
		Socket s = null;
		
		try{
			s = new Socket(server, port);
		} catch (UnknownHostException e){
			throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "Network Error: Could not create socket"));
		} catch (IOException e){
			throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "IO Error"));
		}
		
		OutputStream os = null;
		
		try{
			os = s.getOutputStream();
		} catch(IOException e){
			try{
				s.close();
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "Network Error: Could not send data"));
			} catch (IOException e2){
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "IO Error"));
			}
		}


		
		PrintWriter pw = new PrintWriter(os);
		KVMessage reqMessage = new KVMessage("putreq", keyAsString, valueAsString);
		String xml = reqMessage.toXML();
		pw.write(xml);
		
		try{
			s.setSoTimeout(10000);
		} catch(SocketException e){
			try{
				s.close();
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "Network Error"));
			} catch (IOException e2){
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "IO Error"));
			}
		}

		
		InputStream is = null;
		
		try{
			is = s.getInputStream();
		} catch(IOException e){
			try{
				s.close();
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "Network Error"));
			} catch (IOException e2){
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "IO Error"));
			}
		}

		
		KVMessage respMessage = new KVMessage(is);
		
		try{
			s.close();
		} catch(IOException e){
			throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "IO Error"));
		}
		
		if(respMessage.getMessage().equals("Success")){
			return respMessage.getStatus();
		}
		else{
			throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, respMessage.getMessage()));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) throws KVException {
		String keyAsString = KVMessage.marshall(key);
		if (keyAsString.getBytes().length > 256){
			throw new KVException(new KVMessage("error", null, null, false, "Over sized key"));
		}
		
		String valueAsString = null;
		
		Socket s = null;
		
		try{
			s = new Socket(server, port);
		} catch (UnknownHostException e){
			throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "Network Error: Could not create socket"));
		} catch (IOException e){
			throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "IO Error"));
		}
		
		OutputStream os = null;
		
		try{
			os = s.getOutputStream();
		} catch(IOException e){
			try{
				s.close();
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "Network Error: Could not send data"));
			} catch (IOException e2){
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "IO Error"));
			}
		}


		
		PrintWriter pw = new PrintWriter(os);
		KVMessage reqMessage = new KVMessage("getreq", keyAsString, valueAsString);
		String xml = reqMessage.toXML();
		pw.write(xml);
		
		try{
			s.setSoTimeout(10000);
		} catch(SocketException e){
			try{
				s.close();
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "Network Error"));
			} catch (IOException e2){
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "IO Error"));
			}
		}

		
		InputStream is = null;
		
		try{
			is = s.getInputStream();
		} catch(IOException e){
			try{
				s.close();
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "Network Error"));
			} catch (IOException e2){
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "IO Error"));
			}
		}

		
		KVMessage respMessage = new KVMessage(is);
		
		try{
			s.close();
		} catch(IOException e){
			throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "IO Error"));
		}
		
		if(respMessage.getMessage().equals("Success")){
			V value = null;
			try{
				value = (V) KVMessage.unmarshall(respMessage.getValue());
			} catch (IOException e){
				throw new KVException(new KVMessage("error", keyAsString, respMessage.getValue(), false, "IO Error"));
			}

			return value;
		}
		else{
			throw new KVException(new KVMessage("error", keyAsString, null, false, respMessage.getMessage()));
		}
	}

	@Override
	public void del(K key) throws KVException {
		String keyAsString = KVMessage.marshall(key);
		if (keyAsString.getBytes().length > 256){
			throw new KVException(new KVMessage("error", null, null, false, "Over sized key"));
		}
		
		String valueAsString = null;
		
		Socket s = null;
		
		try{
			s = new Socket(server, port);
		} catch (UnknownHostException e){
			throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "Network Error: Could not create socket"));
		} catch (IOException e){
			throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "IO Error"));
		}
		
		OutputStream os = null;
		
		try{
			os = s.getOutputStream();
		} catch(IOException e){
			try{
				s.close();
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "Network Error: Could not send data"));
			} catch (IOException e){
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "IO Error"));
			}
		}


		
		PrintWriter pw = new PrintWriter(os);
		KVMessage reqMessage = new KVMessage("delreq", keyAsString, valueAsString);
		String xml = reqMessage.toXML();
		pw.write(xml);
		
		try{
			s.setSoTimeout(10000);
		} catch(SocketException e){
			try{
				s.close();
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "Network Error"));
			} catch (IOException e2){
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "IO Error"));
			}
		}

		
		InputStream is = null;
		
		try{
			is = s.getInputStream();
		} catch(IOException e){
			try{
				s.close();
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "Network Error"));
			} catch (IOException e2){
				throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "IO Error"));
			}
		}

		
		KVMessage respMessage = new KVMessage(is);
		
		try{
			s.close();
		} catch(IOException e){
			throw new KVException(new KVMessage("error", keyAsString, valueAsString, false, "IO Error"));
		}
		
		if(!respMessage.getMessage().equals("Success")){
			throw new KVException(new KVMessage("error", keyAsString, null, false, respMessage.getMessage()));
		}

	}
}
