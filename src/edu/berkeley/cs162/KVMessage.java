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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/**
 * This is the object that is used to generate messages the XML based messages 
 * for communication between clients and servers. Data is stored in a 
 * marshalled String format in this object.
 */
public class KVMessage {
	private String msgType = null;
	private String key = null;
	private String value = null;
	private String status = null;
	private String message = null;

	public KVMessage() {
		
	}
	
	public KVMessage(String msgType, String key, String value) {
		this.msgType = msgType;
		this.key = key;
		this.value = value;
	}
	
	// This constructor will handle the Serializable -> String marshalling,
	// and should be the one actually used by KVClient.
	// Will throw DataFormatException if either the key or value are too long.
	public KVMessage(String msgType, Serializable key, Serializable value, Boolean status, String message) {
		
		this.msgType = msgType;
		this.key = marshal(key);
		this.value = marshal(value);
		this.message = message;
		
		
	}
	
	public KVMessage(String msgType, Serializable key, Serializable value, String status, String message) {
		this(msgType, key, value, false, message);
		this.status = status;
	}
	
	/** Read the object from Base64 string. */
    public static Object unmarshal(String s) throws IOException, ClassNotFoundException {
        byte [] data = DatatypeConverter.parseBase64Binary(s);
        //byte [] data = s.getBytes("UTF-8");
    	ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    /** Write the object to a Base64 string. */
    public static String marshal( Serializable o ) {
		if(o == null){
			return null;
		}
	
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
			ObjectOutputStream oos = new ObjectOutputStream( baos );
			oos.writeObject( o );
			oos.close();
		} catch (IOException e) {
		}
        
//        try {
//			return baos.toString("UTF-8");
//        	
//		} catch (UnsupportedEncodingException e) {
//			return "";
//		}
        return DatatypeConverter.printBase64Binary(baos.toByteArray());
    }

	
	/* Hack for ensuring XML libraries does not close input stream by default.
	 * Solution from http://weblogs.java.net/blog/kohsuke/archive/2005/07/socket_xml_pitf.html */
	private class NoCloseInputStream extends FilterInputStream {
	    public NoCloseInputStream(InputStream in) {
	        super(in);
	    }
	    
	    public void close() {} // ignore close
	}
	
	public KVMessage(InputStream input) throws KVException{
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.out.println("throwing exception KVMessage line 145");
			throw new KVException(new KVMessage("resp", null, null, false, "Unknown error: Unable to initialize DocumentBuilder"));
		}
		
		Document d = db.newDocument();
		//Element root = d.createElement("KVMessage");
		
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t;
		
		try {
			t = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			System.out.println("throwing exception KVMessage line 158");
			throw new KVException(new KVMessage("resp", null, null, false, "Unknown error: Unable to initialize Transformer"));
		}
		
		try {
			t.transform(new StreamSource(new NoCloseInputStream(input)), new DOMResult(d));
		} catch (TransformerException e) {
			System.out.println(e);
			throw new KVException(new KVMessage("resp", null, null, false, "XML Error: Received unparseable message"));
		}
		
		Element root = (Element)d.getFirstChild();
		
		msgType = root.getAttribute("type");
		
		Node keyElem = root.getElementsByTagName("Key").item(0);
		if (keyElem != null) {
			key = ((Text)keyElem.getFirstChild()).getWholeText();
//			key = key.substring(9,key.length() - 3);
		}
		
		
		Node valueElem = root.getElementsByTagName("Value").item(0);
		if (valueElem != null) {
			value = ((Text)valueElem.getFirstChild()).getWholeText();
//			value = value.substring(9,key.length() - 3);
		}
		
		Node statusElem = root.getElementsByTagName("Status").item(0);
		if (statusElem != null) status = ((Text)statusElem.getFirstChild()).getWholeText();
		
		Node messageElem = root.getElementsByTagName("Message").item(0);
		if (messageElem != null) message = ((Text)messageElem.getFirstChild()).getWholeText();
		
//		value = ((Text)root.getElementsByTagName("Value").item(0).getFirstChild()).getWholeText();
//		status = Boolean.getBoolean(((Text)root.getElementsByTagName("Status").item(0).getFirstChild()).getWholeText());
//		message = ((Text)root.getElementsByTagName("Message").item(0).getFirstChild()).getWholeText();
	}
	
	

	
	


	/**
	 * Generate the XML representation for this message.
	 * @return the XML String
	 */
	public String toXML() throws KVException {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.out.println("throwing exception KVMessage line 214");
			throw new KVException(new KVMessage("resp", null, null, false, "Unknown error: Unable to initialize DocumentBuilder"));
		}
		Document d = db.newDocument();
		Element root = d.createElement("KVMessage");
		root.setAttribute("type", msgType);
		d.appendChild(root);
		if (key != null) {
			Element keyNode = d.createElement("Key");
			keyNode.appendChild(d.createCDATASection(key));
			root.appendChild(keyNode);
		}
		if (value != null) {
			Element valueNode = d.createElement("Value");
			valueNode.appendChild(d.createCDATASection(value));
			root.appendChild(valueNode);
		}
		if (status != null) {
			Element statusNode = d.createElement("Status");
			statusNode.appendChild(d.createTextNode(status));
			root.appendChild(statusNode);
		}
		if (message != null) {
			Element messageNode = d.createElement("Message");
			messageNode.appendChild(d.createCDATASection(message));
			root.appendChild(messageNode);
		}
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t;
		
		try {
			t = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			System.out.println("throwing exception KVMessage line 245");
			throw new KVException(new KVMessage("resp", null, null, false, "Unknown error: Unable to initialize Transformer"));
		}
		
		StringWriter sw = new StringWriter();
		//ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String ret;
		try {
			//t.transform(new DOMSource(root), new StreamResult(baos));
			t.transform(new DOMSource(root), new StreamResult(sw));
		} catch (TransformerException e) {
			System.out.println("throwing exception KVMessage line 256");
			throw new KVException(new KVMessage("resp", null, null, false, "Unknown error: Unable to generate XML"));
		}
		ret = sw.toString();
//		try {
//			ret = baos.toString("UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			return "";
//		}
		
		//ret = DatatypeConverter.printBase64Binary(baos.toByteArray());
		
//		ret.replace("&", "&amp;");
//		ret.replace("<", "&lt;");
//		ret.replace(">", "&gt;");
//		ret.replace("\"", "&quot;");
//		ret.replace("'", "&apos;");
//		ret = ret.replace("<Key>", "<Key><![CDATA[");
//		ret = ret.replace("</Key>", "]]></Key>");
//		ret = ret.replace("<Value>", "<Value><![CDATA[");
//		ret = ret.replace("</Value>", "]]></Value>");
		return ret;
		//return baos.toString();
		
		
	}
	
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	
	public String getMsgType() {
		return msgType;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setStatus(boolean status) {
		if (status) this.status = "True";
		else this.status = "False";
	}
	
	public boolean getStatus(){
		return Boolean.parseBoolean(status);
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage(){
		return message;
	}
	
	public String toString() {
		return "{msgType = "+msgType+", key = "+key+", value ="+value+", status = "+status+", message = "+message+"}";
	}
}
