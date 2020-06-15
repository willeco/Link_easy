# Link'easy

Le projet vise à faciliter la recherche des appareils énergivores de la maison en diffusant en temps réel la téléinformation du compteur linky vers votre smartphone. Effectivement, pour interroger son compteur Linky, il est nécessaire de se placer devant l’afficheur et d’utiliser les touches de défilement (+) ou (-) afin de visualiser la puissance apparente.
Nous proposons également de classifier les appareils de votre domicile en fonction de leur consommation individuelle.

## Avant propos

L'application est uniquement disponible sur Android. 

### Prerequis

Pour pouvoir utiliser notre application, vous devez connecter à votre compteur Linky, un module radio capable de diffuser en Wi-Fi, la téléinformation sur votre réseau local domestique. 

### Installation

#### Module Radio
Pour télécharger les fichiers sources en langage Python du module radio, [cliquez ici](https://urlz.fr/cQcs). Notre module radio est constitué d'un raspberry PI connecté en Wi-Fi à notre Box Internet. Ce raspberry PI doit être relié aux bornes I1 et I2 de la téléinformation du compteur Linky via un module de chez GCE Electronics (USB). La communication entre le smarphone et le raspberry s'effectue via l'envoi de datagramme en UDP en utilisant le port 10001. Pour pouvoir utiliser l'application Android, vous devrez lors de son lancement renseigner l'adresse IP du module radio au sein de votre réseau domestique.  
Toutefois, même si vous n'êtes pas à domicile, il vous sera quand même possible de récupérer, en temps réel, la téléinformation du compteur LINKY sur votre smartphone. Pour cela, il vous sera nécessaire d'identifier l'adresse IP extérieure de votre box Internet en utilisant les services d'un site comme celui ci [adresseip](https://adresseip.com/). Ensuite, configurez votre box Internet de manière à ce qu'elle redirige le flux UDP du port 10001 vers votre module radio à partir d'une règle NAT. 

#### Application

Pour installer l'application sur votre smartphone vous pouvez télécharger le fichier [apk](https://urlz.fr/cQcF) du projet.

## Lancer le test

* Avant d'avoir accès aux fonctionnalités de notre application, il vous faut renseigner l'adresse IP de votre module radio (en l'occurence l'adresse IP du Raspberry PI sur le réseau local)

![](https://github.com/ThomasCochou/Link_easy/blob/master/Images%20CR/connexion.png)

* Appuyer sur connexion

La demande de télé-information a été envoyée.
Votre portable est désormais en attente de réception de la consommation de votre domicile.

![30% center](https://github.com/ThomasCochou/Link_easy/blob/master/Images%20CR/connexion_en_cours.png)

Lorsque vous commencez à recevoir l'information de votre compteur Linky, la barre de progression de l'application vous en informe.

![30% center](https://github.com/ThomasCochou/Link_easy/blob/master/Images%20CR/reception.png)

* Plusieurs informations vous sont présentées (exemple : PAPP), vous pouvez appuyer sur ces informations pour voir leurs évolutions en direct à travers un graphique

![](https://github.com/ThomasCochou/Link_easy/blob/master/Images%20CR/puissances.png)
![](https://github.com/ThomasCochou/Link_easy/blob/master/Images%20CR/graph.png)

* Pour obtenir une définition des informations transmises, vous pouvez appuyer sur l'icon ![](https://github.com/ThomasCochou/Link_easy/blob/master/Images%20CR/i.png)

* Enfin, vous avez la possibilté de rechercher la consommation individuelle de chaque appareil dans votre maison en appuyant sur le bouton correspondant

![](https://github.com/ThomasCochou/Link_easy/blob/master/Images%20CR/bouton_devices.png)

### Consommation individuelle de chaque appareil

* Appuyer sur le bouton d'ajout d'appareil
* Choisissez l'appareil souhaité parmis la liste que l'on vous propose (s'il n'est pas présent dans la liste choisisez "autre appareil")
* Suivez le protocole
* Votre appareil est créé
* Si vous désirez modifier les informations de votre appareil, appuyer sur celui-ci
* Modifiez les informations que vous souhaitez, ou supprimez votre appareil

## Informations complémentaires

Pour revenir à une activité précédente, il vous suffit d'utiliser la touche retour en arrière de votre portable.

## Créé Avec

* [AndroidStudio](https://cutt.ly/TyTR5ou) - Notre outil de développement principal pour la plateforme Android
* [GitHub](https://github.com/) - Notre plateforme de collaboration
* [Visual Studio Code](https://code.visualstudio.com/) - Notre éditeur de texte pour le simulateur
* [Java](https://www.java.com/fr/) - Principal langage de programmation du projet
* [Python](https://www.python.org/) - Langage de programmation secondaire du projet
* [InkScape](https://inkscape.org/fr/) - Utilisé pour le design de l'application

## Collaboration

Veuillez lire [CONTRIBUTING.md](https://github.com/ThomasCochou/Link_easy/blob/master/CONTRIBUTING.md) pour plus de détails à propos de notre organisation. 

## Auteur

* **William Le Corre** - [Compte GitHub](https://github.com/willeco)

Voir également la liste des [contributeurs](https://github.com/ThomasCochou/Link_easy/graphs/contributors) qui ont participé au projet.

## Contexte

Ce projet a été réalisé dans le cadre de nos études au sein de l'[ENIB](https://www.enib.fr/fr/) (Ecole Nationale d'Ingénieurs de Brest).

## Remerciements

* Lorette Louvel pour le design de l'application [son book](https://lorettelouvel.myportfolio.com)
