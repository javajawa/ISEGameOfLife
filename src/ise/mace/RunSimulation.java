package ise.mace;

import ise.mace.environment.PublicEnvironmentConnection;
import ise.mace.simulations.GenericSimulation;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import presage.gui.ControlCenter;

/**
 * Runs a Simulation, by calling the default {@code presage.ControlCentre}.
 * The Simulations that are listed in the {@link BuildSimulations} auto-build
 * class will be build before the control centre is launched.
 * @author Benedict Harcourt
 */
@SuppressWarnings("ClassWithMultipleLoggers")
public class RunSimulation
{
	/**
	 * Hard Reference to root logger to ensure warnings+ only in CLI mode
	 */
	private final static Logger rootLogger = Logger.getLogger("");
	/**
	 * Logger for this class
	 */
	private final static Logger logger = Logger.getLogger("mace.RunSimulation");
	
	/**
	 * Private constructor for static main class
	 */
	private RunSimulation()
	{
		// Nothing to see here. Move along, citizen!
	}

	/**
	 * Sets up everything necessary to create the GUI,
	 * decode the XML and make Multi-Agent magic happen
	 * @param args Command line arguments
	 * @throws InterruptedException Thrown if CLI mode has a threading issue
	 */
	public static void main(String args[]) throws InterruptedException
	{
		if (args.length == 0)
		{
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (Exception ex)
			{
				logger.log(Level.SEVERE, "Error while setting UI mode", ex);
			}

			BuildSimulations.main(args);
			ControlCenter.main(args);
		}
		else
		{
			
			rootLogger.setLevel(Level.WARNING);
			PublicEnvironmentConnection.logger.setLevel(Level.WARNING);

			if (args.length == 1)
			{
				presage.Presage.main(args);
				return;
			}
				
			if (args.length == 2)
			{
				try
				{
					String name = args[0];
					Class<?> sim = Class.forName("ise.mace.simulations." + name);
					assert(GenericSimulation.class.isAssignableFrom(sim));

					long x = Long.parseLong(args[1]);
					GenericSimulation g = (GenericSimulation)sim.getConstructor(long.class).newInstance(x);
					
					name += Long.toHexString(x);
					
					presage.Presage.main(new String[]{g.getPath() + File.separator + "sim.xml"});
					return;
				}
				catch (Exception ex)
				{
					logger.log(Level.SEVERE, null, ex);
				}
			}
		}
	}

}
