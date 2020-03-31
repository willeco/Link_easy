# -*- coding: utf-8 -*-
"""
Created on Fri Oct 11 19:25:29 2019

@author: Julien
"""
import glob  
import os.path  
import time 
import serial

if __name__ == "__main__":
    
    fichier = "teleinfolog00.TXT"
   
    #try:
    fp = open(fichier, "w")
    buffer = ""
    nbLines = 0
    
    with serial.Serial('COM3', 1200, serial.SEVENBITS, serial.PARITY_ODD, serial.STOPBITS_ONE) as ser:
        x = ser.read()          # read one byte
        s = ser.read(10)        # read up to ten bytes (timeout)
        while True:
            line = ser.readline()   # read a '\n' terminated line
            s = line.decode("utf-8")
            s = s.rstrip("\r\n")
            #buffer += s
            fp.write(s)
            print(s)
            """
            nbLines +=1
            if nbLines > 100:
                #print(buffer)
                fp.write(buffer)
                buffer = ""
                print(buffer)
                nbLines = 0
            """