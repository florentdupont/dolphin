package com.dolphin.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 
 * L'élément peut etre monovalué : 
 * @BusinessRule(id="RG_001", version="1.0")
 * ou multivalué : 
 * @BusinessRule(id={"RG_001", "RG_002"}, version={"1.0", "2.0"})
 * 
 * 
 * @author Florent Dupont
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessRule {
	
    public abstract String[] id() default {};
	public abstract String[] version() default {};
	
}
