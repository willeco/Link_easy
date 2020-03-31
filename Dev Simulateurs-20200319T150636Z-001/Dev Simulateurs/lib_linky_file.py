#!/usr/bin/env python
# -*-coding:Latin-1 -*


from time      import sleep
import sys
import json

# ------------------------------------------------------------------ 
# convert_teleinfo_line_to_json 
# ------------------------------------------------------------------
def convert_teleinfo_line_to_json(line):

    # Decoupage de la ligne du fichier
    x               = line.split()
    #print("Ligne %d : %s %s" % (i, x[0], x[1]) )
    
    if len(x) > 1:
        # Creation d'un dictionnaire contenant une donnee téléinfo
        data            = {x[0]: x[1]} 
        
        # Conversion du dictionnaire au format JSON:
        json_teleinfo   = json.dumps(data)
    else:

        json_teleinfo   = ""

    return(json_teleinfo)


class LinkyData():

    # https://stackoverflow.com/questions/287871/how-to-print-colored-text-in-terminal-in-python
    HEADER      = '\033[95m'
    OKBLUE      = '\033[94m'
    OKGREEN     = '\033[92m'
    WARNING     = '\033[93m'
    FAIL        = '\033[91m'
    ENDC        = '\033[0m'
    BOLD        = '\033[1m'
    UNDERLINE   = '\033[4m'
   
    # ------------------------------------------------------------------ 
    # Initialisation Class LinkyData 
    # ------------------------------------------------------------------
    def __init__(self, filename = "./Capture-TELEINFO/compteur.txt"):

        try:
            # Ouverture fichier teleinformation 
            fp = open(filename, "r")
            print("[*] Read teleinformation file : %s" %(filename))

            # Boucle sur les lignes du fichier

            self.list_teleinfo = []
            for line in fp:
                
                y = convert_teleinfo_line_to_json(line) 
                if y != "":
                    # Sauvegarde teleinformation dans une liste
                    self.list_teleinfo.append(y)

            print("[*] Number of teleinfo in file : %d" % len(self.list_teleinfo) )
  
        except OSError as err:
            print( LinkyData.FAIL + "[!] OS error: {0}".format(err) )
        except:
            print( LinkyData.FAIL + "[!] Unexpected error:", sys.exc_info()[0])
            raise

    # -------------------------------------------------------------------------
    # Retourne la lsite des données de teleinformation lues sur le fichier
    # -------------------------------------------------------------------------
    def get_teleinfo(self):
        return(self.list_teleinfo) 


# -----------------------------------------------------------------------------
# Test
# ----------------------------------------------------------------------------- 
if __name__ == '__main__':
    print("[*] Test Linky Data")
    data = LinkyData("./Capture-TELEINFO/compteur.txt")
 
