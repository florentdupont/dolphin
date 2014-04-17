package com.dolphin.processor.metamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dolphin.api.StatusType;


/**
* EXPERIMENTAL
* metamodel
*
**/
public class Method implements Comparable<Method> {

	/** name */
	String _name;
	
	/** owner : cf classname*/
	String _owner;
	
	/** status */
	StatusType _status;
	
	/** implementedRules*/
	List<Rule> _implementedRules;
	
	
	Method(String owner, String name) {
		_owner = owner;
		_name = name;
		_implementedRules = new ArrayList<Rule>();
		_status = StatusType.TODO;
	}
	
	
	public String getName() {
		return _name;
	}
	
	public String getOwner() {
		return _owner;
	}
	
	public StatusType getStatus() {
		return _status;
	}
	
	public void setStatus(StatusType status) {
		_status = status;
	}
	
	
	/** Ajoute une règle a cette méthode */
	public void addRule(Rule rule) {
		System.out.println("adding " + rule + " to " + this);
		_implementedRules.add(rule);
		
		// create inverse link
		rule.notifyIsImplementedBy(this);
	}
	
	/** */
	public boolean hasImplementedRule() {
		return !_implementedRules.isEmpty();
	}
	
	
	public List<Rule> getImplementedRules() {
		return Collections.unmodifiableList(_implementedRules);
	}


	@Override
	public int compareTo(Method other) {
		return _owner.compareTo(other._owner);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
		result = prime * result + ((_owner == null) ? 0 : _owner.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Method other = (Method) obj;
		if (_name == null) {
			if (other._name != null)
				return false;
		} else if (!_name.equals(other._name))
			return false;
		if (_owner == null) {
			if (other._owner != null)
				return false;
		} else if (!_owner.equals(other._owner))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "Method [owner=" + _owner + ", name=" + _name + ", status=" + _status + "]";
	}
	
	
	
	
	
}
