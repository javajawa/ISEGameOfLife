package ise.mace.plugins.database;

import ise.mace.environment.PublicEnvironmentConnection;
import ise.mace.groups.LoansGroup;
import ise.mace.participants.PublicAgentDataModel;
import ise.mace.participants.PublicGroupDataModel;
import ise.mace.tokens.TurnType;
import org.simpleframework.xml.Element;
import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import presage.Plugin;
import presage.Simulation;
import presage.annotations.PluginConstructor;

/**
 * Updated version of DatabasePlugin that is more more more more... just better.
 */
public class DatabasePlugin implements Plugin
{
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger("mace.DatabasePlugin");
	private final static String name = "Database Plugin v2.9";
	@Element
	private final Boolean remote;
	@Element
	private final String comment;
	@Element
	private final Boolean docRemote;
	@Element
	private final Boolean loans;

	/**
	 * Creates a new instance of the DatabasePlugin,
	 * stores data in local db.
	 *
	 */
	DatabasePlugin()
	{
		//default parameters: local database
		this("No comment", false);
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
		"comment", "remote"
	})
	public DatabasePlugin(String comment, Boolean remote)
	{
		this(comment, remote, false, false);

	}

	/**
	 * Creates a new instance of the DatabasePlugin
	 * with specified parameters.
	 * @param comment Comment for simulation
	 * @param remote Use remote db instead of local
	 * @param docRemote Uses doc remote database
	 * @param loans Should be used for simulating loans
	 */
	@PluginConstructor(
	{
		"comment", "remote", "docRemote"
	})
	public DatabasePlugin(String comment, Boolean remote, Boolean docRemote)
	{
		this(comment, remote, docRemote, false);
	}

	/**
	 * Creates a new instance of the DatabasePlugin
	 * with specified parameters.
	 * @param comment Comment for simulation
	 * @param remote Use remote db instead of local
	 * @param docRemote Uses doc remote database
	 * @param loans Should be used for simulating loans
	 */
	@PluginConstructor(
	{
		"comment", "remote", "docRemote", "loans"
	})
	public DatabasePlugin(String comment, Boolean remote, Boolean docRemote,
					Boolean loans)
	{
		this.loans = loans;
		this.comment = comment;
		this.remote = remote;
		this.docRemote = docRemote;
	}
	private ConnectionWrapper wrap;
	private PublicEnvironmentConnection ec;
	private Simulation sim;
	//internal storage of roundsPassed()
	private int round;
	//used to generate simulation unique agentid and groupid
	private int agentIdGenerator = 0;
	private int groupIdGenerator = 0;
	private final TreeMap<String, PublicGroupDataModel> trackedGroups = new TreeMap<String, PublicGroupDataModel>();
	private final TreeMap<String, PublicAgentDataModel> trackedAgents = new TreeMap<String, PublicAgentDataModel>();
	//contains agentid and groupid that are used to represent participants in the database
	//HashMap size should same order of magnitude as agents+groups set size for speed
	private final Map<String, Integer> agentIdMap = new HashMap<String, Integer>(
					100);
	private final Map<String, Integer> groupIdMap = new HashMap<String, Integer>(
					100);

	@Override
	public void execute()
	{
		//data only updated at the beginning of round
		if (ec.getCurrentTurnType() != TurnType.firstTurn) return;
		round = ec.getRoundsPassed();
		findNewGroups();
		findNewAgents();
		pruneOldAgents();
		//update FreeAgentsGroup with data
		wrap.getFreeAgentGroupData(round, ec.getUngroupedAgents().size());
		//disables agent round data collection for remote db, to speed it up
		//also updates trust table
		if (!remote)
			getAgentRoundData();
		pruneOldGroups();
		getGroupRoundData();


		//writes to db every 50 rounds (or 30 cycles) for local db
		//remote only writes at the end
		//no writes till the end
	   /* if(round%50==0 && !remote)
		{
		if (!remote) logger.log(Level.INFO,"Writing data to local database");
		else logger.log(Level.INFO,"Writing data to remote database (could take a while)");
		wrap.flush(round);
		logger.log(Level.INFO,"Database write complete");
		}
		 */
	}

	@Override
	public void initialise(Simulation sim)
	{
		ec = PublicEnvironmentConnection.getInstance();
		this.sim = sim;
		if (ec == null) throw new IllegalStateException(
							"Connection created before EnviromentConnection was accessible");

		try
		{
			//selects database connection
			String url;
			Properties connProperties = new java.util.Properties();
			if (remote)
			{
				String dbUsername;
				String dbPassword;
				if (docRemote)
				{
					//url to remote db
					url = "jdbc:mysql://icsflibsrv.su.ic.ac.uk:3306/gol";
					dbUsername = "gol";
					dbPassword = "gol";
				}
				else
				{
					url = "jdbc:mysql://69.175.26.66:3306/stratra1_isegol";
					dbUsername = "stratra1_isegol";
					dbPassword = "ise4r3g00d";
				}
				connProperties.put("user", dbUsername);
				connProperties.put("password", dbPassword);
				connProperties.put("autoReconnect", "true");
				//connProperties.put("useCompression", "true");
				//15 seconds delay before each reconnect
				connProperties.put("initialTimeout", "15");
				connProperties.put("maxReconnects", "5");
				logger.log(Level.INFO, "Connecting to remote database:{0}", url);
			}
			else
			{
				//path to /Simulations folder
				String simDir = new File(System.getProperty("user.dir"), "simulations").getAbsolutePath();
				//url to local db
				url = "jdbc:sqlite:" + simDir + "/Simulations.db";
				logger.log(Level.INFO,
								"Connecting to local database at: {0}/Simulations.db", simDir);
			}
			//Establish connection to database
			wrap = new ConnectionWrapper(url, connProperties, comment, ec.getId(),
							remote, loans);

		}
		catch (SQLException ex)
		{
			logger.log(Level.SEVERE, "Initializing database error:", ex);
		}
		catch (ClassNotFoundException ex)
		{
			logger.log(Level.SEVERE, "SQLite JDBC class not found", ex);
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
		if (!remote) logger.log(Level.INFO, "Writing data to local database");
		else
			logger.log(Level.INFO,
							"Writing data to remote database."
							+ "\n ---= DO NOT CLOSE WINDOW (DATABASE UPLOAD IN PROGRESS) =---"
							+ "\n\t (wait till upload complete message if you wish to upload whole of your simulation");
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
		//TreeSet<String> newAgents = new TreeSet<String>(ec.getAgents());
		TreeSet<String> newAgents = new TreeSet<String>(sim.getactiveParticipantIdSet(
						"hunter"));
		//remove already tracked groups
		newAgents.removeAll(trackedAgents.keySet());

		for (String a : newAgents)
		{
			PublicAgentDataModel agent = ec.getAgentById(a);
			int agentid = ++agentIdGenerator;
			wrap.agentAdd(a, agentid, round, agent.getName());
			agentIdMap.put(a, agentid);
			//add the agent to tracked groups
			trackedAgents.put(a, agent);
		}
	}

	private void findNewGroups()
	{
		// get all active groups in simulation
		TreeSet<String> newGroups = new TreeSet<String>(ec.getGroups());
		//TreeSet<String> newGroups = new TreeSet<String>(ec.isAgentId(name));
		//remove already tracked groups
		newGroups.removeAll(trackedGroups.keySet());
                //get rid of special group
                String SpecialID = "";
                for(String group : newGroups)
                   if(ec.getGroupById(group).getName().equals("Group #2"))
                       SpecialID = group;
                newGroups.remove(SpecialID);                
		
                for (String g : newGroups)
		{
			int groupid = ++groupIdGenerator;
			if (!loans) wrap.groupAdd(g, groupid, round);
			else wrap.groupAdd(g, groupid, round,
								LoansGroup.getGreediness(ec.getGroupById(g)));
			groupIdMap.put(g, groupid);
			//add the group to tracked groups
			trackedGroups.put(g, ec.getGroupById(g));
			//logger.log(Level.WARNING,"Name of the normal group:{0}, db id:{1}",new Object[] {ec.getGroupById(g).getName(),groupid});

		}
	}

	private void pruneOldGroups()
	{
		//Stop tracking old groups
		TreeSet<String> oldGroups = new TreeSet<String>(trackedGroups.keySet());
		oldGroups.removeAll(ec.getGroups());
		for (String g : oldGroups)
		{
			//queue group death sql statement
			wrap.groupDie(groupIdMap.remove(g), round);
			trackedGroups.remove(g);
		}
	}

	private void pruneOldAgents()
	{
		//Stop tracking dead agents
		TreeSet<String> deadAgents = new TreeSet<String>(trackedAgents.keySet());
		deadAgents.removeAll(sim.getactiveParticipantIdSet("hunter"));

		for (String a : deadAgents)
		{
			//queue agent death sql statement
			wrap.agentDie(agentIdMap.remove(a), round);
			trackedAgents.remove(a);
		}
	}

	private void getGroupRoundData()
	{

		for (Map.Entry<String, PublicGroupDataModel> entry : trackedGroups.entrySet())
		{
			PublicGroupDataModel group = entry.getValue();
			try
			{

				if (loans)
				{
					double averageHappiness = 0;
					for (String member : group.getMemberList())
					{
						averageHappiness += ec.getAgentById(member).getCurrentHappiness();
					}
					averageHappiness = averageHappiness / group.getMemberList().size();
					wrap.loanGroupRound(groupIdMap.get(entry.getKey()), round, group,
									averageHappiness);
				}
				else wrap.groupRound(groupIdMap.get(entry.getKey()), round, group);
			}
			catch (NullPointerException ex)
			{
				logger.log(Level.WARNING,
								"Null Exception: For group {0} for round {1}.Ex:{2}"
								+ " ", new Object[]
								{
									group.getName(), round, ex
								});
			}
		}


	}

	private void getAgentRoundData()
	{
		for (Map.Entry<String, PublicAgentDataModel> entry : trackedAgents.entrySet())
		{
			PublicAgentDataModel agent = entry.getValue();
			try
			{
				//gets the agents groupid and maps it to database id for group
				//if agent group is null, the map returns 0 groupid

				int groupid = groupIdMap.get(agent.getGroupId());
				int agentid = agentIdMap.get(entry.getKey());
				wrap.agentRound(agentid, groupid, round, agent);
				for (Map.Entry<String, PublicAgentDataModel> entry2 : trackedAgents.entrySet())
				{
					Double trust = agent.getTrust(entry2.getKey());
					if (trust != null)
					{
						int agentid_other = agentIdMap.get(entry2.getKey());
						wrap.agentTrust(agentid, agentid_other, trust, round);
					}
				}
			}
			catch (NullPointerException ex)
			{
				logger.log(Level.WARNING, "Null Exception: For agent {0} for round {1}"
								+ " ", new Object[]
								{
									agent.getName(), round
								});
			}
		}
	}
	/*
	 * creates a group for free agents.
	 */

	private void createFreeAgentGroup()
	{
		int groupid = 0;
		wrap.groupAdd("FreeAgentsGroup", groupid, round);
		//required to allow idMap to work for agents with no group
		groupIdMap.put(null, groupid);
	}
}
