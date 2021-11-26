MaskD: A Tool for Measuring Masking Fault-Tolerance

This is the source code and binaries distribution of the Masking Distance Tool MaskD.

Apache Ant is required in order to build the tool. The is a .deb package included in this repository.

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

The structure of the repository is as follows:
	
	/bin : Contains the binaries
	
	/doc : Contains the grammar of the MaskD language and the experiments made.
	
	/lib : Contains external libraries
	
	/src : Contains the source code
	
	/tests : Contains case study models 

A Demonstration Video is also contained in this repository (Demo.mp4), you can download or you can watch the video in your favorite browser via this link: https://watch.screencastify.com/v/PPS70oPWLysCMDVYJ3Ap

To replicate all experimental results (Fix-Point Algorithm) from the paper: "MaskD: A Tool for Measuring Masking Fault-Tolerance" run:

	$ cd bin

	$ ./mask-test
	
For the Shortest Path Algorithm experiments run:

	$ cd bin

	$ ./mask-test-det

Warning: some experiments may take a lot of time (See Appendix of paper), namely: Reduntant Memory Cell (11 bits), N-Modular Redundancy (11 modules), Dining Philosophers (6 philosophers), Byzantine Generals (5 generals) and Raft (3 followers).






