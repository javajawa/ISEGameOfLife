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
public class LearningAgentSimulation extends EvolvableEntity<SimulationGenome>
{

	private SimulationGenome genome = null;
	private double fitness = -1;

	private String comment = null;
	private int iterations = 0;
	private double foodConsumedPerAdvice = 0;
	private long randomSeed = 0;
	private Simulation simulation;

	private final HashMap<String, Food> foods = new HashMap<String, Food>();
	private final TreeMap<String, Participant> agents = new TreeMap<String, Participant>();
	private final ArrayList<Class<? extends AbstractGroupAgent>> groups = new ArrayList<Class<? extends AbstractGroupAgent>>();
	private final EventScriptManager ms = new EventScriptManager();
	private final PluginManager pm = new PluginManager();

	private ArrayList<String> geneticAgentIds = new ArrayList<String>();

	public LearningAgentSimulation(SimulationGenome genome)
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

		EnvironmentDataModel dm = new EnvironmentDataModel(comment, foods, groups, foodConsumedPerAdvice);
		Environment e = new Environment(true, randomSeed, dm, BasicFreeAgentGroup.class);

		NameGenerator.setRandomiser(new Random(randomSeed));
		foods();
		agents();
		groups();
		plugins();
		events();

		this.simulation = new Simulation(presageConfig, agents, e, pm, ms);
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

	protected void agents()
	{
		Random rand = new Random(this.randomSeed);
		for (int i = 0; i < 10; i++)
		{
			// genetic agents
			GeneticAgent agent = new GeneticAgent(genome);
			geneticAgentIds.add(agent.getId());
			this.addAgent(agent);
			// competing agents
			this.addAgent(new TestPoliticalAgent
				 (20, 2, AgentType.AC, rand.nextDouble(), rand.nextDouble()));
			this.addAgent(new TestPoliticalAgent
				 (20, 2, AgentType.TFT, rand.nextDouble(), rand.nextDouble()));
			this.addAgent(new TestPoliticalAgent
				 (20, 2, AgentType.AD, rand.nextDouble(), rand.nextDouble()));
			this.addAgent(new TestPoliticalAgent
				 (20, 2, AgentType.R, rand.nextDouble(), rand.nextDouble()));
		}
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

	protected final void addFood(String name, double nutrition, int huntersRequired)
	{
		Food f = new Food(name, nutrition, huntersRequired);
		this.foods.put(f.getId().toString(), f);
	}

	protected final void addAgent(AbstractAgent a)
	{
		agents.put(a.getId(), a);
		ms.addPreEvent(new ScriptedEvent(-1, new ActivateParticipant(a.getId())));
	}

	protected final void addGroup(Class<? extends AbstractGroupAgent> g)
	{
		groups.add(g);
	}

	protected final void addPlugin(Plugin p)
	{
		pm.addPlugin(p);
	}

}
