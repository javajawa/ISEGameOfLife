package ise.mace;

import ise.mace.simulations.AgentsAndGroup;
import ise.mace.simulations.FreeAgentsTest;
import ise.mace.simulations.Loans;
import ise.mace.simulations.PoliticsExtended;
import ise.mace.simulations.validation.HungryAgent;
import ise.mace.simulations.Politics;
import ise.mace.simulations.WarGames;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Magic simulation building class
 * Add classes to {@link #simulationClasses} to have them auto-compiled
 */
final class BuildSimulations
{
	/**
	 * Logger for this class
	 */
	private final static Logger logger = Logger.getLogger("mace.SimBuilder");
	/**
	 * <p>The list of different simulation classes that can be built without
	 * initialisation parameters.</p>
	 *
	 * <p>If you design a simulation that requires a random seed, it can be built on
	 * the command line with:<br />
	 * <code>&lt;Classname&gt; &lt;seed&gt;</code><br />
	 * e.g. <code>java -jar Mace.jar CLIPolitics 23</code>
	 * </p>
	 */
	private static final Class[] simulationClasses =
	{
		HungryAgent.class,
		AgentsAndGroup.class,
		Politics.class,
		PoliticsExtended.class,
		PoliticsExtended.class,
		FreeAgentsTest.class,
		Loans.class,
		WarGames.class
	};

	/**
	 * Private constructor for a static main class
	 */
	private BuildSimulations()
	{
		// Nothing to see here. Move along, citizen!
	}

	/**
	 * Build the automatically built classes
	 * @param args Command line arguments - are ignored
	 */
	public static void main(String args[])
	{
		for (Class<?> c : simulationClasses)
		{
			try
			{
				c.newInstance();
			}
			catch (Exception ex)
			{
				logger.log(Level.SEVERE, null, ex);
			}
		}
	}
}
