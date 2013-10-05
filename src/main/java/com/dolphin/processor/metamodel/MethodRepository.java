package com.dolphin.processor.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dolphin.api.StatusType;

public class MethodRepository {

	/** cache des methods */
	private Map<Integer, Method> _cache;
	
	private static MethodRepository _instance;
	
	private MethodRepository() {
		_cache = new HashMap<Integer, Method>();
	}
	
	public static MethodRepository instance() {
		if(_instance == null) {
			_instance = new MethodRepository();
		}
		return _instance;
	}
	
	
	
	public Method create(String owner, String name) {
		
		final Method newMethod = new Method(owner, name);
		int hashCode = newMethod.hashCode();
		if(!_cache.keySet().contains(hashCode)) {
			_cache.put(hashCode, newMethod);
		}
		return _cache.get(hashCode);
	}
	
	public Collection<Method> findAll() {
		return Collections.unmodifiableCollection(_cache.values());
	}
	
	public int count() {
		return _cache.size();
	}
	
	public Collection<Method> findAllByStatus(StatusType status) {
		List<Method> result = new ArrayList<Method>();
		for(Entry<Integer, Method> entry : _cache.entrySet()) {
			Method method = entry.getValue();
			if(method.getStatus() == status) {
				result.add(method);
			}
		}
		return result;
	}
	
	
}
