# -*- coding: utf-8 -*-
"""
Created on Wed Oct  9 21:42:22 2019

@author: Bidou
"""
import matplotlib.pyplot as plt
import numpy as np

def TeleInfoExtraction(path, filename):
    NbIINST1 = 0
    NbIINST2 = 0
    NbIINST3 = 0
    IINST1Data = []
    IINST2Data = []
    IINST3Data = []
    PINST1Data = []
    PINST2Data = []
    PINST3Data = []
    
    NbIMAX1 = 0
    NbIMAX2 = 0
    NbIMAX3 = 0
    IMAX1Data = []
    IMAX2Data = []
    IMAX3Data = []

    NbHCHC = 0
    NbHCHP = 0
    HCHCData = []
    HCHPData = []

    #try:
    fp = open(path, "r")
    print("Processing File %s" %(path))
    for line in fp:

        if line.find("IINST1") != -1:
            line = line.rstrip("\r\n")
            temp = line.split(" ")
            IINST1Data.append(int(temp[1]))
            PINST1Data.append(int(temp[1])*240)
            NbIINST1 += 1
        if line.find("IINST2") != -1:
            line = line.rstrip("\r\n")
            temp = line.split(" ")
            IINST2Data.append(int(temp[1]))
            PINST2Data.append(int(temp[1])*240)
            NbIINST2 += 1
        if line.find("IINST3") != -1:
            line = line.rstrip("\r\n")
            temp = line.split(" ")
            IINST3Data.append(int(temp[1]))
            PINST3Data.append(int(temp[1])*240)
            NbIINST3 += 1

        if line.find("IMAX1") != -1:
            line = line.rstrip("\r\n")
            temp = line.split(" ")
            IMAX1Data.append(int(temp[1]))
            NbIMAX1 += 1
        if line.find("IMAX2") != -1:
            line = line.rstrip("\r\n")
            temp = line.split(" ")
            IMAX2Data.append(int(temp[1]))
            NbIMAX2 += 1
        if line.find("IMAX3") != -1:
            line = line.rstrip("\r\n")
            temp = line.split(" ")
            IMAX3Data.append(int(temp[1]))
            NbIMAX3 += 1

        if line.find("HCHP") != -1:
            line = line.rstrip("\r\n")
            temp = line.split(" ")
            HCHPData.append(float(int(temp[1]))/1000.)
            NbHCHP += 1
        if line.find("HCHC") != -1:
            line = line.rstrip("\r\n")
            temp = line.split(" ")
            HCHCData.append(float(int(temp[1]))/1000.)
            NbHCHC += 1            
    fp.close()
    print("*"*20)
    print("Stats:")
    print("NB frames %d %d %d" % (NbIINST1, NbIINST2,NbIINST3))
    print("*"*20)

    plt.plot(IINST1Data,marker='o')
    plt.plot(IINST2Data,marker='x')
    plt.plot(IINST3Data,marker='+')
    plt.legend(['IINST1',"IINST2","IINST3"])
    plt.title("I instantané par phase %s" % (filename))
    plt.show()
    
    plt.plot(PINST1Data,marker='o')
    plt.plot(PINST2Data,marker='x')
    plt.plot(PINST3Data,marker='+')
    plt.legend(['PINST1',"PINST2","PINST3"])
    plt.title("P instantané par phase %s" % (filename))
    plt.show()
    
#    plt.plot(IMAX1Data,marker='o')
#    plt.plot(IMAX2Data,marker='x')
#    plt.plot(IMAX3Data,marker='+')
#    plt.legend(['IMAX1',"IMAX2","IMAX3"])
#    plt.title("I MAX par phase %s" % (filename))
#    plt.show()
    
    plt.plot(HCHPData,marker='o')
    plt.plot(HCHCData,marker='x')
    plt.legend(['HP',"HC"])
    plt.title("Compteur HP et HC %s" % (filename))
    plt.show()
    print(HCHPData)
    print(HCHCData)
    #print("HCHP: MAX %f MIN %f" %(max(HCHPData), min(HCHPData)))
    #print("HCHC: MAX %f MIN %f" %(max(HCHCData), min(HCHCData)))
    
###############################################################################
# Test main
###############################################################################
if __name__ == '__main__':
	
    chemin = "D:\\JDE\\Scripts\\python\\TELEINFO\\"
    chemin = ""
    fichier = "compteur.txt"
    path = chemin + fichier


    NbIINST1 = 0
    NbIINST2 = 0
    NbIINST3 = 0
    IINST1Data = []
    IINST2Data = []
    IINST3Data = []
    
    NbIMAX1 = 0
    NbIMAX2 = 0
    NbIMAX3 = 0
    IMAX1Data = []
    IMAX2Data = []
    IMAX3Data = []

    NbHCHC = 0
    NbHCHP = 0
    HCHCData = []
    HCHPData = []

    TeleInfoExtraction(path, fichier)
    
    fichier = "compteur2.txt"
    path = chemin + fichier
    TeleInfoExtraction(path, fichier)
    
    fichier = "compteur3.txt"
    path = chemin + fichier
    TeleInfoExtraction(path, fichier)
    
    
    """
    fp = open(path, "r")
    print("Processing File %s" %(path))
    for line in fp:

        if line.find("IINST1") != -1:
            line = line.rstrip("\r\n")
            temp = line.split(" ")
            IINST1Data.append(int(temp[1]))
            NbIINST1 += 1
        if line.find("IINST2") != -1:
            line = line.rstrip("\r\n")
            temp = line.split(" ")
            IINST2Data.append(int(temp[1]))
            NbIINST2 += 1
        if line.find("IINST3") != -1:
            line = line.rstrip("\r\n")
            temp = line.split(" ")
            IINST3Data.append(int(temp[1]))
            NbIINST3 += 1

        if line.find("IMAX1") != -1:
            line = line.rstrip("\r\n")
            temp = line.split(" ")
            IMAX1Data.append(int(temp[1]))
            NbIMAX1 += 1
        if line.find("IMAX2") != -1:
            line = line.rstrip("\r\n")
            temp = line.split(" ")
            IMAX2Data.append(int(temp[1]))
            NbIMAX2 += 1
        if line.find("IMAX3") != -1:
            line = line.rstrip("\r\n")
            temp = line.split(" ")
            IMAX3Data.append(int(temp[1]))
            NbIMAX3 += 1

        if line.find("HCHP") != -1:
            line = line.rstrip("\r\n")
            temp = line.split(" ")
            HCHPData.append(int(temp[1]))
            NbHCHP += 1
        if line.find("HCHC") != -1:
            line = line.rstrip("\r\n")
            temp = line.split(" ")
            HCHCData.append(int(temp[1]))
            NbHCHC += 1            
    fp.close()
    print("*"*20)
    print("Stats:")
    print("NB frames %d %d %d" % (NbIINST1, NbIINST2,NbIINST3))
    print("*"*20)

    plt.plot(IINST1Data,marker='o')
    plt.plot(IINST2Data,marker='x')
    plt.plot(IINST3Data,marker='+')
    plt.legend(['IINST1',"IINST2","IINST3"])
    plt.title("I instantané par phase")
    plt.show()
    
    plt.plot(IMAX1Data,marker='o')
    plt.plot(IMAX2Data,marker='x')
    plt.plot(IMAX3Data,marker='+')
    plt.legend(['IMAX1',"IMAX2","IMAX3"])
    plt.title("I MAX par phase")
    plt.show()
    
    plt.plot(HCHPData,marker='o')
    plt.plot(HCHCData,marker='x')
    plt.legend(['HP',"HC"])
    plt.title("Compteur HP et HC ")
    plt.show()
    """
