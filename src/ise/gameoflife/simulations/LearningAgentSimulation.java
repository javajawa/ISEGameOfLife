/**
 *
 */
package ise.gameoflife.simulations;

import ise.gameoflife.RunSimulation;
import ise.gameoflife.environment.Environment;
import ise.gameoflife.environment.EnvironmentDataModel;
import ise.gameoflife.genetics.EvolvableEntity;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.NameGenerator;
import ise.gameoflife.participants.AbstractFreeAgentGroup;
import ise.gameoflife.participants.AbstractGroupAgent;
import ise.gameoflife.simulations.evolution.SimulationGenome;
import ise.gameoflife.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.gameoflife.agents.TestPoliticalAgent;
import ise.gameoflife.groups.TestPoliticalGroup;
import ise.gameoflife.tokens.AgentType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;

import presage.EventScriptManager;
import presage.Participant;
import presage.PluginManager;
import presage.PresageConfig;
import presage.Simulation;
import presage.configure.ConfigurationLoader;

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
	private Simulation sim;

	private final HashMap<String, Food> foods = new HashMap<String, Food>();
	private final TreeMap<String, Participant> agents = new TreeMap<String, Participant>();
	private final ArrayList<Class<? extends AbstractGroupAgent>> groups = new ArrayList<Class<? extends AbstractGroupAgent>>();
	private final EventScriptManager ms = new EventScriptManager();
	private final PluginManager pm = new PluginManager();

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
		presageConfig.setThreadDelay(1);
		presageConfig.setAutorun(true);
		presageConfig.setEnvironmentClass(Environment.class);

		EnvironmentDataModel dm = new EnvironmentDataModel(comment, foods, groups, foodConsumedPerAdvice);
		Environment e = new Environment(true, randomSeed, dm, chooseFreeAgentHandler());

		NameGenerator.setRandomiser(new Random(randomSeed));
		foods();
		agents();
		groups();
		plugins();
		events();

		this.sim = new Simulation(presageConfig, agents, e, pm, ms);
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
			// this.addAgent(new LearningAgent(20, 2));
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

	protected Class<? extends AbstractFreeAgentGroup> chooseFreeAgentHandler()
	{
		return BasicFreeAgentGroup.class;
	}

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
	public void setFitness(double aFitness)
	{
		this.fitness = aFitness;
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
