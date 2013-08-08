package com.dolphin.processor;


class Method {
	String _classname;
	String _methodname;

	Method(String classname, String methodname) {
		this._classname = classname;
		this._methodname = methodname;
	}

	@Override
	public boolean equals(Object obj) {
		Method other = (Method)obj;
		return other._classname.equals(_classname) && other._methodname.equals(_methodname);
	}

	@Override
	public int hashCode() {
		return _classname.hashCode() + _methodname.hashCode();
	}
}