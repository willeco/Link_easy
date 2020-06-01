# Organisation

Vous pourrez trouver ici, toutes les informations concernant notre organisation pour chaque activité ainsi que la contribution de chacun des membres dans ce projet.

## Communication entre le Linky et le portable
 
Etant étudiants en troisièmes année à l'ENIB, il nous a fallu nous familiariser avec les notions de communications UDP avant même de commencer ce projet. Lorsque nous nous sommes sentis plus à l'aise avec ces notions, nous avons commencé à faire nos tests à travers des simulations en python. Nous avons choisi le langage python car nous avions un Raspberry Pi en guise de module radio.

## Premiers pas sur Android Studio

Nous avons débuté notre application par une unique activité "HubActivity" qui s'appelait alors "MainActivity".

### HubActivity

Cette activité permet à l'utilisateur de voir les informations du compteur Linky en direct sur son portable à travers 4 catégories : 

* la PAPP ou puissance apparente
* l'intensité instantannée
* le type de forfait que vous possédez (forfait de base ou forfait heures pleines/heures creuses)
* le PTEC

L'utilisateur peut également visualiser ces informations sous forme de graphiques en cliquant sur les cases PAPP ou BASE.

Nous nous sommes ensuite partagés les tâches pour finir le projet dans les temps impartis.

## Repartition des taches

L'activité "DeviceActivity" étant plus conséquante à réaliser, nous nous y sommes consacrés à deux :
* William LE CORRE [Compte GitHub](https://github.com/willeco)
* Thomas COCHOU [Compte GitHub](https://github.com/ThomasCochou)

L'activité "QuickConfig" a été réalisée par :
* William LE CORRE [Compte GitHub](https://github.com/willeco)

L'activité Graph a été réalisée par :
* Thomas COCHOU [Compte GitHub](https://github.com/ThomasCochou)

L'activité "DailyConsumption" (pas encore présente dans l'application Android mais en simulation) a été réalisée par :
* Damien CRENN [Compte GitHub](https://github.com/Damiencrenn29)

Pour finir l'ergonomie de l'application a été pensée et réalisée par :
* David LE SAOUT [Compte GitHub](https://github.com/DavidLeSaout)

## Contribution 

### DeviceActivity

Le principe de cette activité est de détecter la consommation de chaque appareil dans votre domicile. Pour ce faire, l'activité vous propose d'ajouter des appareils à cette liste. 
Nous vous proposons une méthode pour lui affecter :
* une puissance active qui correspond à sa puissance lorsqu'il est en marche
* une puissance passive qui correspond à sa puissance lorsqu'il n'est pas en marche mais tout de même branché à votre réseau éléctrique
* une puissance moyenne calculée en fonction du taux d'utilisation de votre appareil.

Bien entendu, cette méthode n'est pas infaillible : c'est pourquoi nous vous laissons la possibilité de modifier la puissance active et passive de chaque appareil.

#### William LE CORRE

* création de l'activité
* création des différents appareils
* création du pop up d'ajout d'appareils
* gestion d'affectation de l'appareil dans la base de données en fonction de la sélection
* affichage dynamique des appareils créés
* interaction avec les appareils créés

#### Thomas COCHOU

* création des protocoles d'affectation de puissances
* création de la base de données
* demande et récuperation de la télé-information

### QuickConfig 

* création de l'activité
* correspondance entre l'appareil selectionné dans DeviceActivity et celle-ci
* enregistrement des modifications de l'appareil dans la base de données
* mise à jour de la puissance moyenne
* maintien de l'icon associé à l'appareil après modification du nom
* suppression de l'appareil

### Graph 

* création de l'activité
* récuperation de la télé-information
* tracer en direct l'information recupérée

### DailyConsumption 

* création d'un nouveau thread de communication
* stockage de la consommation qu'en cas de besoin
* envoi de la liste des consommations au portable lors d'une demande

### Ergonomie 

* choix du thème principal de l'application (noir, gris, vert)
* design des pages de l'application sur Adobe XD
* choix des affichages du menu principal
* création d'un background dégradé gris/noir afin d'économiser de la batterie
* implémentation du design dans le code XML
* création de tutoriel pour faciliter l'utilisation de l'application
* simplifier au maximum l'utilisation des différents boutons
