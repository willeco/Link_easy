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
import lib_UDP
import time
import socket

class InterfacePhone(Frame):
   
    # ------------------------------------------------------------------ 
    # Initialisation Interface 
    # ------------------------------------------------------------------
    def __init__(self, fenetre):

        Frame.__init__(self, fenetre, width=500, height=500)
        fenetre.title('Smartphone')
        
        # Préparation class UDP
        self.udp                = lib_UDP.LinkyUDP()

        # Creation des widgets
        self.creation_widgets()

        # Initialisation du thread
        self._thread            = None

        # Initialisation du socket
        self.sock               = None

        self.nb_trames_recues   = 0

    # ------------------------------------------------------------------ 
    # Creation Widgets
    # ------------------------------------------------------------------
    def creation_widgets(self):

        self.entry_width                    = 17

        # Création des variables interface Homme Machine 
        self.var_add_ip_er                  = StringVar()
        self.var_add_ip_phone               = StringVar()
        self.var_port_ER2phone              = StringVar()
        self.var_port_phone2ER              = StringVar()

        self.var_puissance_apparente        = StringVar()
        self.var_index_heure_creuse         = StringVar() 
        self.var_index_heure_pleine         = StringVar()

        # Affectation des variables 
        self.var_add_ip_er.set(     "erlinky.home")

        # Get IP host just for fun
        ip_host = self.udp.get_ip_host()
        self.var_add_ip_phone.set(  ip_host )

        self.var_port_ER2phone.set(self.udp.get_port_ER2phone() )
        self.var_port_phone2ER.set(self.udp.get_port_phone2ER() )
        self.var_puissance_apparente.set("?")
        self.var_index_heure_creuse.set("?")
        self.var_index_heure_pleine.set("?")

        # Création Label : Etat de connection et Affichage Label dans la fenetre
        self.widget_label_etat_connection   = Label(self, text="Non connecté à l'émetteur radio Linky")
        
        # Création Entry et Label : Adresse IP et Port de Destination
        self.widget_label_reseau            = Label(self, text="Configuration réseau"      )
        self.widget_label_add_ip_er         = Label(self, text="Adresse IP du module radio") 
        self.widget_label_add_ip_phone      = Label(self, text="Adresse IP du smarphone"   ) 
        self.widget_label_port_ER2phone     = Label(self, text="Port UDP : ER vers smarphone"  ) 
        self.widget_label_port_phone2ER     = Label(self, text="Port UDP : smarphone vers ER"  ) 
        self.widget_label_teleinfo          = Label(self, text="Télé-information")
        self.widget_label_etat              = Label(self, text="Etat de réception"                 )
        self.widget_label_papp              = Label(self, text="Puissance apparente (V.A)" )
        self.widget_label_index_hc          = Label(self, text="Index heures creuses (H.C)" )
        self.widget_label_index_hp          = Label(self, text="Index heures pleines (H.P)" ) 
        self.widget_label_messages_recus    = Label(self, text="Messages reçus :")

        self.widget_entry_add_ip_er         = Entry(self, width=self.entry_width, textvariable=self.var_add_ip_er       ) 
        self.widget_entry_add_ip_phone      = Entry(self, width=self.entry_width, textvariable=self.var_add_ip_phone    ) 
        self.widget_entry_port_ER2phone     = Entry(self, width=self.entry_width, textvariable=self.var_port_ER2phone   ) 
        self.widget_entry_port_phone2ER     = Entry(self, width=self.entry_width, textvariable=self.var_port_phone2ER   ) 

        self.widget_entry_papp              = Entry(self, width=self.entry_width, textvariable=self.var_puissance_apparente ) 
        self.widget_entry_index_hc          = Entry(self, width=self.entry_width, textvariable=self.var_index_heure_creuse  ) 
        self.widget_entry_index_hp          = Entry(self, width=self.entry_width, textvariable=self.var_index_heure_pleine  ) 


        # Creation d'un widget Etat
        self.widget_label_etat_trame        = Label(self, text="Aucune trame reçue")

        # Création des Widgets Bouton ON
        self.widget_on_button               = Button(self, text="Demande de télé-info.",  command=self.callback_on)
        
        
        # Création d'un widget Text
        self.widget_text                    =  Text(self, height=10, width=40)
        self.widget_text.insert(INSERT, "Appuyez sur le bouton Demande de télé-info.")

        # Création d'un Widget représentant graphiquement le Compteur Linky 
        self.can                            = Canvas(fenetre, width=310, height=100, background="black")
    

        # Configuration des widgets
        self.widget_on_button.config( width=self.entry_width-3, bg='red')

        #self.widget_label_add_ip_er.config(         fg='gray')
        self.widget_label_add_ip_phone.config(      fg='gray')
        self.widget_label_port_ER2phone.config(     fg='gray')
        self.widget_label_port_phone2ER.config(     fg='gray')
        self.widget_label_reseau.config(            bg='gray', fg='white')

        #self.widget_entry_add_ip_er.config(         bg='gray', fg='white')
        self.widget_entry_add_ip_phone.config(      bg='gray', fg='white', state=DISABLED)
        self.widget_entry_port_ER2phone.config(     bg='gray', fg='white', state=DISABLED)
        self.widget_entry_port_phone2ER.config(     bg='gray', fg='white', state=DISABLED)

        self.widget_entry_papp.config(              state=DISABLED) 
        self.widget_entry_index_hc.config(          state=DISABLED) 
        self.widget_entry_index_hp.config(          state=DISABLED)


        self.widget_label_teleinfo.config(          bg='gray', fg='white')
        self.widget_label_etat.config(              bg='gray', fg='white')


        # Positionnement des widgets
        self.grid()
        ind_row = 0
        self.widget_label_reseau.grid(           row=ind_row,  columnspan = 2, sticky =  W+E+N+S)
        ind_row += 1
        self.widget_label_add_ip_er.grid(        row=ind_row,  column=0, sticky = W)
        self.widget_entry_add_ip_er.grid(        row=ind_row,  column=1, sticky = W)
        ind_row += 1
        self.widget_label_add_ip_phone.grid(     row=ind_row,  column=0, sticky = W)
        self.widget_entry_add_ip_phone.grid(     row=ind_row,  column=1, sticky = W)
        ind_row += 1
        self.widget_label_port_ER2phone.grid(    row=ind_row,  column=0, sticky = W)
        self.widget_entry_port_ER2phone.grid(    row=ind_row,  column=1, sticky = W)
        ind_row += 1
        self.widget_label_port_phone2ER.grid(    row=ind_row,  column=0, sticky = W)
        self.widget_entry_port_phone2ER.grid(    row=ind_row,  column=1, sticky = W)
        ind_row += 1
        self.widget_label_etat.grid(             row=ind_row,  columnspan = 2, sticky =  W+E+N+S)
        ind_row += 1
        self.widget_label_etat_connection.grid(  row=ind_row,  columnspan = 2)
        ind_row += 1
        self.widget_label_etat_trame.grid(       row=ind_row,  columnspan = 2)
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
        self.widget_on_button.grid(              row=ind_row,  columnspan = 2, sticky =W+E+N+S)
        ind_row += 1

        self.widget_label_messages_recus.grid(   row=ind_row,  columnspan = 2, sticky =W)
        ind_row += 1

        self.widget_text.grid(                   row=ind_row,  columnspan = 2, sticky =W+E+N+S)
        ind_row += 1

        self.can.grid(                          row = ind_row )

    # ------------------------------------------------------------------------- 
    # Call-back : Demande de télé-information
    # -------------------------------------------------------------------------
    def callback_on(self):
        
        # Création socket
        address     = self.var_add_ip_er.get()
        port        = self.var_port_phone2ER.get()

        if self.sock is None:
            # Création  du socket
            self.sock   = socket.socket(socket.AF_INET, socket.SOCK_DGRAM )
            
        else:
            # Il est inutile et dangereux de créer un nouveau socket. 
            # Les ports ont déjà été réservés
            print('[*] Le socket a déjà été crée ')

        # Demande de téléinformation au module Radio Linky
        self.udp.smartphone_ask_teleinfo_to_erlinky( self.sock, address, port )

        # Si Thread jamais été créée
        if self._thread is None:
            
            # Création du Thread : la fonction "réception" sera appelée
            self._thread = Thread(target=self.reception)
            
            # Démarrage du Thread de réception des données de téléinformation
            self._thread.start()
        else:
            print('[*] Le Thread de réception a déjà été crée ')
    # ------------------------------------------------------------------------- 
    # Call-back Réception de la télé-information
    # -------------------------------------------------------------------------
    def reception(self):


        message                                     = "Attente de la télé-information" 
        self.widget_label_etat_connection["text"]   = message
        print("[*] " + message)
 
        while True:

            # Réception des données Linky : On ré-utilise le socket ayant servi
            # a réveiller le compteur linky
            data, address   = self.sock.recvfrom ( 4096 )
            ip_exp          = address[0]
            port_exp        = address[1]


            # Mise à jour de l'IHM (Message reçu)
            print("[*] Recu de %s depuis port %d message %s" % ( ip_exp , int(port_exp), data) )
            self.widget_text.insert(END, data + '\n')
            self.widget_text.yview('moveto', 1.0)

            # On renseigne le champ de l'interface avec l'adresse IP du module radio
            self.var_add_ip_er.set(     ip_exp  )
            #self.var_port_ER2phone.set( port_exp) 


            # Mise au Format JSON de la téléinformation
            json_tele = json.loads(data)
            
            # Boucle sur les clés (normalement, il n'y en a qu'une)
            for cle in json_tele.keys():
                if cle == "PAPP":
                    self.var_puissance_apparente.set( json_tele[cle] )  
                elif cle == "HCHC":
                    self.var_index_heure_creuse.set( json_tele[cle] )  
                elif ((cle == "HCHP") or (cle == "BASE")):
                    self.var_index_heure_pleine.set( json_tele[cle] )                


            # Affichage état de réception
            self.nb_trames_recues += 1
            self.widget_label_etat_trame["text"] = ("%d trames reçues" % self.nb_trames_recues)

            # Mise à jour de l'interface
            if (self.nb_trames_recues == 1):
                self.widget_label_etat_connection["text"] = "Réception téléinfo en cours"

        #sock.close()
        
# -----------------------------------------------------------------------------
# Test
# ----------------------------------------------------------------------------- 
if __name__ == '__main__':
    fenetre     = Tk()
    phone       = InterfacePhone(fenetre)  
    phone.mainloop()
    exit(0)
