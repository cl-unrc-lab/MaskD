#!/usr/bin/python

import sys, getopt
import random
import math

def writePreamble(mFile) :

    # a depiction of the board as a comment
    mFile.write("// Memory Cell\n")



def writeMemoryTickMilestone(fileName) :

    mFile = open(fileName,"w")

    writePreamble(mFile)

    mFile.write("Process Memory {\n")
    mFile.write("    v: INT; //0.."+str(bits)+"\n")
    mFile.write("    s:INT; //0..2    0=normal, 1=faulty, 2=refreshing\n")
    mFile.write("\n")
    
    mFile.write("    // Initially the cell is in normal state and all bits are set to 0\n")
    mFile.write("    Initial : v==0 && s==0;\n")
    mFile.write("\n")

    mFile.write("    // write\n")
    mFile.write("\n")
    mFile.write("    [w0] !(s==2) -> v=0,s=0;\n")
    mFile.write("    [w1] !(s==2) -> v="+str(bits)+",s=0;\n")
    mFile.write("\n")

    mFile.write("    // read\n")
    mFile.write("\n")
    mFile.write("    [r0] !(s==2) && v<="+str(bits//2)+" -> v=v;\n")
    mFile.write("    [r1] !(s==2) && v>"+str(bits//2) +" -> v=v;\n")
    mFile.write("\n")

    mFile.write("    // tick\n")
    mFile.write("\n")
    mFile.write("    [tick] <1> s==0 -> "+str(probRefresh)+" : s=2\n")
    mFile.write("                    ++ "+str(probFault)+" : s=1\n")
    mFile.write("                    ++ "+str((1-probFault)-probRefresh)+" : v=v;\n")
    mFile.write("    [tick] <1> s==1 -> "+str(probRefresh)+" : s=2\n")
    mFile.write("                    ++ "+str(1-probRefresh)+" : v=v;\n")
    mFile.write("\n")

    mFile.write("    // refresh\n")
    mFile.write("\n")
    mFile.write("    [refresh] s==2 && v<="+str(bits//2)+" -> s=0, v=0;\n")
    mFile.write("    [refresh] s==2 && v>"+str(bits//2)+" -> s=0, v="+str(bits)+";\n")
    mFile.write("\n")

    mFile.write("    // faults\n")
    mFile.write("\n")
    mFile.write("    [fault1] faulty s==1 && v<"+str(bits)+"-> s=0, v=v+1;\n")
    mFile.write("    [fault1] faulty s==1 && v>="+str(bits)+"-> s=0, v="+str(bits-1)+";\n")
    mFile.write("    [fault2] faulty s==1 && v>0-> s=0, v=v-1;\n")
    mFile.write("    [fault2] faulty s==1 && v<=0-> s=0, v=1;\n")
    mFile.write("\n")


    mFile.write("}\n\n")

    mFile.write("Main() {\n")
    mFile.write("    m : Memory ;\n")
    mFile.write("    run m() ;\n")
    mFile.write("}\n")

    mFile.close()

def writeMemoryRefreshMilestone(fileName) :

    mFile = open(fileName,"w")

    writePreamble(mFile)

    mFile.write("Process Memory {\n")
    mFile.write("    v: INT; //0.."+str(bits)+"\n")
    mFile.write("    s:INT; //0..2    0=normal, 1=faulty, 2=refreshing\n")
    mFile.write("\n")
    
    mFile.write("    // Initially the cell is in normal state and all bits are set to 0\n")
    mFile.write("    Initial : v==0 && s==0;\n")
    mFile.write("\n")

    mFile.write("    // write\n")
    mFile.write("\n")
    mFile.write("    [w0] !(s==2) -> v=0,s=0;\n")
    mFile.write("    [w1] !(s==2) -> v="+str(bits)+",s=0;\n")
    mFile.write("\n")

    mFile.write("    // read\n")
    mFile.write("\n")
    mFile.write("    [r0] !(s==2) && v<="+str(bits//2)+" -> v=v;\n")
    mFile.write("    [r1] !(s==2) && v>"+str(bits//2) +" -> v=v;\n")
    mFile.write("\n")

    mFile.write("    // tick\n")
    mFile.write("\n")
    mFile.write("    [tick] s==0 -> "+str(probRefresh)+" : s=2\n")
    mFile.write("                    ++ "+str(probFault)+" : s=1\n")
    mFile.write("                    ++ "+str((1-probFault)-probRefresh)+" : v=v;\n")
    mFile.write("    [tick] s==1 -> "+str(probRefresh)+" : s=2\n")
    mFile.write("                    ++ "+str(1-probRefresh)+" : v=v;\n")
    mFile.write("\n")

    mFile.write("    // refresh\n")
    mFile.write("\n")
    mFile.write("    [refresh] <1> s==2 && v<="+str(bits//2)+" -> s=0, v=0;\n")
    mFile.write("    [refresh] <1> s==2 && v>"+str(bits//2)+" -> s=0, v="+str(bits)+";\n")
    mFile.write("\n")

    mFile.write("    // faults\n")
    mFile.write("\n")
    mFile.write("    [fault1] faulty s==1 && v<"+str(bits)+"-> s=0, v=v+1;\n")
    mFile.write("    [fault1] faulty s==1 && v>="+str(bits)+"-> s=0, v="+str(bits-1)+";\n")
    mFile.write("    [fault2] faulty s==1 && v>0-> s=0, v=v-1;\n")
    mFile.write("    [fault2] faulty s==1 && v<=0-> s=0, v=1;\n")
    mFile.write("\n")


    mFile.write("}\n\n")

    mFile.write("Main() {\n")
    mFile.write("    m : Memory ;\n")
    mFile.write("    run m() ;\n")
    mFile.write("}\n")

    mFile.close()





def usage(exitVal) :

    print("\nusage memory_faulty_gen.py [-h] [-b <int> ] [-p <float>] [-q <float>]\n")
    print("-h :\n")
    print("  Print this help\n")
    print("-r :\n")
    print("  Exclude the rewards setting\n")
    print("-b num :\n")
    print("  Sets the number of bits that the memory cell has\n")
    print("-p num :\n")
    print("  Sets the failure probability of the memory cell to 'num'. 'num' must be a float")
    print("  in the interval (0,1) (default = 0.1)\n")
    print("-q num :\n")
    print("  Sets the refresh probability of the memory cell to 'num'. 'num' must be a float")
    print("  in the interval (0,1) (default = 0.05)\n")
    sys.exit(exitVal)



def main(argv):

    global bits
    global probFault
    global probRefresh
    global includeRewards

    bits = 3
    probFault = 0.5
    probRefresh = 0.5
    seed = 0
    includeRewards = True
    path = "../tests/stochastic/faulty/memory/"

    try:
        opts, args = getopt.getopt(argv,"b:p:q:r",["bits=","prob_fault","prob_refresh"])
    except getopt.GetoptError:
        usage(2)
    for opt, arg in opts:
        if opt == '-h':
            usage(0)
        elif opt in ("-b","bits=") :
            try :
                bits = int(arg)
            except ValueError :
                print("The bits must be a nonnegative integer")
                sys.exit(2)            
            if bits < 0 :
                print("The bits must be a nonnegative integer")
                sys.exit(2)
        elif opt in ("-p","prob_fail=") :
            try :
                probFault = float(arg)
            except ValueError :
                print("The failure probability of the robot must be a float in (0,1)")
                sys.exit(2)
            if probFault <= 0 or probFault >= 1 :
                print("The failure probability of the robot must be a float in (0,1)")
                sys.exit(2)
        elif opt in ("-q","prob_refresh=") :
            try :
                probRefresh = float(arg)
            except ValueError :
                print("The failure probability of the light must be a float in (0,1)")
                sys.exit(2)
            if probRefresh <= 0 or probRefresh >= 1 :
                print("The failure probability of the light must be a float in (0,1)")
                sys.exit(2)
        elif opt == "-r" :
            includeRewards = False


    writeMemoryTickMilestone(path+"memoryTickMilestone["+str(bits)+"-"+str(probRefresh)+"-"+str(probFault)+("" if includeRewards else "-NR")+"].mdp")
    writeMemoryRefreshMilestone(path+"memoryRefreshMilestone["+str(bits)+"-"+str(probRefresh)+"-"+str(probFault)+("" if includeRewards else "-NR")+"].mdp")



main(sys.argv[1:])
