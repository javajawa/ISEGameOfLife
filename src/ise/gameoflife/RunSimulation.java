package ise.gameoflife;

import ise.gameoflife.plugins.ErrorLog;
import presage.PluginManager;
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

	public static void main(String args[])
	{
		ControlCenter.main(args);
		
		PluginManager pm = new PluginManager();
		pm.addPlugin(new ErrorLog());
		

	}
}
