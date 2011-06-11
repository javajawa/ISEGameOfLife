package ise.gameoflife.plugins.database;

import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.participants.PublicAgentDataModel;
import ise.gameoflife.participants.PublicGroupDataModel;
import ise.gameoflife.tokens.TurnType;
import java.io.File;
import java.sql.SQLException;
import java.util.Map;
import org.simpleframework.xml.Element;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import presage.Plugin;
import presage.Simulation;
import presage.annotations.PluginConstructor;

/**
 * Updated version of DatabasePlugin that is more more more more... just better.
 * @author Valdas
 */
public class NewDatabasePlugin implements Plugin
{
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger("gameoflife.DatabasePlugin");
	private final static String name = "Database v2";
		
	@Element
	private final Boolean remote;
	@Element
	private final String comment;
	
	/**
	 * Creates a new instance of the DatabasePlugin,
	 * stores data in local db.
	 * 
	 */
	NewDatabasePlugin()
	{
	  //default parameters: local database
	  this("No comment",false);
	}
	
	 /**
	 * Creates a new instance of the DatabasePlugin
	 * 
	 * @param comment Comment for simulation
	 * @param remote Use remote db instead of local
	 * 
	 */
	@PluginConstructor(
	{
		"comment","remote"
	})
	public NewDatabasePlugin(String comment,Boolean remote)
	{
	    this.remote = remote;
	    this.comment = comment;
	}
	

	private ConnectionWrapper wrap;
	private PublicEnvironmentConnection envConn;
	private int round;
	
	private final TreeMap<String, PublicGroupDataModel> trackedGroups 
					= new TreeMap<String, PublicGroupDataModel>();
	private final TreeMap<String, PublicAgentDataModel> trackedAgents
					= new TreeMap<String, PublicAgentDataModel>();

	@Override
	public void execute()
	{
	    //data only updated at the beginning of round
	    if (envConn.getCurrentTurnType() != TurnType.firstTurn) return;
	    round = envConn.getRoundsPassed();
	    pruneOldGroups();
	    findNewGroups();
	    pruneOldAgents();
	    findNewAgents();
	    getGroupRoundData();
	    getAgentRoundData();
	    //writes to db every 25 rounds (or 150 cycles)
	    if(round%25==0) {
		if (!remote) logger.log(Level.INFO,"Writing data to local database");
		else logger.log(Level.INFO,"Writing data to remote database (could be slow)");
		wrap.flush();
	    }
	    
	}

	@Override
	public void initialise(Simulation sim)
	{
	    	envConn = PublicEnvironmentConnection.getInstance();
		if (envConn == null) throw new IllegalStateException(
		    "Connection created before EnviromentConnection was accessible");
		try {
		    String url;
		    if(remote) {
			//url to remote db
			url = "jdbc:mysql://69.175.26.66:3306/stratra1_isegol?user=stratra1_isegol&password=ise4r3g00d";
			logger.log(Level.INFO,"Connecting to remote database:");
		    }
		    else {
			//path to /Simulations folder
			String simDir = new File(System.getProperty("user.dir"), "simulations").getAbsolutePath();
			//url to local db
			url = "jdbc:sqlite:"+ simDir + "/Simulations.db";
			logger.log(Level.INFO, "Connecting to local database at: {0}/Simulations.db", simDir);
		    }
		    wrap = new ConnectionWrapper(url, comment, envConn.getId(), remote);

		}
		catch (SQLException ex)
		{
			logger.log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			logger.log(Level.SEVERE,"SQLite JDBC class not found", ex);
		}

	}

	@Override
	public void onDelete()
	{
		wrap.flush();
		wrap.end(round);
	}

	@Override
	public void onSimulationComplete()
	{
		wrap.flush();
		wrap.end(round);
	}

	@Override
	public String getLabel()
	{
		return name;
	}

	@Override
	public String getShortLabel()
	{
		return name;
	}
	
	private void findNewAgents()
	{
		// get all active agentsin simulation
		TreeSet<String> newAgents = new TreeSet<String>(envConn.getAgents());
		//remove already tracked groups
		newAgents.removeAll(trackedAgents.keySet());

		for (String a : newAgents)
		{
			PublicAgentDataModel agent = envConn.getAgentById(a);
			//queue agent addition sql statement
			wrap.agentAdd(a, round, agent.getName()); 
			//add the agent to tracked groups
			trackedAgents.put(a, agent);
		}
	}

	private void findNewGroups()
	{
		// get all active groups in simulation
		TreeSet<String> newGroups = new TreeSet<String>(envConn.availableGroups());
		//remove already tracked groups
		newGroups.removeAll(trackedGroups.keySet());

		for (String g : newGroups)
		{
			//queue group addition sql statement
			wrap.groupAdd(g, round);
			//add the group to tracked groups
			trackedGroups.put(g, envConn.getGroupById(g));
		}
	}

	
	private void pruneOldGroups()
	{
		//Stop tracking old groups
		TreeSet<String> oldGroups = new TreeSet<String>(trackedGroups.keySet());
		oldGroups.removeAll(envConn.availableGroups());

		for (String g : oldGroups)
		{
			//queue group death sql statement
			wrap.groupDie(g, round);
			trackedGroups.remove(g);
		}
	}
	
	private void pruneOldAgents()
	{
		//Stop tracking dead agents
		TreeSet<String> deadAgents = new TreeSet<String>(trackedAgents.keySet());
		deadAgents.removeAll(envConn.getAgents());

		for (String a : deadAgents)
		{
			//queue agent death sql statement
			wrap.agentDie(a, round);
			trackedAgents.remove(a);
		}
	}

	private void getGroupRoundData() {
	    for(Map.Entry<String, PublicGroupDataModel> entry : trackedGroups.entrySet())
		{
		    wrap.groupRound(entry.getKey(), round, entry.getValue());
		}
	}

	private void getAgentRoundData() {
	    
	    for(Map.Entry<String, PublicAgentDataModel> entry : trackedAgents.entrySet())
		{
		    wrap.agentRound(entry.getKey(), round, entry.getValue());
		}
	}
}
