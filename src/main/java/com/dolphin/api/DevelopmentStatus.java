package com.dolphin.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Annotation de status de dévelopment.
 * 
 * Placé sur une méthode, il indique le status spécifique de cette méthode.
 * 
 * class Toto {
 * 
 * 	@DevelopmentStatus(DONE)
 *  public maMethod()
 *  
 *  public maMethod2()
 * 
 * }
 * 
 * => maMethod2 aura aucun status.
 * => maMathod aura un status DONE.
 * 
 * @author Florent Dupont
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DevelopmentStatus {
	
	/**
	 * @return le status de développement.
	 * @see StatusType
	 */
	public StatusType value();
	
}
