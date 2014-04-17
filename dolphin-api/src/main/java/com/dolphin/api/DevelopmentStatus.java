package com.dolphin.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * L'annotation @DevelopmentStatus permet d'indiquer l'avancement d'un développement. 
 * L'annotation porte sur une méthode.
 *
 * Par exemple, une méthode en cours de développement :
 * 
 * @DevelopmentStatus(StatusType.ONGOING)
 * public void myMethod() {
 *   ...    
 * }
 * 
 * Il existe 5 types de statut :
 * - StatusType.TODO       : méthode à implémenter
 * - StatusType.ONGOING    : méthode en cours de développement
 * - StatusType.DONE       : méthode implémentée, mais pas encore testée
 * - StatusType.TESTED     : méthode testée unitairement. Passe les tests unitaires
 * - StatusType.INTEGRATED : méthode testée en intégration. Passe les tests d'intégration.
 *  
 * @author Florent Dupont
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
