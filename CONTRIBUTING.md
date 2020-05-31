# Organisation

Vous pourrez trouver ici, toutes les informations concernant notre organisation pour chaque activité ainsi que la contribution de chacun des membres dans ce projet.

## Communication entre le Linky et le portable
 
Etant des élèves de troisième année à l'ENIB, il nous a fallut nous familiariser avec les notions de communications UDP avant même de commencer ce projet. Lorsque nous étions plus à l'aise avec ces notions nous avons commencé à faire nos tests à travers des simulations en python. Nous avons choisis le langage python car nous avions un Raspberry Pi en guise de module radio.

## Premiers pas sur Android Studio

Nous avons débuter notre application par une unique activité "HubActivity" qui s'appellait alors "MainActivity".

### HubActivity

Cette activité permet à l'utilisateur de voir les informations du compteur Linky en direct sur son portable à travers 4 catégories : 

* la PAPP ou puissance apparente
* l'intensité instantannée
* le type de forfait que vous possedez (forfait de base ou forfait heures/pleines heures creuses)
* le PTEC

L'utilisateur peut également visualiser ces informations sous forme de graphiques en cliquant sur les cases PAPP ou BASE.

Nous nous sommes ensuite partagé les taches pour finir le projet dans les délais.

## Repartition des taches

L'activité "DeviceActivity" étant plus conséquante à réaliser, nous nous y sommes consacré à deux :
* William LE CORRE [Compte GitHub](https://github.com/willeco)
* Thomas COCHOU [Compte GitHub](https://github.com/ThomasCochou)

L'activité "QuickConfig" a été réalisé par :
* William LE CORRE [Compte GitHub](https://github.com/willeco)

L'activité Graph a été réalisé par :
* Thomas COCHOU [Compte GitHub](https://github.com/ThomasCochou)

L'activité "DailyConsumption" (pas encore présente dans l'application Android mais en simulation) a été réalisé par :
* Damien CRENN [Compte GitHub](https://github.com/Damiencrenn29)

Pour finir l'ergonomie de l'application a été pensé et réalisé par :
* David LE SAOUT [Compte GitHub](https://github.com/DavidLeSaout)

## Contribution 

### DeviceActivity

Le principe de cette activité est de detecter la consommation de chaque appareils dans votre domicile. Pour ce faire l'activité vous propose d'ajouter des appareils à cette liste. 
Nous vous proposons une méthode pour lui affecter :
* une puissance active qui correspond à sa puissance lorsqu'il est en marche
* une puissance passive qui correspond à sa puissance lorsqu'il n'est pas en marche mais tout de même branché à votre réseau éléctrique
* une puissance moyenne calculée en fonction du taux d'utilisation de votre appareil.

Bien entendu, cette méthode n'est pas infaillible, c'est pourquoi nous vous laissons la possibilité de modifier la puissance active et passive de chaque appareils.

#### William LE CORRE

* création de l'activité
* création des différents appareils
* création du popup d'ajout d'ajout d'appareils
* gestion d'affectation de l'appareil dans la base de données en fonction de la selection
* affichage dynamique des appareils créés
* interaction avec les appareils créés

#### Thomas COCHOU

* création des protocoles d'affectation de puissances
* création de la base de données
* demande et récuperation de la télé-information

### QuickConfig 

* coresspondance entre l'appareil selectionné dans DeviceActivity et celle-ci
* enregistrement des modifications de l'appareil dans la base de données
* mise à jour de la puissance moyenne
* maintien de l'icon associé à l'appareil apres modification du nom
* suppression de l'appareil
