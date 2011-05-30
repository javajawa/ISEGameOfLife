package ise.gameoflife.simulatons;

import ise.gameoflife.agents.TestAgent;
import ise.gameoflife.enviroment.EnvironmentDataModel;
import ise.gameoflife.models.Food;
import java.io.File;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;
import presage.EventScriptManager;
import presage.Participant;
import presage.PluginManager;
import presage.PresageConfig;
import presage.ScriptedEvent;
import presage.events.CoreEvents.ActivateParticipant;
import presage.Environment;
import presage.configure.ConfigurationWriter;

/**
 *
 * @author Benedict
 */
public class SingleAgent
{

	public static void main(String args[])
	{
		PresageConfig presageConfig = new PresageConfig();

		// Basic config
		presageConfig.setComment("Simple Test of 1 agent consuming food until death");
		presageConfig.setIterations(25);
		presageConfig.setRandomSeed(0);

		presageConfig.setOutputFolder("tempoutput");
		presageConfig.setThreadDelay(1);
		presageConfig.setAutorun(false);

		// Path configuarations
		String configPath = new File(System.getProperty("user.dir"), "simulations/singleagent").getAbsolutePath();
		presageConfig.setPluginsConfigPath(configPath + "/plugins.xml");
		presageConfig.setEventscriptConfigPath(configPath + "/methods.xml");
		presageConfig.setParticipantsConfigPath(configPath + "/participants.xml");
		presageConfig.setEnvironmentConfigPath(configPath + "/environment.xml");

		// All the big objects
		TreeMap<String, Participant> parts = new TreeMap<String, Participant>();
		PluginManager pm = new PluginManager();
		EventScriptManager ms = new EventScriptManager();

		TestAgent a = new TestAgent(20, 2);

		parts.put(a.getId(), a);
		ms.addEvent(new ScriptedEvent(-1, new ActivateParticipant(a.getId())));

		HashMap<String, Food> foods = new HashMap<String, Food>();

		Food rabbit = new Food("rabbit", 1, 1);

		foods.put(rabbit.getId().toString(), rabbit);
                
                Food chicken = new Food("chicken", 2, 1);
                foods.put(chicken.getId().toString(),chicken);

		EnvironmentDataModel dm = new EnvironmentDataModel("Single Certain Death", foods);

		Environment environment = (Environment) new ise.gameoflife.enviroment.Environment(true, 0, dm);

		presageConfig.setEnvironmentClass(environment.getClass());

		ConfigurationWriter.write(configPath + "/sim.xml", presageConfig, parts, environment, pm, ms);
	}

	private SingleAgent()
	{
		// Nothing to see here. Move along, citizen!
	}
}
