package com.dolphin.processor;

import java.util.ArrayList;
import java.util.List;

import com.dolphin.api.StatusType;

/**
 * Une ligne du rapport
 * @author flo
 */
class Line implements Comparable<Line>{
	Method _method;
	List<Rule> _rules = new ArrayList<Rule>();
	StatusType _status;

	Line(Method method, String rule, String version) {
		_method = method;
		_rules.add(new Rule(rule, version));
	}

	Line(Method method) {
		_method = method;
	}

	Line(Method method, StatusType status) {
		_method = method;
		_status = status;
	}

	@Override
	public int compareTo(Line o) {
		return _method._classname.compareTo(o._method._classname);
	}

}
