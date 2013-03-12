Dolphin
=======

Dolphin est un plugin **Maven** d'analyse d'annotations de suivi des exigences m�tier dans le code Java.


Annotation du code
------------------

**R�gles m�tier** 

L'annotation ```@BusinessRule``` indique les r�gles m�tiers impl�ment�es sur une m�thode.

Cette annotation porte 2 attributs :

- _id_ repr�sente l'identifiant de la r�gle
- _version_ repr�sente la version de la r�gle.


    @BusinessRule(id="RG_0009", version="0.1")
    public void myMethod() {
      // blabla
    }


Si plusieurs r�gles sont appliqu�es sur une m�thode, alors il faut indiquer les diff�rents
identifiants en sp�cifiant les valeurs multiples entre accolades `{}` :

Dans l'exemple ci dessous, ```myMethod()``` impl�mente la r�gle RG_0009 en version 0.1 et la
r�gle RG_0010 en version 0.2.

    @BusinessRule(id={"RG_0009", "RG_0010"}, version={"0.1", "0.2"})
    public void myMethod() {

    }

**Bonne pratique** : il est pr�f�rable de ne pas cr�er de constantes pour les business
rules. En effet, l'utilisation de constante n'apporte rien quant � la lecture. Par exemple :
@BusinessRule(id=REGLE_00009, version=VERSION_0_1) n'est pas plus lisible que
@BusinessRule(id="RG_0009", version="0.1") De plus, la cr�ation de constante n'aurait pas
sp�cialement de sens en tant que signature de la classe. Elle n'aurait ici qu'un r�le technique.

**Status de d�veloppement**

L'annotation ```@DevelopmentStatus``` permet d'indiquer l'avancement d'un d�veloppement.
L'annotation se porte sur une m�thode ou une classe (ou les deux).

- Sur une classe : toutes les m�thodes de la classe auront le status indiqu� par la classe.
- Sur une m�thode : le status indiqu� sur la m�thode sera prioritaire sur celui de la classe.

Par exemple, une m�thode en cours de d�veloppement :

    @DevelopmentStatus(StatusType.ONGOING)
    public void myMethod() {
    
    }

On peut ainsi avoir une vision g�n�rale, mais sp�cifier le status d'une m�thode en particulier.
C'est le cas dans l'exemple ci dessous : La classe est en status en cours `ONGOING`, c'est �
dire que ```myMethod1()``` prend le status indiqu� sur la classe (donc `ONGOING`), alors que
```myMethod2()``` poss�de le status qui lui est propre (donc `DONE`).

    @DevelopmentStatus(StatusType.ONGOING)
    public class MyClass {
    public void myMethod1() {...}
    
    @DevelopmentStatus(StatusType.DONE)
    public void myMethod2() {...}
    
    }

**Utilisation conjointe**

Bien que les deux annotations peuvent �tre utilis�es ind�pendamment, l'int�r�t est
�videmment de les utiliser conjointement afin de pouvoir b�n�ficier de l'avancement de
l'impl�mentation des r�gles m�tier. L'exemple ci-dessous montre un exemple d'utilisation
conjointe.

    @DevelopmentStatus(StatusType.ONGOING)
    public class MyClass {
    
    @BusinessRule(id="RG_0001", version="1.0")

    public void myMethod1() {...}

    @BusinessRule(id="RG_0016", version="1.0")
    @DevelopmentStatus(StatusType.DONE)
    public void myMethod2() {...}
    
    }

**A savoir**

=> L'outil ne fait qu'une analyse statique et ressort les r�sultats tels quels. Par exemple, il ne
v�rifie pas le formalisme sur les nommages des r�gles, ni les valeurs de versions.

Plusieurs r�gles � suivre :

- Utilisez toujours le formalisme suivant : RG_XXXX pour le nommage des r�gles.
- les annotations sur les m�thodes priv�es ne sont pas prises en compte.
- V�rifiez, apr�s avoir test�, que les @DevelopmentStatus soient bien plac�s en TESTED. (status final)


Utilisation 
------------

Lancement se fait : 

    mvn package -DskipTests=true dolphin:dolphin

Deux possibilit�s de parametrage
 
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

    mvn package -DskipTests=true dolphin:dolphin -PdolphinPrefix=com.mypackage.test


Installation
------------

Pour installer dans le repo local : ```mvn install -DskipTests```

Pour que le plugin soit utilisable en mode pr�fixe, il faut ajouter les lignes suivantes dans le settings.xml du poste local. 

    <pluginGroups>
      <pluginGroup>com.dolphin</pluginGroup>
    </pluginGroups>

Sans ces lignes dans le settings.xml, le plugin serait toutefois appelable avec la commande ```mvn package com.dolphin:dolphin-maven-plugin:dolphin```    