
import socket
import sys
import lib_UDP

# -----------------------------------------------------------------------------
# Preparation de l'objet UDP
# -----------------------------------------------------------------------------
udp_obj = lib_UDP.LinkyUDP()

# -----------------------------------------------------------------------------
# Sent of message of wake-up to Linky
# -----------------------------------------------------------------------------
udp_obj.smartphone_ask_teleinfo_to_erlinky()

# -----------------------------------------------------------------------------
# Creation socket UDP pour recevoir la téléinfomation
# -----------------------------------------------------------------------------
# https://python.doctor/page-reseaux-sockets-python-port
# https://www.lifewirep.com/ping-command-2618099
# 
address         = "erlinky.home"
address         = "localhost"
port            = 10002
port            = udp_obj.get_port_ER2phone()

server_address  = (address, port )
s               = socket.socket(socket.AF_INET, socket.SOCK_DGRAM )
s.bind(server_address)


while True:
    print("[*] En attente de message du compteur Linky ...")
    # Limiter la taille a 8176 octets
    data, address = sock.recvfrom ( 4096 )
    print("[*] Recu message de %d octets de %s " % ( len(data), address ) )
    print("[*] Data = %s " % data)

s.close()

