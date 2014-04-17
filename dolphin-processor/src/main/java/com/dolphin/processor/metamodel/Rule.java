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
public class Rule {

	/** name */
	String _name;
	
	/** version */
	String _version;
	
	/** implementedBy - derived attribute*/
	List<Method> _implementedBy;
	
	/** use repository to create a new Rule */
	Rule(String name, String version) {
		_name = name;
		_version = version;
		_implementedBy = new ArrayList<Method>();
	}
	
	void notifyIsImplementedBy(Method method) {
		if(!_implementedBy.contains(method)) {
			_implementedBy.add(method);
		}
	}
	
	public String getName() {
		return _name;
	}
	
	public String getVersion() {
		return _version;
	}
	
	
	public List<Method> getImplementedBy() {
		return Collections.unmodifiableList(_implementedBy);
	}

	/** retourne le statut général d'avancement de cette règle.*/
	public StatusType getGeneralStatus() {
		if(_implementedBy.size() == 1) {
			return _implementedBy.get(0)._status;
		}
		else {
			
			int highestLevel = StatusType.TODO.ordinal();
			int lowestLevel = StatusType.INTEGRATED.ordinal();
			
			// j'itère sur tous les status
			for(Method m : _implementedBy) {
				if(m.getStatus().ordinal() < lowestLevel) {
					lowestLevel = m.getStatus().ordinal();
				}
				if(m.getStatus().ordinal() > highestLevel) {
					highestLevel = m.getStatus().ordinal();
				}
			}
			
			// le seuil est sur ONGOING.  s'ils chevauchent le ONGOING, alors l'indicateur global est ONGOING.
			// si tous les indicateurs sont en TODO, alors le status est en TODO.
			// sinon, ils prennent l'indicateur le plus bas (ie. DONE, TESTED ou INTEGRATED);
			if(lowestLevel <= StatusType.ONGOING.ordinal() && highestLevel >= StatusType.ONGOING.ordinal())
				return StatusType.ONGOING;
			if(lowestLevel == StatusType.TODO.ordinal() && highestLevel == StatusType.TODO.ordinal())
				return StatusType.TODO;
			else 		
			    return StatusType.values()[highestLevel];
		}
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
		result = prime * result
				+ ((_version == null) ? 0 : _version.hashCode());
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
		Rule other = (Rule) obj;
		if (_name == null) {
			if (other._name != null)
				return false;
		} else if (!_name.equals(other._name))
			return false;
		if (_version == null) {
			if (other._version != null)
				return false;
		} else if (!_version.equals(other._version))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Rule [name=" + _name + ", version=" + _version + "]";
	}
	
}
