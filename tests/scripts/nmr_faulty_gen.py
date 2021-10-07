#!/usr/bin/python

import sys, getopt
import random
import math

def writePreamble(mFile) :

    # a depiction of the board as a comment
    mFile.write("// N-Modular Redundancy\n")


def writeNMR(fileName) :

    mFile = open(fileName,"w")

    writePreamble(mFile)

    mFile.write("Process NMR {\n")
    mFile.write("    v: INT; //0.."+str(modules)+"\n")
    mFile.write("    s:INT; //0..1    0=normal, 1=faulty\n")
    mFile.write("\n")
    
    mFile.write("    Initial : v==0 && s==0;\n")
    mFile.write("\n")

    mFile.write("    // environment input\n")
    mFile.write("\n")
    mFile.write("    [i0] true -> v=0;\n")
    mFile.write("    [i1] true -> v="+str(modules)+";\n")
    mFile.write("\n")

    mFile.write("    // vote\n")
    mFile.write("\n")
    mFile.write("    [v0] v<="+str(modules//2)+" -> v=v;\n")
    mFile.write("    [v1] v>"+str(modules//2)+" -> v=v;\n")
    mFile.write("\n")

    mFile.write("    // tick\n")
    mFile.write("\n")
    mFile.write("    [tick] <1> s==0 && v<"+str(modules)+"->"+str(1-probFault)+": v=v\n")
    mFile.write("                    ++ "+str(probFault)+": s=1;\n")
    mFile.write("    [tick] <1> s==1 || v=="+str(modules)+"-> v=v;\n")
    mFile.write("\n")

    mFile.write("    // faults\n")
    mFile.write("\n")
    mFile.write("    [fault1] faulty s==1 && v<"+str(modules)+"-> s=0, v=v+1;\n")
    mFile.write("    [fault1] faulty s==1 && v>="+str(modules)+"-> s=0, v="+str(modules-1)+";\n")
    mFile.write("    [fault2] faulty s==1 && v>0-> s=0, v=v-1;\n")
    mFile.write("    [fault2] faulty s==1 && v<=0-> s=0, v=1;\n")
    mFile.write("\n")

    mFile.write("}\n\n")

    mFile.write("Main() {\n")
    mFile.write("    m : NMR ;\n")
    mFile.write("    run m() ;\n")
    mFile.write("}\n")

    mFile.close()




def usage(exitVal) :

    print("\nusage nmr_faulty_gen.py [-h]\n")
    print("-h :\n")
    print("  Print this help\n")
    print("-r :\n")
    print("-m num :\n")
    print("  Sets the number of modules\n")
    print("-p num :\n")
    print("  Sets the failure probability of a module to fail to 'num'. 'num' must be a float")
    print("  in the interval (0,1) (default = 0.1)\n")
    print("  Exclude the rewards setting\n")
    sys.exit(exitVal)



def main(argv):

    global modules
    global probFault
    global includeRewards

    modules = 3
    probFault = 0.5
    includeRewards = True
    path = "../tests/stochastic/faulty/nmr/"

    try:
        opts, args = getopt.getopt(argv,"m:p:r",["modules=","prob_fault"])
    except getopt.GetoptError:
        usage(2)
    for opt, arg in opts:
        if opt == '-h':
            usage(0)
        elif opt in ("-m","modules=") :
            try :
                modules = int(arg)
            except ValueError :
                print("The modules must be a nonnegative integer")
                sys.exit(2)            
            if modules < 0 :
                print("The modules must be a nonnegative integer")
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
        elif opt == "-r" :
            includeRewards = False


    writeNMR(path+"nmr["+str(modules)+"-"+str(probFault)+("" if includeRewards else "-NR")+"].mdp")



main(sys.argv[1:])
