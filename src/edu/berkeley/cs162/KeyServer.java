 /**
 * Slave Server component of a KeyValue store
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

import java.io.Serializable;

/**
 * This class defines the salve key value servers. Each individual KeyServer 
 * would be a fully functioning Key-Value server. For Project 3, you would 
 * implement this class. For Project 4, you will have a Master Key-Value server 
 * and multiple of these slave Key-Value servers, each of them catering to a 
 * different part of the key namespace.
 *
 * @param <K> Java Generic Type for the Key
 * @param <V> Java Generic Type for the Value
 */
public class KeyServer<K extends Serializable, V extends Serializable> implements KeyValueInterface<K, V> {
	private KVStore<K, V> dataStore = null;
	private KVCache<K, V> dataCache = null;
	
	/**
	 * @param cacheSize number of entries in the data Cache.
	 */
	public KeyServer(int cacheSize) {
	    dataCache = dataCache(cacheSize);
	    dataStore = new KVStore();
	}
	
	public boolean put(K key, V value) throws KVException {
	    string keyString = KVMessage.marshall(key);
	    string valueString = KVMessage.marshall(key);
	    byte[] size = (keyString).getBytes;
	    if (size.length() > 256)
		throw new KVException(KVMessage("Over sized key", keyString, valueString));
	    if (size == 0)
		throw new KVException(KVMessage("Empty key", keyString, valueString));
	    size = (valueString).getBytes;
	    if (size.length() > 131072)
		throw new KVException(KVMessage("Over sized value", keyString, valueString));
	    if (size == 0)
		throw new KVException(KVMessage("Empty value", keyString, valueString));
	    boolean store = dataStore.put(key,value);
	    boolean cache = dataCache.put(key,value);
	    if (store == true)
		return true;
	    if (store == false && cache == false)
		return false;
	    else throw new KVException(KVMessage("Unknown Error: cache and store not in sync"));
	}
	
	public V get (K key) throws KVException {
		// implement me
		return null;
	}

	@Override
	public void del(K key) throws KVException {
		// implement me		
	}
}


