MaskD: A Tool for Measuring Masking Fault-Tolerance

This is the source code and binaries distribution of the Masking Distance Tool MaskD. It requires ant for building.

Compile:
$ ant compile jar

Clean:
$ ant clean

Run:
$ cd bin
$ ./maskD <options> <specification path> <implementation path>

Options:
      -nb : toggle if faulty model do not need to simulate nominal model
      -det : use deterministic masking distance algorithm
      -d : create dot file
      -t : print error trace (uses det. algorithm)
      -s : start simulation
      -l : also treat deadlock as error state
      -v : turn verbosity on

Examples can be found
in the directory: tests/standard/


To replicate all experimental results (Fix-Point Algorithm) from the paper: "MaskD: A Tool for Measuring Masking Fault-Tolerance" run the script from bin: ./mask-test  
For the Shortest Path Algorithm experiments run: ./mask-test-det