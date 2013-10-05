Dolphin
=======

Dolphin est un plugin **Maven** d'analyse d'annotations de suivi des exigences métier dans le code Java.
Il permet d'assurer la traçabilité de règles métier entre les SFD et le code. Il permet de connaitre quelle méthode implémente une règle métier particulère et le niveau d'avancement de celle-ci.

Une démo en live du résultat est disponible à cette adresse : http://dolphindemo.flo.cloudbees.net/

Annotation du code
------------------

**Règles métier** 

L'annotation ```@BusinessRule``` indique les règles métiers implémentées sur une méthode.

Cette annotation porte 2 attributs :

- _id_ représente l'identifiant de la règle
- _version_ représente la version de la règle.

Par exemple :
```java
@BusinessRule(id="RG_0009", version="0.1")
public void myMethod() {
  // blabla
}
```

Si plusieurs règles sont appliquées sur une méthode, alors il faut indiquer les différents
identifiants en spécifiant les valeurs multiples entre accolades `{}` :

Dans l'exemple ci dessous, ```myMethod()``` implémente la règle RG_0009 en version 0.1 et la
règle RG_0010 en version 0.2.

```java
@BusinessRule(id={"RG_0009", "RG_0010"}, version={"0.1", "0.2"})
public void myMethod() {
...
}
```

**Bonne pratique** : il est préférable de ne pas créer de constantes pour les business
rules. En effet, l'utilisation de constante n'apporte rien quant à la lecture. Par exemple :
@BusinessRule(id=REGLE_00009, version=VERSION_0_1) n'est pas plus lisible que
@BusinessRule(id="RG_0009", version="0.1") De plus, la création de constante n'aurait pas
spécialement de sens en tant que signature de la classe. Elle n'aurait ici qu'un rôle technique.

**Status de développement**

L'annotation ```@DevelopmentStatus``` permet d'indiquer l'avancement d'un développement.
L'annotation porte sur une méthode.

Par exemple, une méthode en cours de développement :

```java
@DevelopmentStatus(StatusType.ONGOING)
public void myMethod() {
...    
}
```

Il existe 5 types de statut : 

* TODO : méthode à implémenter
* ONGOING : méthode en cours de développement
* DONE : méthode implémentée, mais pas encore testée
* TESTED : méthode testée unitairement. Passe les tests unitaires
* INTEGRATED : méthode testée en intégration. Passe les tests d'intégration.

**Utilisation conjointe**

Bien que les deux annotations peuvent être utilisées indépendamment, l'intérêt est
évidemment de les utiliser conjointement afin de pouvoir bénéficier de l'avancement de
l'implémentation des règles métier. L'exemple ci-dessous montre un exemple d'utilisation
conjointe.

```java
public class MyClass {
    
    @BusinessRule(id="RG_0001", version="1.0")

    public void myMethod1() {...}

    @BusinessRule(id="RG_0016", version="1.0")
    @DevelopmentStatus(StatusType.DONE)
    public void myMethod2() {...}
    
}
```


**A savoir**

=> L'outil ne fait qu'une analyse statique et ressort les résultats tels quels. Par exemple, il ne
vérifie pas le formalisme sur les nommages des règles, ni les valeurs de versions.

Plusieurs règles à suivre :

- Utilisez toujours le formalisme suivant : RG_XXXX pour le nommage des règles.
- les annotations sur les méthodes privées ne sont pas prises en compte.
- Vérifiez, après avoir testé, que les @DevelopmentStatus soient bien placés en TESTED. (statut final)


Utilisation 
------------

Lancement se fait : 

```
mvn package -DskipTests=true dolphin:dolphin
```

Deux possibilités de parametrage
 
Avec Configuration Maven : a définir dans le POM du projet que l'on souhaite analyser

```xml
<plugin>
  <groupId>com.dolphin</groupId>
  <artifactId>dolphin</artifactId>
  <version>1.1</version>
  <configuration>
    <path>/Users/flo/projets/annotations/export</path>
    <namespaceprefix>com.mypackage.test</namespaceprefix>
    <type>all</type>
    <debug>true</debug>
   </configuration>
</plugin>
```  

Sans configuration Maven :
Il faut dans ce cas définir les variables en ligne de commande

    mvn package -DskipTests=true dolphin:dolphin -DdolphinPrefix=com.mypackage.test -DdolphinPath=/var/tmp -DdolphinType=all -DdolphinDebug=true


Installation
------------

Pour installer dans le repo local : ```mvn install -DskipTests```

Pour que le plugin soit utilisable en mode préfixe, il faut ajouter les lignes suivantes dans le settings.xml du poste local. 

```xml
<pluginGroups>
  <pluginGroup>com.dolphin</pluginGroup>
</pluginGroups>
```

Sans ces lignes dans le settings.xml, le plugin serait toutefois appelable avec la commande ```mvn package com.dolphin:dolphin-maven-plugin:1.4:dolphin```    
