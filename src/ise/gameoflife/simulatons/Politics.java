/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ise.gameoflife.simulatons;

import ise.gameoflife.environment.EnvironmentDataModel;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.NameGenerator;
import ise.gameoflife.plugins.ErrorLog;
import ise.gameoflife.plugins.HuntersAlivePlugin;
import ise.gameoflife.plugins.DatabasePlugin;
import ise.gameoflife.plugins.DebugSwitchPlugin;
import ise.gameoflife.plugins.HunterListPlugin;
import ise.gameoflife.plugins.PoliticalCompassPlugin;
import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import presage.EventScriptManager;
import presage.Participant;
import presage.PluginManager;
import presage.PresageConfig;
import presage.Environment;
import presage.configure.ConfigurationWriter;

/**
 *
 * @author george
 */
public class Politics
{

	public static void main(String Args[])
	{
		PresageConfig presageConfig = new PresageConfig();

		// Basic config
		presageConfig.setComment("Simple test for politics");
		presageConfig.setIterations(200);
		presageConfig.setRandomSeed(0);

		presageConfig.setOutputFolder(new File(System.getProperty("user.dir"),
						"output").getAbsolutePath());
		presageConfig.setThreadDelay(1);
		presageConfig.setAutorun(false);

		// Path configuarations
		String configPath = new File(System.getProperty("user.dir"),
						"simulations/politics").getAbsolutePath();
		presageConfig.setPluginsConfigPath(configPath + "/plugins.xml");
		presageConfig.setEventscriptConfigPath(configPath + "/methods.xml");
		presageConfig.setParticipantsConfigPath(configPath + "/participants.xml");
		presageConfig.setEnvironmentConfigPath(configPath + "/environment.xml");

		NameGenerator.setRandomiser(new Random(700));
		// All the big objects
		PluginManager pm = new PluginManager();
		pm.addPlugin(new DebugSwitchPlugin());
		pm.addPlugin(new HuntersAlivePlugin(configPath + "/population.png", 1500,
						1200));
		pm.addPlugin(new ErrorLog());
		pm.addPlugin(new DatabasePlugin(1, "Simulation comment", false));
		pm.addPlugin(new HunterListPlugin());
		pm.addPlugin(new PoliticalCompassPlugin()); // Use this for just a display of the political compass

		TreeMap<String, Participant> parts = new TreeMap<String, Participant>();

		HashMap<String, Food> foods = new HashMap<String, Food>();
		Food chicken = new Food("chicken", 2, 1);
		foods.put(chicken.getId().toString(), chicken);

		EventScriptManager ms = new EventScriptManager();
		EnvironmentDataModel dm = new EnvironmentDataModel("Single Certain Death", foods);
		Environment environment = (Environment)new ise.gameoflife.environment.Environment(true, 0, dm, null);
		presageConfig.setEnvironmentClass(environment.getClass());
		ConfigurationWriter.write(configPath + "/sim.xml", presageConfig, parts, environment, pm, ms);
	}

	private Politics()
	{
	}

}
