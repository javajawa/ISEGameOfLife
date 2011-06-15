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
				if (args[1].equalsIgnoreCase("--rebuild"))
				{
					String name = args[0];
					if (name.lastIndexOf(File.separator) > -1)
					{
						name = name.substring(name.lastIndexOf(File.separator)+1);
					}

					Class<?> sim = Class.forName("ise.gameoflife.simulations." + name);
					assert(GenericSimulation.class.isAssignableFrom(sim));

					sim.newInstance();
					presage.Presage.main(new String[]{args[0] + File.separator + "sim.xml"});
				}
				else
				{
					presage.Presage.main(new String[]{args[0] + File.separator + args[1] + File.separator + "sim.xml"});
				}
				return;
			}

			// Source dir, clasname, rebuild flag
			if (args.length == 3)
			{
				String name = args[1];

				if (args[2].equalsIgnoreCase("--rebuild"))
				{
					Class<?> sim = Class.forName("ise.gameoflife.simulations." + name);
					assert(GenericSimulation.class.isAssignableFrom(sim));

					sim.newInstance();
				}
				presage.Presage.main(new String[]{args[0] + File.separator + name + File.separator + "sim.xml"});
				return;
			}
		}
	}

}
