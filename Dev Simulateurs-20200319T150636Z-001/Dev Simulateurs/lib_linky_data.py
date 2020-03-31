#!/usr/bin/env python
# -*-coding:Latin-1 -*


from time      import sleep
import sys
import json

class LinkyData():
   
    # ------------------------------------------------------------------ 
    # Initialisation Class LinkyData 
    # ------------------------------------------------------------------
    def __init__(self, filename = "./Capture-TELEINFO/compteur.txt"):

        try:
            # Ouverture fichier teleinformation 
            fp = open(filename, "r")
            print("[*] Traitement fichier teleinformation : %s" %(filename))

            # Boucle sur les lignes du fichier
            i = 0
            self.list_teleinfo = []
            for line in fp:
                
                # Decoupage de la ligne fu fichier
                x = line.split()
                #print("Ligne %d : %s %s" % (i, x[0], x[1]) )
                
                i += 1
                
                # Creation d'un dictionnaire contenant une donnee téléinfo
                data = {x[0]: x[1]} 
                
                # Conversion du cictionnaire au format JSON:
                y   = json.dumps(data)
                
                # Sauvegarde ligne JSON de teleinformation dans une liste
                self.list_teleinfo.append(y)

            print("[*] Nombre lignes de téléinformation   : %d" % len(self.list_teleinfo) )
  
        except OSError as err:
            print( "[!] OS error: {0}".format(err) )
        except:
            print( "[!] Unexpected error:", sys.exc_info()[0])
            raise

    # -------------------------------------------------------------------------
    # Retourne toutes les données de teleinformation lues sur le fichier
    # -------------------------------------------------------------------------
    def get_teleinfo(self):
        return(self.list_teleinfo) 


# -----------------------------------------------------------------------------
# Test
# ----------------------------------------------------------------------------- 
if __name__ == '__main__':
    print("[*] Test Linky Data")
    data = LinkyData("./Capture-TELEINFO/compteur.txt")
 
