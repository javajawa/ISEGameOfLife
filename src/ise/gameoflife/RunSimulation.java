package ise.gameoflife;

import presage.gui.ControlCenter;

/**
 * Runs a Simulation, by calling the default {@code presage.ControlCentre}.
 * The Simulations that are listed in the {@link BuildSimulations} auto-build
 * class will be build before the control centre is launched.
 * @author Benedict Harcourt
 */
public class RunSimulation
{

	private RunSimulation()
	{
	}

	/**
	 * Sets up everything necessary to create the GUI,
	 * decode the XML and make Multi-Agent magic happen
	 * @param args Command line arguments
	 */
	public static void main(String args[])
	{
		BuildSimulations.main(args);
		ControlCenter.main(args);
	}

}
