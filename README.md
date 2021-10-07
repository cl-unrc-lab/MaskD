This is the source code and binaries distribution of the Masking Distance Tool MaskD,

Compile:
$ ant compile jar

Clean:
$ ant clean

Run:
$ cd bin
$ ./maskD (then follow the help instructions). 

Examples can be found
in the directory: tests/

To replicate all experimental results shown on paper "Measuring Masking Fault-Tolerance" (Castro, D’Argenio, Demasi, Putruele), run the script: ./mask-test . For the journal extended version, the algorithms for deterministic games can be run with: ./mask-test-det



