#!/usr/bin/env python
# -*-coding:Latin-1 -*

import socket
import sys

class LinkyUDP:

    # https://stackoverflow.com/questions/287871/how-to-print-colored-text-in-terminal-in-python
    HEADER      = '\033[95m'
    OKBLUE      = '\033[94m'
    OKGREEN     = '\033[92m'
    WARNING     = '\033[93m'
    FAIL        = '\033[91m'
    ENDC        = '\033[0m'
    BOLD        = '\033[1m'
    UNDERLINE   = '\033[4m'

    def __init__(self):
        
        # Ports de communication UDP
        self.port_ER2phone          = 10002 # Port for UDP communications ER to MobilePhone
        self.port_phone2ER          = 10001 # Port for UDP communications MobilePhone to ER
        self.buffer_size            = 1024

        # Host Name de l'émetteur radio Linky
        self.ER_hostname            = "erlinky.home"

    # -------------------------------------------------------------------------
    # Port à utiliser dans le sens ER (émetteur radio Linky) vers smartphone
    # -------------------------------------------------------------------------
    def get_port_ER2phone(self):

        return(self.port_ER2phone)

    # -------------------------------------------------------------------------
    # Port à utiliser dans le sens smartphone vers ER (émetteur radio Linky)
    # -------------------------------------------------------------------------
    def get_port_phone2ER(self):

        return(self.port_phone2ER)

    # -------------------------------------------------------------------------
    # Get IP host
    # -------------------------------------------------------------------------
    def get_ip_host(self):

        try:
            hostname              = socket.gethostname()
            ip_host               = socket.gethostbyname(hostname)
        except:
            ip_host               = "localhost"

        return(ip_host)


    # -------------------------------------------------------------------------
    # Get the hostname of the current machine
    # -------------------------------------------------------------------------
    def get_hostname(self):

        # The hostname is usually saved in the file /etc/hosts
        # It can be changed by modifying the file : sudo nano /etc/hosts
        self.hostname               = socket.gethostname()
        print("[*] The name of this machine is : %s "% self.hostname)

        return(self.hostname)

    # -------------------------------------------------------------------------
    # Return the IP address of the livebox
    # -------------------------------------------------------------------------
    def get_ip_of_box(self):
        ip_address_of_livebox  = socket.gethostbyname('livebox.home')
        print("[*] IP address of livebox : %s "% ip_address_of_livebox )

        return(ip_address_of_livebox) 

    # -------------------------------------------------------------------------
    # Sent teleinformation to the smarphone (Nouvelle version)
    # -------------------------------------------------------------------------
    def send_teleinfo(self, sock, address, teleinfo):


        # Envoi du message
        sent                = sock.sendto ( teleinfo , address )
        print("[*] Send to %s (port %d) Message:%s" % (address[0], address[1], teleinfo ) )


    # -------------------------------------------------------------------------
    # Linky's ER is waiting a message from the smarphone (Nouvelle version)
    # -------------------------------------------------------------------------
    def waiting_message_from_smartphone(self, port):

        s    = socket.socket(socket.AF_INET, socket.SOCK_DGRAM )

        try:
            s.bind( ('', int(port) ) )
        except:
            print(LinkyUDP.FAIL + '[!] Impossible to bind on port:%d' % int(port) )

        print(LinkyUDP.WARNING + '[*] Wait request from smartphone on port %d' % int(port) )
        data, address = s.recvfrom ( self.buffer_size )

        print (LinkyUDP.OKGREEN + '[*] Received message  : %s ' % data )
        print (LinkyUDP.OKGREEN + '[*] Source IP address : %s ' % address[0] )
     

        # On connait désormais l'adresse de la source
        return (s, address)

    # ------------------------------------------------------------------
    # Requete de données au compteur Linky depuis le smartphone
    # Port 10001
    # ------------------------------------------------------------------
    def smartphone_ask_teleinfo_to_erlinky(self, sock, address, port ):
       
        # Création du message à destination du module radio
        message         = "Hello ERLinky. Envoie la teleinformation !\n"
        print('[*] Send to %s (port=%d)  Message:%s' % ( address, int(port), message ) )  

        # Envoi du message sur le port 10001
        try:
            sent            = sock.sendto ( message.encode(), (address, int(port) ) )
        except:
            print('[!] Avez-vous saisi la bonne adresse IP du LINKY ??')

        # On retourne le socket car il sera utilisé pour la réception
        # return (sock)

# ----------------------------------------------------------------------
# Main
# ----------------------------------------------------------------------
if __name__ == '__main__':
    print("[*] Test Linky UDP")
