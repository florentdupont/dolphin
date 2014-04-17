package com.dolphin.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 
 * L'annotation @BusinessRule indique les règles métiers implémentées sur une méthode.
 * 
 * Cette annotation porte 2 attributs : 
 * - id représente l'identifiant de la règle 
 * - version représente la version de la règle.
 * 
 * Par exemple :
 * 
 * @BusinessRule(id="RG_0009", version="0.1") 
 * public void myMethod() { 
 *   // blabla 
 * } 
 * 
 * Si plusieurs règles sont appliquées sur une méthode, alors il faut indiquer les 
 * différents identifiants en spécifiant les valeurs multiples entre accolades {} :
 * 
 * Dans l'exemple ci dessous, myMethod() implémente la règle RG_0009 en version 0.1 et 
 * la règle RG_0010 en version 0.2.
 * 
 * @BusinessRule(id={"RG_0009", "RG_0010"}, version={"0.1", "0.2"}) 
 * public void myMethod() { 
 *   ... 
 * }
 * 
 * @author Florent Dupont
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessRule {
	
	 /** identifiants des règles métier */
    public String[] id() default {};
    
    /** versions des règles métiers */
	public String[] version() default {};
	
}
