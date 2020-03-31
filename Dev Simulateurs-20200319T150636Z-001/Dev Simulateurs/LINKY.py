#!/usr/bin/env python
# -*-coding:Latin-1 -*

from threading import Thread
from time      import sleep

try:
    try:
        from tkinter import *
    except:
        from Tkinter import *
except:
    raise ImportError('Wrapper Tk non disponible')
import sys
import json 
import lib_linky_file
import lib_linky_serial
import lib_UDP
import time
import argparse

#from PIL import ImageTk, Image



class InterfaceLinky(Frame):
   
    # ------------------------------------------------------------------ 
    # Initialisation Interface : Création Widgets
    # ------------------------------------------------------------------
    def __init__(self, fenetre, mode_simulator = False):

        Frame.__init__(self, fenetre, width=500, height=500)
        fenetre.title('Compteur Linky')

        self.simulator      = mode_simulator

        
        if self.simulator == True:
            # Prépation données linky issues d'un fichier texte (mode simulateur)
            self.linky_file     = lib_linky_file.LinkyData()   
            self.linky_serial   = None
        else:
            # Préparation interface série si module USB connecté (mode temps réel)
            self.linky_serial   = lib_linky_serial.LinkySerial()
            self.linky_file     = None
            
        # Préparation classe UDP pour l'envoi par voie radio 
        self.udp            = lib_UDP.LinkyUDP()

        # Creation des widgets de l'interface Homme Machine
        self.creation_widgets()

        # Création thread (processus) permettant de gérer les communications UDP
        # Notamment l'attente d'une demande de transmisson de téléinformation
        # Puis l'envoi des données de téléinformation vers le smartphone
        self._thread = None
        self.creation_thread()

    # ------------------------------------------------------------------ 
    # Création des Widgets
    # ------------------------------------------------------------------
    def creation_widgets(self):

        self.entry_width                    = 17

        # Création des variables interface Homme Machine 
        self.var_ip_erlinky                 = StringVar()
        self.var_ip_phone                   = StringVar()
        self.var_port_ER2phone              = StringVar()
        self.var_port_phone2ER              = StringVar()
        self.var_mise_en_veille             = IntVar()
        self.var_puissance_apparente        = StringVar()
        self.var_index_heure_creuse         = StringVar()
        self.var_index_heure_pleine         = StringVar()
        self.var_label_teleinfo             = StringVar()
        self.var_value_teleinfo             = StringVar()
        self.var_time                       = StringVar()

        # Affectation des variables de l'IHM par défault
        ip_host                             = self.udp.get_ip_host()
        self.var_ip_erlinky.set( ip_host )
        self.var_ip_phone.set(                  "localhost")
        self.var_port_ER2phone.set(             "?" )
        self.var_port_phone2ER.set(             self.udp.get_port_phone2ER() )
        self.var_puissance_apparente.set(       "?")
        self.var_index_heure_creuse.set(        "?")
        self.var_index_heure_pleine.set(        "?")
        self.var_mise_en_veille.set(            1)
        self.var_time.set(                      0)

        # Creation Label : Etat de connection et Affichage Label dans la fenetre
        self.widget_label_etat_connection   = Label(self, text="Non connecté à la livebox")
        
        # Creation Entry et Label : Adresse IP et Port de Destination
        self.widget_label_reseau            = Label(self, text="Configuration réseau"      )
        self.widget_label_ip_erlinky        = Label(self, text="@ IP module radio") 
        self.widget_label_ip_phone          = Label(self, text="@ IP smarphone") 
        self.widget_label_port_ER2phone     = Label(self, text="Port ER vers smarphone") 
        self.widget_label_port_phone2ER     = Label(self, text="Port smarphone vers ER") 
        self.widget_label_time              = Label(self, text="Temps écoulé")
        self.widget_label_parametres        = Label(self, text="Paramètres"      )
        self.widget_label_mise_en_veille    = Label(self, text="Mise en veille (minutes)") 

        self.widget_label_teleinfo          = Label(self, text="Télé-information")
        self.widget_label_papp              = Label(self, text="Puissance apparente (V.A)") 
        self.widget_label_index_hc          = Label(self, text="Index heures creuses (H.C)") 
        self.widget_label_index_hp          = Label(self, text="Index heures pleines (H.P)")
        self.widget_label_etat              = Label(self, text="Etat")

        self.widget_entry_ip_erlinky        = Entry(self, width=self.entry_width, textvariable=self.var_ip_erlinky   ) 
        self.widget_entry_ip_phone          = Entry(self, width=self.entry_width, textvariable=self.var_ip_phone     ) 
        self.widget_entry_port_ER2phone     = Entry(self, width=self.entry_width, textvariable=self.var_port_ER2phone   ) 
        self.widget_entry_port_phone2ER     = Entry(self, width=self.entry_width, textvariable=self.var_port_phone2ER   ) 
        self.widget_entry_mise_en_veille    = Entry(self, width=self.entry_width, textvariable=self.var_mise_en_veille  ) 
        self.widget_entry_papp              = Entry(self, width=self.entry_width, textvariable=self.var_puissance_apparente ) 
        self.widget_entry_index_hc          = Entry(self, width=self.entry_width, textvariable=self.var_index_heure_creuse )
        self.widget_entry_index_hp          = Entry(self, width=self.entry_width, textvariable=self.var_index_heure_pleine )
        self.widget_entry_label_teleinfo    = Entry(self, width=self.entry_width, textvariable=self.var_label_teleinfo )
        self.widget_entry_value_teleinfo    = Entry(self, width=self.entry_width, textvariable=self.var_value_teleinfo )
        self.widget_entry_time              = Entry(self, width=self.entry_width, textvariable=self.var_time )
        # Création des Widgets Label Etat envoi Trame
        self.widget_label_etat_trame        = Label(self, text="Aucune trame transmise")

        # Modification des couleurs
        self.widget_label_ip_erlinky.config(             fg='gray')
        self.widget_label_ip_phone.config(               fg='gray')
        self.widget_label_port_ER2phone.config(             fg='gray')
        self.widget_label_port_phone2ER.config(             fg='gray')
        self.widget_label_parametres.config(    bg='gray', fg='white')
        self.widget_label_reseau.config(        bg='gray', fg='white')
        self.widget_entry_ip_erlinky.config(    bg='gray', fg='white')
        self.widget_entry_ip_phone.config(      bg='gray', fg='white')
        self.widget_entry_port_ER2phone.config( bg='gray', fg='white')
        self.widget_entry_port_phone2ER.config( bg='gray', fg='white')
        self.widget_entry_time.config(          bg='gray', fg='white')
        self.widget_label_teleinfo.config(      bg='gray', fg='white')
        self.widget_label_etat.config(          bg='gray', fg='white')
        

        
        # Création d'un Widget de Canvas représentant graphiquement le Compteur Linky 
        self.can    = Canvas(fenetre, width=270, height=320, background="lawn green")
        rect1       = self.can.create_rectangle(60,100,200,300, fill="light grey")
        rect2       = self.can.create_rectangle(80,150,180,250, fill="snow4")
        text1       = self.can.create_text(130, 50, text="Linky", fill="snow4", font=("Purisa, 20") )

    
        # Positionnement des widgets (méthode de position Grid)
        self.grid()
        ind_row = 0
        self.widget_label_reseau.grid(           row=ind_row,  columnspan = 2, sticky =  W+E+N+S)
        ind_row += 1
        self.widget_label_ip_erlinky.grid(      row=ind_row,  column=0, sticky = W)
        self.widget_entry_ip_erlinky.grid(      row=ind_row,  column=1, sticky = W)
        ind_row += 1
        self.widget_label_ip_phone.grid(        row=ind_row,  column=0, sticky = W)
        self.widget_entry_ip_phone.grid(        row=ind_row,  column=1, sticky = W)
        ind_row += 1
        self.widget_label_port_ER2phone.grid(    row=ind_row,  column=0, sticky = W)
        self.widget_entry_port_ER2phone.grid(    row=ind_row,  column=1, sticky = W)
        ind_row += 1
        self.widget_label_port_phone2ER.grid(    row=ind_row,  column=0, sticky = W)
        self.widget_entry_port_phone2ER.grid(    row=ind_row,  column=1, sticky = W)
        ind_row += 1
        self.widget_label_parametres.grid(       row=ind_row,  columnspan = 2, sticky =  W+E+N+S)
        ind_row += 1
        self.widget_label_mise_en_veille.grid(   row=ind_row,  column=0, sticky = W)
        self.widget_entry_mise_en_veille.grid(   row=ind_row,  column=1, sticky = W)
        ind_row += 1
        self.widget_label_teleinfo.grid(         row=ind_row,  columnspan = 2, sticky =  W+E+N+S)
        ind_row += 1
        self.widget_label_papp.grid(             row=ind_row,  column=0, sticky = W)
        self.widget_entry_papp.grid(             row=ind_row,  column=1, sticky = W)
        ind_row += 1
        self.widget_label_index_hc.grid(         row=ind_row,  column=0, sticky = W)
        self.widget_entry_index_hc.grid(         row=ind_row,  column=1, sticky = W)
        ind_row += 1
        self.widget_label_index_hp.grid(         row=ind_row,  column=0, sticky = W)
        self.widget_entry_index_hp.grid(         row=ind_row,  column=1, sticky = W)
        ind_row += 1
        self.widget_entry_label_teleinfo.grid(   row=ind_row,  column=0, sticky = E)
        self.widget_entry_value_teleinfo.grid(   row=ind_row,  column=1, sticky = W)
        ind_row += 1
        self.widget_label_etat.grid(             row=ind_row,  columnspan = 2, sticky =  W+E+N+S)
        ind_row += 1
        self.widget_label_etat_connection.grid(  row=ind_row,  columnspan = 2)
        ind_row += 1
        self.widget_label_etat_trame.grid(       row=ind_row,  columnspan = 2)
        ind_row += 1
        self.widget_label_time.grid(             row=ind_row,  column=0, sticky = W)
        self.widget_entry_time.grid(             row=ind_row,  column=1, sticky = W)
        ind_row += 1
        

        self.can.grid( row = ind_row )

    # ------------------------------------------------------------------------- 
    # Création Thread permettant de gérer les communications
    # -------------------------------------------------------------------------
    def creation_thread(self):
        
        # Si Thread jamais été cree
        if self._thread is None:
            
            # Création du Thread : la fonction dénommé "action" sera appelée
            # lors du démarrage du thread
            self._thread = Thread(target=self.action)
            
            # Démarrage du Thread : on peut démarrer tout de suite
            self._thread.start()
        
 
    # ------------------------------------------------------------------------- 
    # Thread off
    # -------------------------------------------------------------------------
    def thread_off(self):
        if self._thread is not None:
            self._thread = None

    # ------------------------------------------------------------------------- 
    # Fonction calculant le temps écoulé depuis le début de la transmission
    # -------------------------------------------------------------------------
    def compute_delta_time(self, t_start):

        # Récupération du temps courant en secondes
        current_time        = time.time()

        # Calcul de la durée écoulée
        delta_min       = float( (current_time - t_start)/ 60.0 )
        chaine_txt      = "%.2f minutes" % delta_min
        self.var_time.set(chaine_txt)

        return delta_min


    # ------------------------------------------------------------------------- 
    # Permet de tester si la mise en veille doit être prononcée
    # t_start : entrée contenant la date démarrage de l'envoi des données
    # -------------------------------------------------------------------------
    def continue_to_send(self, t_start):

        delta_min = self.compute_delta_time(t_start)

        # Récupération du champs spécifiant la durée de mise en veille
        delay_mise_veille   = float(self.var_mise_en_veille.get())

        # Test : A t-on dépasser la durée
        return( delta_min < delay_mise_veille )  

    # ------------------------------------------------------------------------- 
    # Transmission de la téléinformation par UDP
    #  ADCO     : Adresse du compteur
    #  BASE     : Index option de base
    #  HCHC     : Index Heures creuses
    #  HCHP     : Index Heures pleines
    #  ISOUSC   : Intensité souscrite
    # -------------------------------------------------------------------------
    def send_teleinfo_by_UDP(self, sock, address, json_tele):

        # Boucle sur les clés (normalement, il n'y a qu'une)
        for cle in json_tele.keys():

            # Affichage de la téléinformation sur l'IHM
            self.var_label_teleinfo.set(cle)
            self.var_value_teleinfo.set(json_tele[cle])

            # Identification Puissance apparente et affichage
            if cle == "PAPP":
                # Mise à jour du champ puissance apparente 
                self.var_puissance_apparente.set( json_tele[cle] )
                send_UDP = True 
            elif cle == "HCHC":
                # Mise à jour du index heures creuses 
                self.var_index_heure_creuse.set( json_tele[cle] )
                send_UDP = True 
            elif cle == "HCHP":
                 # Mise à jour du index heures pleines 
                self.var_index_heure_pleine.set( json_tele[cle] )
                send_UDP = True 
            elif cle == "BASE":
                 # Mise à jour du champ heures pleines (car pas abonnement heures creuses)
                self.var_index_heure_pleine.set( json_tele[cle] )
                send_UDP = True 
            elif cle == "ADCO":
                send_UDP = True 
            elif cle == "OPTARIF":
                send_UDP = True 
            elif cle == "ISOUSC":
                send_UDP = True 
            elif cle == "IINST":
                send_UDP = True 
            elif cle == "IMAX":
                send_UDP = True 
            else :
                send_UDP = False

            if send_UDP == True:
                # Envoi de la télé-information
                message = str(json.dumps(json_tele)) 
                self.udp.send_teleinfo( sock, address,  message )  

    # ------------------------------------------------------------------------- 
    # Send with UDP teleinfo from file
    # -------------------------------------------------------------------------
    def send_teleinfo_from_file(self, t_start, sock, address):

        i = 0
        # Boucle sur la liste de données de téléinformation
        for teleinfo in self.linky_file.get_teleinfo():

            if self.continue_to_send(t_start) == False:
                break

            # Conversion téléinformation au format Json
            json_tele = json.loads(teleinfo)
            
            # Envoi par UDP
            self.send_teleinfo_by_UDP(sock, address, json_tele) 
                                          
            # Mise à jour nombre IHM
            self.widget_label_etat_trame["text"] = "Transmission ... (%d)" % ( i )

            # Pause 
            sleep(0.01)
            i+= 1

    # ------------------------------------------------------------------------- 
    # Send with UDP teleinfo from file
    # -------------------------------------------------------------------------
    def send_teleinfo_from_serial(self, sock, address):

        teleinfo = self.linky_serial.get_linkydata()

        if teleinfo != "":

            # Conversion téléinformation au format Json
            json_tele = json.loads(teleinfo)

            # Envoi par UDP
            self.send_teleinfo_by_UDP(sock, address, json_tele) 

            # Mise à jour nombre IHM
            self.widget_label_etat_trame["text"] = "Transmission ..." 

    # ------------------------------------------------------------------------- 
    # Call-back action (Gère les communications)
    # -------------------------------------------------------------------------
    def action(self):
        
        while 1:

            # Attente bloquante d'une requête en provenance du smartphone 
            self.widget_label_etat_connection["text"] = "Attente requête du smartphone ..."

            # Attente du message de réveil
            sock,address = self.udp.waiting_message_from_smartphone( self.var_port_phone2ER.get() )

            # Mise à jour de l'interface
            self.var_ip_phone.set(      address[0] )
            self.var_port_ER2phone.set( address[1] )

            self.widget_label_etat_connection["text"] = "Requête reçu du smartphone "

            # Récupération du temps 
            t_start = time.time()

            while self.continue_to_send(t_start):

                if self.simulator == True:
                    self.send_teleinfo_from_file(t_start, sock, address)
                else:
                    self.send_teleinfo_from_serial(sock, address)


        
            print("[*] Arrêt transmission de la téléinformation")     
            self.widget_label_etat_trame["text"] = "Stopped."
            # Fermeture du socket
            sock.close()            
            sleep(1)
        
# -----------------------------------------------------------------------------
# Test
# ----------------------------------------------------------------------------- 
if __name__ == '__main__':

    # Running Options
    parser  = argparse.ArgumentParser()
    parser.add_argument("--simulator", help="display a square of a given number", action="store_true")
    args    = parser.parse_args()
    if args.simulator:
        print("[*] Simulator turned ON")
        print("[*] If you want to get real data, type 'python LINKY.py' ")
        mode_simu = True
    else:
        print("[*] The simulator is turned OFF")
        print("[*] If you want to run the simulator, type 'python LINKY.py --simulator'  ")
        print("[*] Don't forget to connect a Linky demodulator to USB port")
        mode_simu = False

    # Create interface
    fenetre     = Tk()
    linky       = InterfaceLinky(fenetre, mode_simulator = mode_simu)  
    linky.mainloop()
    linky.thread_off()
    exit(0)
