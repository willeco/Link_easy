#!/usr/bin/env python
# -*-coding:Latin-1 -*

import  serial
import  sys
import  json
import  lib_linky_file


from    time      import sleep

 

class LinkySerial():

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
    # Initialisation du constructeur (port série)
    # ------------------------------------------------------------------
    def __init__(self):

        print("[*] Create serial objet to connect a Linky demodulator on USB port")

        #self.portname = "/dev/ttyAMA0"
        self.portname = "/dev/ttyUSB0"

        try:
            self.port = serial.Serial(   self.portname, 
                                    baudrate = 1200,
                                    parity   = serial.PARITY_EVEN,
                                    stopbits = serial.STOPBITS_ONE,
                                    bytesize = serial.SEVENBITS,
                                    timeout  = 10.0)
            print(LinkySerial.OKGREEN + "[*] Linky demodulator connected to USB port : %s" % self.port.name) 
        except:
            self.port = None
            
            print(LinkySerial.FAIL + "[!] Please connect a Linky demodulator on USB port !!!!")


        


    # -------------------------------------------------------------------------
    # Lecture port série et transformation de ligne au format Json
    # -------------------------------------------------------------------------
    def get_linkydata(self):

        json_teleinfo = ""
        if self.port != None:
            rcv             = self.port.readline()
            json_teleinfo   = lib_linky_file.convert_teleinfo_line_to_json(rcv)

        return(json_teleinfo)

    # -------------------------------------------------------------------------
    # For Test only
    # -------------------------------------------------------------------------
    def display_linky(self):

        if self.port != None:
            while True:
                json_teleinfo   = self.get_linkydata()

                print("[*] Received stream : %s" % json_teleinfo)
        else:
            print(FAIL + "[!] USB port is not connected !")



# -----------------------------------------------------------------------------
# Test
# ----------------------------------------------------------------------------- 
if __name__ == '__main__':

    print("[*] Test Linky Serial")

    my_serial = LinkySerial()
    my_serial.display_linky()

