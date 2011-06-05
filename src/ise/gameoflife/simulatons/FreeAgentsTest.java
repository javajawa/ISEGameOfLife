package ise.gameoflife.simulatons;

import ise.gameoflife.agents.TestAgent;
import ise.gameoflife.agents.TestGroupableAgent;
import ise.gameoflife.environment.EnvironmentDataModel;
import ise.gameoflife.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.NameGenerator;
import ise.gameoflife.participants.AbstractAgent;
import ise.gameoflife.plugins.ErrorLog;
import ise.gameoflife.plugins.HuntersAlivePlugin;
import ise.gameoflife.plugins.DatabasePlugin;
import ise.gameoflife.plugins.DebugSwitchPlugin;
import ise.gameoflife.plugins.HunterListPlugin;
import ise.gameoflife.plugins.PoliticalCompassPlugin;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
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
 * @author Benedict Harcourt
 */
public class FreeAgentsTest
{

	public static void main(String args[])
	{
		PresageConfig presageConfig = new PresageConfig();

		// Basic config
		presageConfig.setComment("Test of 5 agents with no groups avaialable");
		presageConfig.setIterations(200);
		presageConfig.setRandomSeed(0);

		presageConfig.setOutputFolder(new File(System.getProperty("user.dir"), "output").getAbsolutePath());
		presageConfig.setThreadDelay(1);
		presageConfig.setAutorun(false);

		// Path configuarations
		String configPath = new File(System.getProperty("user.dir"),"simulations/freegrouping").getAbsolutePath();
		presageConfig.setPluginsConfigPath(configPath + "/plugins.xml");
		presageConfig.setEventscriptConfigPath(configPath + "/methods.xml");
		presageConfig.setParticipantsConfigPath(configPath + "/participants.xml");
		presageConfig.setEnvironmentConfigPath(configPath + "/environment.xml");

		NameGenerator.setRandomiser(new Random(700));
		// All the big objects
		PluginManager pm = new PluginManager();
		pm.addPlugin(new DebugSwitchPlugin());		
		pm.addPlugin(new HuntersAlivePlugin(configPath + "/population.png", 1500, 1200));
		pm.addPlugin(new ErrorLog());
		pm.addPlugin(new DatabasePlugin(1,"Simulation comment",false));
		pm.addPlugin(new HunterListPlugin());
		pm.addPlugin(new PoliticalCompassPlugin()); // Use this for just a display of the political compass

		TreeMap<String, Participant> parts = new TreeMap<String, Participant>();

		EventScriptManager ms = new EventScriptManager();

		AbstractAgent a;
		
		for (int i = 0; i < 5; i++)
		{
			a = new TestGroupableAgent(20, 2);
			parts.put(a.getId(), a);
			ms.addPreEvent(new ScriptedEvent(-1, new ActivateParticipant(a.getId())));
		}
		for (int i = 0; i < 1; i++)
		{
			a = new TestAgent(20, 2);
			parts.put(a.getId(), a);
			ms.addPreEvent(new ScriptedEvent(-1, new ActivateParticipant(a.getId())));
		}

		HashMap<String, Food> foods = new HashMap<String, Food>();
		Food rabbit = new Food("rabbit", 1, 1);
		foods.put(rabbit.getId().toString(), rabbit);
		Food stag = new Food("stag", 5, 2);
		foods.put(stag.getId().toString(), stag);
		
		@SuppressWarnings("unchecked")
		EnvironmentDataModel dm = new EnvironmentDataModel("Single Certain Death", foods, Collections.EMPTY_LIST, 0.1);
		Environment environment = (Environment)new ise.gameoflife.environment.Environment(true, 0, dm, BasicFreeAgentGroup.class);

		presageConfig.setEnvironmentClass(environment.getClass());
		ConfigurationWriter.write(configPath + "/sim.xml", presageConfig, parts, environment, pm, ms);
	}

	private FreeAgentsTest()
	{
		// Nothing to see here. Move along, citizen!
	}

}
