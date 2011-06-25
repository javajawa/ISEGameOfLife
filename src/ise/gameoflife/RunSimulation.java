package ise.gameoflife;

import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.simulations.GenericSimulation;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.LogManager;
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
public class RunSimulation
{
	private final static Logger rootLogger = Logger.getLogger("");
	
	private RunSimulation()
	{
	}

	/**
	 * Sets up everything necessary to create the GUI,
	 * decode the XML and make Multi-Agent magic happen
	 * @param args Command line arguments
	 */
	public static void main(String args[]) throws InterruptedException
	{
		if (args.length == 0)
		{
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (ClassNotFoundException ex)
			{
				Logger.getLogger(RunSimulation.class.getName()).log(Level.SEVERE, null,
								ex);
			}
			catch (InstantiationException ex)
			{
				Logger.getLogger(RunSimulation.class.getName()).log(Level.SEVERE, null,
								ex);
			}
			catch (IllegalAccessException ex)
			{
				Logger.getLogger(RunSimulation.class.getName()).log(Level.SEVERE, null,
								ex);
			}
			catch (UnsupportedLookAndFeelException ex)
			{
				Logger.getLogger(RunSimulation.class.getName()).log(Level.SEVERE, null,
								ex);
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
					Class<?> sim = Class.forName("ise.gameoflife.simulations." + name);
					assert(GenericSimulation.class.isAssignableFrom(sim));

					long x = Long.parseLong(args[1]);
					GenericSimulation g = (GenericSimulation)sim.getConstructor(long.class).newInstance(x);
					
					name += Long.toHexString(x);
					
					presage.Presage.main(new String[]{g.getPath() + File.separator + "sim.xml"});
					return;
				}
				catch (InstantiationException ex)
				{
					Logger.getLogger(RunSimulation.class.getName()).log(Level.SEVERE, null,
									ex);
				}
				catch (IllegalAccessException ex)
				{
					Logger.getLogger(RunSimulation.class.getName()).log(Level.SEVERE, null,
									ex);
				}
				catch (IllegalArgumentException ex)
				{
					Logger.getLogger(RunSimulation.class.getName()).log(Level.SEVERE, null,
									ex);
				}
				catch (InvocationTargetException ex)
				{
					Logger.getLogger(RunSimulation.class.getName()).log(Level.SEVERE, null,
									ex);
				}
				catch (NoSuchMethodException ex)
				{
					Logger.getLogger(RunSimulation.class.getName()).log(Level.SEVERE, null,
									ex);
				}
				catch (SecurityException ex)
				{
					Logger.getLogger(RunSimulation.class.getName()).log(Level.SEVERE, null,
									ex);
				}
				catch (ClassNotFoundException ex)
				{
					Logger.getLogger(RunSimulation.class.getName()).log(Level.SEVERE, null,
									ex);
				}
			}
		}
	}

}
