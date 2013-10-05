package com.dolphin.processor.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EXPERIMENTAL : repository des règles.
 * @author fdupont
 *
 */
public final class RuleRepository {

	/** cache des règles */
	private Map<Integer, Rule> _cache;
	
	private static RuleRepository _instance;
	
	private RuleRepository() {
		_cache = new HashMap<Integer, Rule>();
	}
	
	public static RuleRepository instance() {
		if(_instance == null) {
			_instance = new RuleRepository();
		}
		return _instance;
	}
	
	
	/** creer une nouvelle règle et l'ajoute au repository */
	public Rule create(String name, String version) {
		final Rule newRule = new Rule(name, version);
		int hashCode = newRule.hashCode();
		if(!_cache.containsKey(hashCode)) {
			_cache.put(hashCode, newRule);
		}
		return _cache.get(hashCode);
	}
	
	/** Retourne toutes les règles */
	public Collection<Rule> findAll() {
		return Collections.unmodifiableCollection(_cache.values());
	}
	
}
