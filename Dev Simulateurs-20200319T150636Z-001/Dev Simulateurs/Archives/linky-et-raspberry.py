
import socket
import socket
import sys
import json
import os
import time
import lib_UDP

from random import *

# Create a UDP socket
# https://python.doctor/page-reseaux-sockets-python-port
# https://www.lifewire.com/ping-command-2618099
# 

print("+------------------------------------------+")
print("| Linky's Simulator Linky and Raspberry PI |")
print("+------------------------------------------+")

print("[*] Prepare Linky Data")
linky_data      =  '{ "HCHP":0, "PTEC":"HC..", "IINST":1, "PAPP":170, "HHPHC":3}'
linky_json      = json.loads(linky_data)


# -----------------------------------------------------------------------------
# Wait a connection from the smartphone
# -----------------------------------------------------------------------------
udp_obj = lib_UDP.UDPClass()

udp_obj.waiting_message_from_smartphone()

# -----------------------------------------------------------------------------
# Prepare to send Linky's data
# -----------------------------------------------------------------------------
print("[*] Preparation Socket UDP")
sock           = socket.socket(socket.AF_INET, socket.SOCK_DGRAM )

#address        = "192.168.1.18"
#address        = "livebox.home" # Host Name
#address        = "erlinky.home" # Host Name
address         = "localhost"
port            = 10002
server_address  = (address, port )



for i in range(10):
    try :
        # ---------------------------------------------------------------------
        # Prepare Linky Data
        # ---------------------------------------------------------------------
        # Linky provides a new value of power every seconds
        linky_json["PAPP"] = randint(100, 200)
        print("\t > Puissance apparente Linky : %d VA" % linky_json["PAPP"])
        message_json       = json.dumps(linky_json, sort_keys=True, indent=4, separators=(',', ': '))
        print("\t > Message Json %s" % message_json) 

        # ---------------------------------------------------------------------
        # Send Linky Data to the client
        # ---------------------------------------------------------------------
        sent                = sock.sendto ( message_json , server_address )
        #print("\t > Message of %d characters sent to '%s' on port '%d' " % (sent, address, port ) )
        print("\t > Message '%s' envoyé à '%s' port:%d " % (message_json, address, port ) )
    except:
        print("[!] Cannot sent message")

    # Wait one second (Linky data available every second)
    time.sleep(1)
