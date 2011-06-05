package ise.gameoflife;

import ise.gameoflife.simulatons.AgentsAndGroup;
import ise.gameoflife.simulatons.DoubleAgent;
import ise.gameoflife.simulatons.FreeAgentsTest;
import ise.gameoflife.simulatons.SingleAgent;
import ise.gameoflife.simulatons.Politics;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Magic simulation building class
 * Add classes to the simulationClasses const to have them auto-compiled
 * @author Benedict
 */
final class BuildSimulations
{

	private static final Class<?>[] simulationClasses =
	{
		SingleAgent.class,
		DoubleAgent.class,
		AgentsAndGroup.class,
		Politics.class,
		FreeAgentsTest.class
	};

	private BuildSimulations()
	{
		// Nothing to see here. Move along, citizen!
	}

	/**
	 * Build the automatically built classes
	 * @param args Command line arguments
	 */
	public static void main(String args[])
	{

		Object passedArgv[] =
		{
			args
		};

		for (Class<?> c : simulationClasses)
		{
			try
			{
				c.getMethod("main", args.getClass()).invoke(null, passedArgv);
			}
			catch (IllegalAccessException ex)
			{
				Logger.getLogger(BuildSimulations.class.getName()).log(Level.SEVERE,
								null, ex);
			}
			catch (IllegalArgumentException ex)
			{
				Logger.getLogger(BuildSimulations.class.getName()).log(Level.SEVERE,
								null, ex);
			}
			catch (InvocationTargetException ex)
			{
				Logger.getLogger(BuildSimulations.class.getName()).log(Level.SEVERE,
								null, ex);
			}
			catch (NoSuchMethodException ex)
			{
				Logger.getLogger(BuildSimulations.class.getName()).log(Level.SEVERE,
								null, ex);
			}
			catch (SecurityException ex)
			{
				Logger.getLogger(BuildSimulations.class.getName()).log(Level.SEVERE,
								null, ex);
			}
		}
	}

}
