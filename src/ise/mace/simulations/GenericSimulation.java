package ise.mace.simulations;

import ise.mace.environment.Environment;
import ise.mace.environment.EnvironmentDataModel;
import ise.mace.models.Food;
import ise.mace.models.GroupDataInitialiser;
import ise.mace.models.NameGenerator;
import ise.mace.participants.AbstractAgent;
import ise.mace.participants.AbstractFreeAgentGroup;
import ise.mace.participants.AbstractGroupAgent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import presage.EventScriptManager;
import presage.Participant;
import presage.Plugin;
import presage.PluginManager;
import presage.PresageConfig;
import presage.ScriptedEvent;
import presage.events.CoreEvents.ActivateParticipant;
import static presage.configure.ConfigurationWriter.write;

/**
 *
 */
abstract public class GenericSimulation
{
	/**
	 * Foods added to the simulation
	 */
	private final HashMap<String, Food> foods = new HashMap<String, Food>();
	/**
	 * Agents added to the simulation
	 */
	private final TreeMap<String, Participant> agents = new TreeMap<String, Participant>();
	/**
	 * Groups added to the simulation
	 */
	private final ArrayList<Class<? extends AbstractGroupAgent>> groups = new ArrayList<Class<? extends AbstractGroupAgent>>();
	/**
	 * Event manger for the simulation
	 */
	private final EventScriptManager ms = new EventScriptManager();
	/**
	 * Plugin manager for the simulation
	 */
	private final PluginManager pm = new PluginManager();
	/**
	 * Path at which file will be written
	 * @see #getPath()
	 */
	private final String configPath;
	/**
	 * Random seed to be used with this simulation
	 */
	protected final long randomSeed;
	/**
	 * Comment for the simulation
	 */
	protected final String comment;

	/**
	 * Build the simulation
	 * @param comment Comment for the simulation
	 * @param iterations Number of cycles to run for
	 * @param randomSeed The random seed to use with this simulation
	 * @param foodConsumedPerAdvice Amount of food consumed per advice;
	 * see {@link EnvironmentDataModel#foodConsumedPerAdvice}
	 */
	public GenericSimulation(String comment, int iterations, long randomSeed,
					double foodConsumedPerAdvice)
	{
		this(comment, iterations, randomSeed, foodConsumedPerAdvice, "");
	}

	/**
	 * Build the simulation
	 * @param comment Comment for the simulation
	 * @param iterations Number of cycles to run for
	 * @param randomSeed The random seed to use with this simulation
	 * @param foodConsumedPerAdvice Amount of food consumed per advice;
	 * see {@link EnvironmentDataModel#foodConsumedPerAdvice}
	 * @param suffix Suffix for the simulations generated name (and path)
	 */
	@SuppressWarnings("OverridableMethodCallInConstructor")
	public GenericSimulation(String comment, int iterations, long randomSeed,
					double foodConsumedPerAdvice, String suffix)
	{
		PresageConfig presageConfig = new PresageConfig();
		this.comment = comment;
		this.randomSeed = randomSeed;
		presageConfig.setComment(comment);
		presageConfig.setIterations(iterations);
		presageConfig.setRandomSeed(randomSeed);
		presageConfig.setThreadDelay(1);
		presageConfig.setAutorun(false);
		presageConfig.setEnvironmentClass(Environment.class);

		File rootFile = new File(System.getProperty("user.dir"));
		// Path configuarations
		File path = new File(rootFile,
						"simulations/" + this.getClass().getSimpleName() + suffix);
		configPath = path.getAbsolutePath();

		presageConfig.setPluginsConfigPath(configPath + "/plugins.xml");
		presageConfig.setEventscriptConfigPath(configPath + "/methods.xml");
		presageConfig.setParticipantsConfigPath(configPath + "/participants.xml");
		presageConfig.setEnvironmentConfigPath(configPath + "/environment.xml");

		EnvironmentDataModel dm = new EnvironmentDataModel(comment, foods, groups,
						foodConsumedPerAdvice);
		Environment e = new Environment(randomSeed, dm,
						chooseFreeAgentHandler());

		NameGenerator.setRandomiser(new Random(randomSeed));
		foods();
		agents();
		groups();
		plugins();
		events();

		write(configPath + "/sim.xml", presageConfig, agents, e, pm, ms);
	}

	/**
	 * Place to put calls to {@link #addPlugin(presage.Plugin) addPlugin}
	 */
	abstract protected void plugins();

	/**
	 * Place to put calls to {@link
	 * #addFood(comment, randomSeed, huntersRequired) addFood}
	 */
	abstract protected void foods();

	/**
	 * Place to put calls to {@link
	 * #addAgent(ise.mace.participants.AbstractAgent) addAgent}
	 */
	abstract protected void agents();

	/**
	 * Place to put calls to {@link #addGroup(java.lang.Class) addGroup}
	 */
	abstract protected void groups();

	/**
	 * Place to put calls to {@link #addEvent addEvent}
	 */
	abstract protected void events();

	/**
	 * Select which class is used to model the interactions of the agents that are
	 * not members of a group
	 * @return The class that is going to be used
	 * @see AbstractFreeAgentGroup
	 */
	abstract protected Class<? extends AbstractFreeAgentGroup> chooseFreeAgentHandler();

	/**
	 * Add a food to the Environment
	 * @param name Name of the food
	 * @param nutrition The amount of nutrition that one of this food supplies
	 * @param huntersRequired The number of {@link AbstractAgent Agents} needed
	 * to get one unit of this food
	 * @see Food
	 */
	protected final void addFood(String name, double nutrition,
					int huntersRequired)
	{
		Food f = new Food(name, nutrition, huntersRequired);
		this.foods.put(f.getId().toString(), f);
	}

	/**
	 * Adds an {@link AbstractAgent agent} to the {@link Environment}.
	 *
	 * It will be automatically activated at the beginning of the simulation
	 * @param a The agent to add
	 */
	protected final void addAgent(AbstractAgent a)
	{
		agents.put(a.getId(), a);
		ms.addPreEvent(new ScriptedEvent(-1, new ActivateParticipant(a.getId())));
	}

	/**
	 * Adds a class of group to the list of groups that can be used in this
	 * simulation.
	 *
	 * It should not be abstract, and a have a public constructor that takes a
	 * single parameter of the class {@link GroupDataInitialiser}
	 * @param g The class to add
	 * @see AbstractGroupAgent
	 */
	protected final void addGroup(Class<? extends AbstractGroupAgent> g)
	{
		groups.add(g);
	}

	/**
	 * Adds a plugin to the simulation
	 * @param p The plugin to add
	 */
	protected final void addPlugin(Plugin p)
	{
		pm.addPlugin(p);
	}

	/**
	 * Returns the path to which the files will be / were written when generating
	 * the simulation code.
	 *
	 * The primary file has the path of
	 * <code>getPath + File.separator + "sim.xml"</code>
	 * @return The path to the simulation files.
	 */
	public final String getPath()
	{
		return configPath;
	}
}
