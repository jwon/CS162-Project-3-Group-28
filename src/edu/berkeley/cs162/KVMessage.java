/**
 * 
 * XML Parsing library for the key-value store
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
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * This is the object that is used to generate messages the XML based messages 
 * for communication between clients and servers. Data is stored in a 
 * marshalled String format in this object.
 */
@XmlRootElement
public class KVMessage {
	private String msgType = null;
	private String key = null;
	private String value = null;
	private boolean status = false;
	private String message = null;

	public KVMessage(String msgType, String key, String value) {
		this.msgType = msgType;
		this.key = key;
		this.value = value;
	}
	
	// This constructor will handle the Serializable -> String marshalling,
	// and should be the one actually used by KVClient.
	// Will throw DataFormatException if either the key or value are too long.
	public KVMessage(String msgType, Serializable key, Serializable value, boolean status, String message) {
		this.msgType = msgType;
		this.key = marshall(key);
		this.value = marshall(value);
		this.status = status;
		this.message = message;
		
		
	}
	
	/** Read the object from Base64 string. */
    public static Object unmarshall(String s) throws IOException, ClassNotFoundException {
        byte [] data = DatatypeConverter.parseBase64Binary(s);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    /** Write the object to a Base64 string. */
    public static String marshall( Serializable o ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
			ObjectOutputStream oos = new ObjectOutputStream( baos );
			oos.writeObject( o );
			oos.close();
		} catch (IOException e) {
			// Shouldn't happen
		}
        return new String( DatatypeConverter.printBase64Binary(baos.toByteArray()));
    }

	
	/* Hack for ensuring XML libraries does not close input stream by default.
	 * Solution from http://weblogs.java.net/blog/kohsuke/archive/2005/07/socket_xml_pitf.html */
	private class NoCloseInputStream extends FilterInputStream {
	    public NoCloseInputStream(InputStream in) {
	        super(in);
	    }
	    
	    public void close() {} // ignore close
	}
	
	public KVMessage(InputStream input)  {
		KVMessage dummy;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(KVMessage.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			dummy = (KVMessage) jaxbUnmarshaller.unmarshal(new NoCloseInputStream(input));
		} catch (UnmarshalException e) {
			// XXX Not sure what to do here; should throw an exception and die at this point
			return;
		} catch (JAXBException e) {
			return;
		}
		
		this.msgType = dummy.msgType;
		this.key = dummy.key;
		this.value = dummy.value;
		this.status = dummy.status;
		this.message = dummy.message;
	}
	
	

	
	


	/**
	 * Generate the XML representation for this message.
	 * @return the XML String
	 */
	public String toXML() {
		StringWriter sw = new StringWriter();
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(KVMessage.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
 
			jaxbMarshaller.marshal(this, sw);
		} catch (JAXBException e) {
			
		}
		return sw.toString();
	}
	
	@XmlElement
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	
	public String getMsgType() {
		return msgType;
	}
	
	@XmlElement
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	@XmlElement
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	@XmlElement
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public boolean getStatus(){
		return status;
	}

	@XmlElement
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage(){
		return message;
	}
}
