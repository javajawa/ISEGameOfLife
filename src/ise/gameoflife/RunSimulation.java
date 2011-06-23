package ise.gameoflife;

import ise.gameoflife.simulations.GenericSimulation;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.UIManager;
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
	public static void main(String args[]) throws Exception
	{
		if (args.length == 0)
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			BuildSimulations.main(args);
			ControlCenter.main(args);
		}
		else
		{
			Logger.getLogger("").setLevel(Level.WARNING);

			if (args.length == 1)
			{
				presage.Presage.main(args);
				return;
			}
				
			if (args.length == 2)
			{
				String name = args[0];
				Class<?> sim = Class.forName("ise.gameoflife.simulations." + name);
				assert(GenericSimulation.class.isAssignableFrom(sim));

				long x = Long.parseLong(args[1]);
				GenericSimulation g = (GenericSimulation)sim.getConstructor(long.class).newInstance(x);
				
				name += Long.toHexString(x);
				
				presage.Presage.main(new String[]{g.getPath() + File.separator + "sim.xml"});
				return;
			}
		}
	}

}
