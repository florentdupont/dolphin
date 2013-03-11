package com.dolphin.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Annotation de status de dévelopment.
 * Placé sur une classe, il indique le suivi du développement global pour toutes les méthodes
 * => Toutes les méthodes.
 * 
 * Placé sur une méthode, il indique le status spécifique de cette méthode.
 * 
 * Ainsi : On peut placer une classe : 
 * @DevelopmentStatus(ONGOING)
 * class Toto {
 * 
 * 	@DevelopmentStatus(DONE)
 *  public maMethod()
 *  
 *  public maMethod2()
 * 
 * }
 * 
 * => maMethod2 aura un status ONGOING, hérité de la classe.
 * => maMathod aura un status DONE, indiqué sur la méthode.
 * 
 * @author Florent Dupont
 *
 */
// applicable sur Class, Method, partout quoi..
// mais nous on le vérifiera seulement sur les classes et methodes
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DevelopmentStatus {
	
	/**
	 * @return le status de développement.
	 * @see StatusType
	 */
	public abstract StatusType value();
	
}
