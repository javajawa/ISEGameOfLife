package ise.gameoflife.simulations;

import ise.gameoflife.environment.Environment;
import ise.gameoflife.environment.EnvironmentDataModel;
import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.genetics.EvolvableEntity;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.NameGenerator;
import ise.gameoflife.participants.AbstractAgent;
import ise.gameoflife.participants.AbstractGroupAgent;
import ise.gameoflife.participants.PublicAgentDataModel;
import ise.gameoflife.simulations.evolution.SimulationGenome;
import ise.gameoflife.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.gameoflife.agents.GeneticAgent;
import ise.gameoflife.agents.TestPoliticalAgent;
import ise.gameoflife.groups.TestPoliticalGroup;
import ise.gameoflife.tokens.AgentType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import presage.events.CoreEvents.ActivateParticipant;
import presage.EventScriptManager;
import presage.Participant;
import presage.Plugin;
import presage.PluginManager;
import presage.PresageConfig;
import presage.ScriptedEvent;
import presage.Simulation;

/**
 * @author admko
 *
 */
public class GeneticAgentSimulation extends EvolvableEntity<SimulationGenome>
{

	private SimulationGenome genome = null;
	private double fitness = -1;

	private String comment = null;
	private int iterations = 0;
	private double foodConsumedPerAdvice = 0;
	private long randomSeed = 0;
	@SuppressWarnings("unused")
	private Simulation simulation;

	private final HashMap<String, Food> foods = new HashMap<String, Food>();
	private final TreeMap<String, Participant> agents = new TreeMap<String, Participant>();
	private final ArrayList<Class<? extends AbstractGroupAgent>> groups = new ArrayList<Class<? extends AbstractGroupAgent>>();
	private final EventScriptManager scriptManager = new EventScriptManager();
	private final PluginManager pluginManager = new PluginManager();

	private ArrayList<String> geneticAgentIds = new ArrayList<String>();

	public GeneticAgentSimulation(SimulationGenome genome)
	{
		super();
		this.setGenome(genome);
	}

	public void run()
	{
		PresageConfig presageConfig = new PresageConfig();
		presageConfig.setComment(comment);
		presageConfig.setIterations(iterations);
		presageConfig.setRandomSeed(randomSeed);
		presageConfig.setAutorun(true);
		presageConfig.setAsync(false);
		presageConfig.setEnvironmentClass(Environment.class);

		EnvironmentDataModel envDataModel = new EnvironmentDataModel
				(comment, foods, groups, foodConsumedPerAdvice);
		Environment environment = new Environment
				(true, randomSeed, envDataModel, BasicFreeAgentGroup.class);

		NameGenerator.setRandomiser(new Random(randomSeed));
		foods();
		agents();
		groups();
		plugins();
		events();

		Logger.getLogger("presage.Simulation").setLevel(Level.SEVERE);
		Logger.getLogger("gameoflife.AbstractAgent").setLevel(Level.SEVERE);
		this.simulation = new Simulation
				(presageConfig, agents, environment, pluginManager, scriptManager);
	}

	public ArrayList<PublicAgentDataModel> agentDataModels()
	{
		if (geneticAgentIds.isEmpty())
		{
			return null;
		}

		ArrayList<PublicAgentDataModel> dataModels = new ArrayList<PublicAgentDataModel>();
		PublicEnvironmentConnection envConn = PublicEnvironmentConnection.getInstance();

		for (String id : geneticAgentIds)
		{
			if (null == id) continue;
			dataModels.add((PublicAgentDataModel)envConn.getAgentById(id));
		}

		return dataModels;
	}

	protected void plugins(){}

	protected void foods()
	{
		this.addFood("Rabbit", 2, 1);
		this.addFood("Stag", 5, 2);
	}

	private int ordinal = 0;
	private AgentType nextAgentType()
	{
		if (ordinal == AgentType.values().length)
		{
			ordinal = 0;
		}
		return AgentType.values()[ordinal++];
	}

	protected void agents()
	{
		Random rand = new Random(this.randomSeed);
		for (int i = 0; i < genome.population()-1; i++)
		{
			// competing agents
			this.addAgent(new TestPoliticalAgent
				 (genome.initialFood(), genome.consumption(),
				  this.nextAgentType(), rand.nextDouble(), rand.nextDouble()));
		}

		// genetic agents
		this.addAgent(new GeneticAgent(genome));
	}

	protected void groups()
	{
		this.addGroup(TestPoliticalGroup.class);
	}

	protected void events(){}

	@Override
	public SimulationGenome genome()
	{
		return genome;
	}

	@Override
	public void setGenome(SimulationGenome genome)
	{
		this.genome = genome;

		this.comment = genome.comment();
		this.iterations = genome.iterations();
		this.randomSeed = genome.randomSeed();
		this.foodConsumedPerAdvice = genome.foodConsumedPerAdvice();
	}

	@Override
	public double fitness()
	{
		return fitness;
	}

	@Override
	public void setFitness(double fitness)
	{
		this.fitness = fitness;
	}

	public long randomSeed()
	{
		return randomSeed;
	}

	public void setRandomSeed(long seed)
	{
		this.randomSeed = seed;
	}

	protected final void addFood(String name, double nutrition, int huntersRequired)
	{
		Food f = new Food(name, nutrition, huntersRequired);
		this.foods.put(f.getId().toString(), f);
	}

	protected final void addAgent(AbstractAgent a)
	{
		// keep track of all genetic agents
		// will need them to tell how well they performed
		if (a.getClass() == GeneticAgent.class)
		{
			geneticAgentIds.add(a.getId());
		}

		agents.put(a.getId(), a);
		scriptManager.addPreEvent(new ScriptedEvent(-1, new ActivateParticipant(a.getId())));
	}

	protected final void addGroup(Class<? extends AbstractGroupAgent> g)
	{
		groups.add(g);
	}

	protected final void addPlugin(Plugin p)
	{
		pluginManager.addPlugin(p);
	}

	public static void main(String[] args)
	{
		SimulationGenome genome = new SimulationGenome(System.currentTimeMillis());
		genome.randomize();
		GeneticAgentSimulation sim = new GeneticAgentSimulation(genome);
		sim.run();
		ArrayList<PublicAgentDataModel> agentDataModels = sim.agentDataModels();
		for (PublicAgentDataModel dataModel : agentDataModels)
		{
			if (null == dataModel) continue;
			System.out.println(dataModel.getName() + ": " + dataModel.getCurrentHappiness());
		}
	}

}
