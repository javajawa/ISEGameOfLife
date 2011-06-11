
Support: bh308@imperial.ac.uk

Installation
============

 - Clone the repo from GitHub
 - Initialise the presage submodule
  - Terminal / GitShell
  - <code>cd /path/to/repo</code>
  - <code>git submodule update --init</code>
 - Open netbeans
 - Open project (the repo folder will appear as a project to netbeans)
 - Right click on project, open required projects
  - Should open the presage project
 - Right click on presage, select clean and build
 - Right click on GameOfLife
  - Properties
  - Compiling
  - Ensure compile on save is on if you plan to change things
 - Clean and Build GameOfLife
 - You should be set to build and run simulations (see below)

Building Simulations
====================

 - Each simulation is a main-class in <code>ise.gameoflife.simulations</code>
 - Running any of these classes will build the simulation related to that class

Running Simulations
===================

 - Used the <code>ise.gameoflife.RunSimulation</code>
 - This is the default main class in netbeans
 - Run using F6 or the run button
 - Simulations will be in the simulation folder

Running with Default Simulations
====================

 - Right click on GameOfLife
 - Properties
 - Run
 - Change Arguments field to: <code>simulations\doubleagent\sim.xml</code> (or similar)
 - Run using F6 or the run button
 - The select simulation step skipped and simulation in Argument field is launched.
