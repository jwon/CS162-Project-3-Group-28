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

import java.util.*;
import java.io.*;
import java.util.zip.DataFormatException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.TransformerFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * This is the object that is used to generate messages the XML based messages 
 * for communication between clients and servers. Data is stored in a 
 * marshalled String format in this object.
 */
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
	public KVMessage(String msgType, Serializable key, Serializable value, boolean status, String message) throws DataFormatException{
		this.msgType = msgType;
		this.key = marshall(key);
		this.value = marshall(value);
		this.status = status;
		this.message = message;
		
		if (this.key.length() > 256)
			throw new DataFormatException("Over sized key");
		if (this.value.length() > 128 << 10) // if value longer than 128KiB
			throw new DataFormatException("Over sized value");
	}
	
	/** Read the object from Base64 string. */
    public static Object marshall( String s ) throws IOException ,
                                                        ClassNotFoundException {
        byte [] data = Base64Coder.decode( s );
        ObjectInputStream ois = new ObjectInputStream( 
                                        new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    /** Write the object to a Base64 string. */
    public static String unmarshall( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return new String( Base64Coder.encode( baos.toByteArray() ) );
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
		XMLReader xr;
		xr = new XMLFilterImpl();
		
		try {
			xr.parse(new InputSource(input));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		} catch (SAXException e) {
			// TODO Auto-generated catch block
		}
	}
	
	private class MyParser extends XMLFilterImpl { 
		private String elementString;
		
		
		private MyParser() {
			super();
		}
		
		@Override
		public void fatalError(SAXParseException e)
                throws SAXException {
			
		}


		@Override
		public void startElement(String uri,
                String localName,
                String qName,
                Attributes attributes)
         throws SAXException {
			elementString = null; // necessary?
			if (localName.equals("KVMessage")) {
				msgType = attributes.getValue(0); // Get the first attribute, which is the only attribute, which is "type
			}
		}
		
		@Override
		public void characters(char[] ch,
                int start,
                int length)
         throws SAXException {
			elementString = new String(ch, start, length);
			
			
		}
		
		@Override
		public void endElement(String uri,
                String localName,
                String qName)
         throws SAXException {
			switch(localName) {
			
			case "Key":
				key = elementString;
			case "Value":
				value = elementString;
			case "Message":
				message = elementString;
			case "Status":
				status = Boolean.getBoolean(elementString);
			}
		}
	}

	
	


	/**
	 * Generate the XML representation for this message.
	 * @return the XML String
	 */
	public String toXML() {
		// implement me
		return null;
	}
	
	public String getMsgType() {
		return msgType;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	public boolean getStatus(){
		return status;
	}

	public String getMessage(){
		return message;
	}
}
