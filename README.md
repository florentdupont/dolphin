dolphin
=======

Business Rules annotation checker

**Plugin Maven**

Utilisation 
------------

Lancement se fait : 
   mvn package -DskipTests=true com.dolphin:dolphin:dolphin

Deux possibilit� de parametrage
 
Avec Configuration Maven : a d�finir dans le POM du projet que l'on souhaite analyser
 
   <plugin>
 	 <groupId>com.dolphin</groupId>
 		<artifactId>dolphin</artifactId>
 		<version>1.0</version>
 		 <configuration>
   		<logpath>/Users/flo/projets/annotations/export</logpath>
   		<namespaceprefix>com.mypackage.test</namespaceprefix>
   	</configuration>
   </plugin>
  
Sans configuration Maven :
Il faut dans ce cas d�finir les variables en ligne de commande

   mvn package -DskipTests=true com.dolphin:dolphin:dolphin -PdolphinPrefix=com.mypackage.test


