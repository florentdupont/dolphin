package com.dolphin.api;

/**
 * Status de développement. 
 * Valeurs utilisées dans les annotations {@link DevelopmentStatus}
 * 
 * @author Florent Dupont
 * @see DevelopmentStatus
 */
public enum StatusType {
	/** A faire */
	TODO,
	
	/** en cours de développement */
	ONGOING, 
	
	/** terminé */
	DONE,
	
	/** terminé et testé */
	TESTED,
	
	/** testé et intégré */
	INTEGRATED
}
