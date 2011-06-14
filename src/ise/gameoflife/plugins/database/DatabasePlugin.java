package ise.gameoflife.plugins.database;

import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.participants.PublicAgentDataModel;
import ise.gameoflife.participants.PublicGroupDataModel;
import ise.gameoflife.tokens.TurnType;
import org.simpleframework.xml.Element;
import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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
public class DatabasePlugin implements Plugin
{
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger("gameoflife.DatabasePlugin");
	private final static String name = "Database Plugin v2.2";
		
	@Element
	private final Boolean remote;
	@Element
	private final String comment;
	
	/**
	 * Creates a new instance of the DatabasePlugin,
	 * stores data in local db.
	 * 
	 */
	DatabasePlugin()
	{
	  //default parameters: local database
	  this("No comment",false);
	}
	
	 /**
	 * Creates a new instance of the DatabasePlugin
	 * with specified parameters.
	 * @param comment Comment for simulation
	 * @param remote Use remote db instead of local
	 * 
	 */
	@PluginConstructor(
	{
		"comment","remote"
	})
	public DatabasePlugin(String comment,Boolean remote)
	{
	    this.remote = remote;
	    this.comment = comment;

	}
	

	private ConnectionWrapper wrap;
	private PublicEnvironmentConnection ec;
	//internal storage of roundsPassed()
	private int round;
	//used to generate simulation unique agentid and groupid
	private int idGenerator = 0;
	
	private final TreeMap<String, PublicGroupDataModel> trackedGroups 
					= new TreeMap<String, PublicGroupDataModel>();
	private final TreeMap<String, PublicAgentDataModel> trackedAgents
					= new TreeMap<String, PublicAgentDataModel>();
	
	//contains agentid and groupid that are used to represent participants in the database
	//HashMap size should same order of magnitude as agents+groups set size for speed
	private final Map<String,Integer> idMap = new HashMap<String,Integer>(80);


	@Override
	public void execute()
	{
	    //data only updated at the beginning of round
	    if (ec.getCurrentTurnType() != TurnType.firstTurn) return;
	    round = ec.getRoundsPassed();
	    pruneOldGroups();
	    findNewGroups();
	    pruneOldAgents();
	    findNewAgents();
	    getGroupRoundData();
	    getAgentRoundData();
	    
	    //writes to db every 25 rounds (or 150 cycles)
	    if(round%50==0) 
	    {
		if (!remote) logger.log(Level.INFO,"Writing data to local database");
		else logger.log(Level.INFO,"Writing data to remote database (could take a while)");
		wrap.flush(round);
		logger.log(Level.INFO,"Database write complete");
	    }
	    
	}

	@Override
	public void initialise(Simulation sim)
	{
	    ec = PublicEnvironmentConnection.getInstance();
	    if (ec == null) throw new IllegalStateException(
		"Connection created before EnviromentConnection was accessible");

	    try {
		//selects database connection
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
		//Establish connection to database
		wrap = new ConnectionWrapper(url, comment, ec.getId(), remote);

	    }   catch (SQLException ex) {
		    logger.log(Level.SEVERE,"Initializing database error:", ex);
	    }   catch (ClassNotFoundException ex) {
		    logger.log(Level.SEVERE,"SQLite JDBC class not found", ex);
	    }

	    //required to prevent null exception for agents without group
	    createFreeAgentGroup();

	}

	@Override
	public void onDelete()
	{
		wrap.flush(round);
		wrap.end(round);
	}

	@Override
	public void onSimulationComplete()
	{
		if (!remote) logger.log(Level.INFO,"Writing data to local database");
		else logger.log(Level.INFO,"Writing data to remote database (could take a while)");
		wrap.flush(round);
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
		TreeSet<String> newAgents = new TreeSet<String>(ec.getAgents());
		//remove already tracked groups
		newAgents.removeAll(trackedAgents.keySet());

		for (String a : newAgents)
		{
			PublicAgentDataModel agent = ec.getAgentById(a);
			int agentid = ++idGenerator;
			wrap.agentAdd(a, agentid, round, agent.getName());
			idMap.put(a, agentid);
			//add the agent to tracked groups
			trackedAgents.put(a, agent);
		}
	}

	private void findNewGroups()
	{
		// get all active groups in simulation
		TreeSet<String> newGroups = new TreeSet<String>(ec.availableGroups());
		//remove already tracked groups
		newGroups.removeAll(trackedGroups.keySet());
		for (String g : newGroups)
		{
			int groupid = ++idGenerator;
			wrap.groupAdd(g, groupid, round);
			idMap.put(g,groupid);
			//add the group to tracked groups
			trackedGroups.put(g, ec.getGroupById(g));
		}
	}

	
	private void pruneOldGroups()
	{
		//Stop tracking old groups
		TreeSet<String> oldGroups = new TreeSet<String>(trackedGroups.keySet());
		oldGroups.removeAll(ec.availableGroups());
		for (String g : oldGroups)
		{
			//queue group death sql statement
			wrap.groupDie(idMap.remove(g), round);
			trackedGroups.remove(g);
		}
	}
	
	private void pruneOldAgents()
	{
		//Stop tracking dead agents
		TreeSet<String> deadAgents = new TreeSet<String>(trackedAgents.keySet());
		deadAgents.removeAll(ec.getAgents());

		for (String a : deadAgents)
		{
			//queue agent death sql statement
			wrap.agentDie(idMap.remove(a), round);
			trackedAgents.remove(a);
		}
	}

	private void getGroupRoundData() 
	{
	    for(Map.Entry<String, PublicGroupDataModel> entry : trackedGroups.entrySet())
		{
		    wrap.groupRound(idMap.get(entry.getKey()), round, entry.getValue());
		}
	}

	private void getAgentRoundData() 
	{	    
	    for(Map.Entry<String, PublicAgentDataModel> entry : trackedAgents.entrySet())
		{
		    //gets the agents groupid and maps it to database id for group
		    //if agent group is null, the map returns 0 groupid
		    PublicAgentDataModel agent = entry.getValue();
		    int groupid = idMap.get(agent.getGroupId());
		    wrap.agentRound(idMap.get(entry.getKey()), groupid, round, agent);
		}
	}
	/*
	 * creates a group for free agents.
	 */
	private void createFreeAgentGroup() 
	{
		int groupid = 0;
//		wrap.groupAdd("FreeAgentsGroup", groupid, round);
		//required to allow idMap to work for agents with no group
		idMap.put(null,groupid);
	}

}
