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
	 * TODO: Documentation
	 * @param args
	 */
	public static void main(String args[])
	{
		BuildSimulations.main(args);
		ControlCenter.main(args);
	}
}
