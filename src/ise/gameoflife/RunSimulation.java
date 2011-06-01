package ise.gameoflife;

import presage.gui.ControlCenter;

/**
 *Runs a Simulation
 * @author Benedict
 */
public class RunSimulation
{

	private RunSimulation()
	{
	}

	/**
	 * Sets up everything necessary to create the GUI,
	 * decode the XML and make Multi-Agent magic
	 * happen
	 * @param args
	 */
	public static void main(String args[])
	{
		BuildSimulations.main(args);
		ControlCenter.main(args);
	}
}
