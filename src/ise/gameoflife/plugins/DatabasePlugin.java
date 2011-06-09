package ise.gameoflife.plugins;

import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.participants.PublicAgentDataModel;
import ise.gameoflife.participants.PublicGroupDataModel;
import ise.gameoflife.tokens.TurnType;
import org.simpleframework.xml.Element;
import java.io.File;
import presage.Plugin;
import presage.Simulation;
import presage.annotations.PluginConstructor;
import java.util.logging.Logger;

import java.sql.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

/**
 *
 * @author valdas
 * Creates an SQLite database in main simulation folder. Sends data to DB
 * every 100 cycles and commits every 1000 and at the end, thus reducing writes to disk.
 * 
 * Also, allows sending to remote DB if given correct input parameters.
 * 
 */
//TODO Make additional data available to DB.
//TODO Expand DBMS schema to store additional data types.

public class DatabasePlugin implements Plugin {

    private static final long serialVersionUID = 1L;

    private final static String title = "DatabasePlugin";
    private final static String label = "DatabasePlugin";
    
    public final static Logger logger = Logger.getLogger("gameoflife.DatabasePlugin");

    private Simulation sim;
    private PublicEnvironmentConnection ec = null;
    
    private TreeMap<String, PublicAgentDataModel> agentMap = new TreeMap<String, PublicAgentDataModel>();
    private TreeMap<String, PublicGroupDataModel> groupMap = new TreeMap<String, PublicGroupDataModel>();
    private int simid;
    
    //database connection
    private Connection conn;
    private Statement stat;
    private PreparedStatement prep_newAgent;
    private PreparedStatement prep_dieAgent;
    private PreparedStatement prep_newGroup;
    private PreparedStatement prep_dieGroup;
    private PreparedStatement prep_roundAgent;
    private PreparedStatement prep_roundGroup;
    



    @Element
    private Boolean clearDB = false;
    @Element
    private String sim_comment;
    @Element
    private Boolean saveToRemote = false;
     // private JPanel control = new JPanel();


 
    @Deprecated
    public DatabasePlugin()
    {
	
    }

    /**
     * Creates a new instance of the DatabasePlugin
     * 
     * @param sim_comment - Comment for simulation
     * @param saveToRemote - uses remote DB instead of local
     * @param clearDB - resets local db (only works when not saving to remote)
     */
    @PluginConstructor(
    {
	    "sim_comment","saveToRemote","clearDB"
    })
    public DatabasePlugin(String sim_comment, Boolean saveToRemote, Boolean clearDB)
    {
	    super();
	    this.sim_comment = sim_comment;
	    this.clearDB = clearDB;
	    this.saveToRemote = saveToRemote;
   }

       /**
     * Creates a new instance of the DatabasePlugin
     * 
     * @param sim_comment - Comment for simulation
     * @param saveToRemote - uses remote DB instead of local
     * 
     */
    @PluginConstructor(
    {
	    "sim_comment","saveToRemote","clearDB"
    })
    public DatabasePlugin(String sim_comment, Boolean saveToRemote)
    {
	    super();
	    this.sim_comment = sim_comment;
	    this.clearDB = clearDB;
	    
    }
    
    private void createDatabaseSchema() {
	try {
	    stat.addBatch("CREATE TABLE [simulations] (\n" +
		    "[simid] INTEGER NOT NULL PRIMARY KEY ASC, \n" +
		    "[sim_uuid] TEXT NOT NULL, \n" +
		    "[userid] TEXT, \n" +
		    "[comment] TEXT, \n" +
		    "[timestmp] TIMESTAMP DEFAULT (CURRENT_TIMESTAMP), \n" +
		    "[rounds] INTEGER,\n" +
		    "[done] INTEGER DEFAULT (0));");

	    stat.addBatch("CREATE TABLE [agents] (\n" +
		    "[simid] INTEGER NOT NULL REFERENCES [simulations]([simid]) ON DELETE CASCADE ON UPDATE CASCADE, \n" +
		    "[a_uuid] TEXT NOT NULL, \n" +
		    "[name] TEXT NOT NULL,\n" +
		    "[start] INTEGER NOT NULL,\n" +
		    "[end] INTEGER,\n" +
		    "PRIMARY KEY ([simid], [a_uuid]));");

	    stat.addBatch("CREATE TABLE [groups] (\n" +
		    "[simid] INTEGER NOT NULL REFERENCES [simulations]([simid]) ON DELETE CASCADE ON UPDATE CASCADE, \n" +
		    "[g_uuid] TEXT NOT NULL,\n" +
		    "[start] INTEGER NOT NULL,\n" +
		    "[end] INTEGER,\n" +
		    "PRIMARY KEY ([simid], [g_uuid]));");

	    stat.addBatch("CREATE TABLE [g_data] (\n" +
		    "[simid] INTEGER NOT NULL,\n" +
		    "[round] INTEGER  NOT NULL,\n" +
		    "[g_uuid] TEXT NOT NULL,\n" +
		    "[pop] INTEGER,\n" +
		    "[socialPosition] DOUBLE,\n" +
		    "[economicPosition] DOUBLE,\n" +
		    "FOREIGN KEY([simid], [g_uuid]) REFERENCES [groups]([simid],[g_uuid]) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,\n" +
		    "PRIMARY KEY ([simid],[round],[g_uuid]));");

	    stat.addBatch("CREATE TABLE [a_data] (\n" +
		    "[simid] INTEGER NOT NULL,\n" +
		    "[round] INTEGER  NOT NULL,\n" +
		    "[a_uuid] TEXT NOT NULL,\n" +
		    "[g_uuid] TEXT,\n" +
		    "[foodAmount] DOUBLE NOT NULL,\n" +
		    "[lastHunted] TEXT, \n" +
		    "[socialBelief] DOUBLE,\n" +
		    "[economicBelief] DOUBLE,\n" +
		    "[happiness] DOUBLE,\n" +
		    "[loyalty] DOUBLE,\n" +
		    "FOREIGN KEY([simid], [g_uuid]) REFERENCES [groups]([simid],[g_uuid]) ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,\n" +
		    "FOREIGN KEY([simid], [a_uuid]) REFERENCES [agents]([simid],[a_uuid]) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,\n" +
		    "PRIMARY KEY ([simid],[round],[a_uuid]));");

	    stat.addBatch("CREATE TABLE [a_trust] (\n" +
		    "[simid] INTEGER NOT NULL,\n" +
		    "[round] INTEGER  NOT NULL,\n" +
		    "[a_uuid] TEXT NOT NULL,\n" +
		    "[other_uuid] TEXT NOT NULL,\n" +
		    "[trust]  DOUBLE NOT NULL,\n" +
		    "FOREIGN KEY([simid], [a_uuid]) REFERENCES [agents]([simid],[a_uuid]) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,\n" +
		    "FOREIGN KEY([simid], [other_uuid]) REFERENCES [agents]([simid],[a_uuid]) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,\n" +
		    "PRIMARY KEY ([simid],[round],[a_uuid],[other_uuid]));");
	    stat.executeBatch();
	    stat.close();
	} catch (SQLException ex) {
	    logger.log(Level.SEVERE,"Error creating new DB schema", ex);
	}
    }
    
    
    private void updateAgents()
    {
	   Set<String> active_agent_ids = ec.getAgents();
	   
	   Iterator<String> iter = active_agent_ids.iterator();

	    // Add any new agents
	    while(iter.hasNext())
	    {
		String id = iter.next();
		if(!agentMap.containsKey(id))
		{
		    try {
			PublicAgentDataModel newAgent = ec.getAgentById(id);
			agentMap.put(id, newAgent);
			//a_uuid
			prep_newAgent.setString(1,id);
			//agent name
			prep_newAgent.setString(2,newAgent.getName());
			//start
			prep_newAgent.setInt(3,ec.getRoundsPassed());
			prep_newAgent.addBatch();
		    } catch (SQLException ex) {
			logger.log(Level.WARNING, null, ex);
			return;
		    }
		}
	    }

	    // Delete agents which are no longer active
	    List<String> ids_to_remove = new LinkedList<String>();
	    for(Map.Entry<String, PublicAgentDataModel> entry : agentMap.entrySet())
	    {
		String id = entry.getKey();
		if(!active_agent_ids.contains(id))
		{
		    try {
			ids_to_remove.add(id);
			//end round for cycle written
			prep_dieAgent.setInt(1,ec.getRoundsPassed());
			prep_dieAgent.setString(2,id);
			prep_dieAgent.addBatch();
		    } catch (SQLException ex) {
			logger.log(Level.WARNING, null, ex);
		    }
		}
		else {
		    try {
			PublicAgentDataModel agent = entry.getValue();
			prep_roundAgent.setInt(1,ec.getRoundsPassed());
			prep_roundAgent.setString(2,id);
			prep_roundAgent.setString(3,agent.getGroupId());
			prep_roundAgent.setDouble(4,agent.getFoodAmount());
			prep_roundAgent.setString(5,"Food unknown");
			prep_roundAgent.setDouble(6,agent.getSocialBelief());
			prep_roundAgent.setDouble(7,agent.getEconomicBelief());
			prep_roundAgent.setDouble(8,0.0);
			prep_roundAgent.setDouble(9,0.0);
			prep_roundAgent.addBatch();
		    } catch (SQLException ex) {
			logger.log(Level.WARNING, null, ex);
		    }
		}
		
	    }
	    iter = ids_to_remove.iterator();
	    while(iter.hasNext())
	    {
		    agentMap.remove(iter.next());
	    }
    }
    
    private void updateGroups()
    {
	try {
	    Set<String> active_group_ids = ec.availableGroups();
	    
	    Iterator<String> iter = active_group_ids.iterator();

	     // Add any new groups
	     while(iter.hasNext())
	     {
		 String id = iter.next();
		 if(!groupMap.containsKey(id))
		 {
			 PublicGroupDataModel newGroup = ec.getGroupById(id);
			 groupMap.put(id, newGroup);
			 prep_newGroup.setString(1,id);
			 prep_newGroup.setInt(2,ec.getRoundsPassed());
			 prep_newGroup.addBatch();
		 }
	     }
	     // Delete groups which are no longer active
	     List<String> ids_to_remove = new LinkedList<String>();
	     for(Map.Entry<String, PublicGroupDataModel> entry : groupMap.entrySet())
	     {
		 String id = entry.getKey();
		 if(!active_group_ids.contains(id))
		 {
			 ids_to_remove.add(id);
			 //write end time
			 prep_dieGroup.setInt(1,ec.getRoundsPassed());
			 prep_dieGroup.setString(2,id);
			 prep_dieGroup.addBatch();

		 }
		 else{
			 PublicGroupDataModel group = entry.getValue();
			 prep_roundGroup.setInt(1,ec.getRoundsPassed());
			 prep_roundGroup.setString(2,id);
			 prep_roundGroup.setInt(3,group.getMemberList().size());
			 prep_roundGroup.setDouble(4,group.getEstimatedSocialLocation());
			 prep_roundGroup.setDouble(5,group.getCurrentEconomicPoisition());
			 prep_roundGroup.addBatch();
		 }
	     }
	     prep_roundGroup.setInt(1,ec.getRoundsPassed());
	     prep_roundGroup.setString(2,"noGroup");
	     prep_roundGroup.setInt(3,ec.getUngroupedAgents().size());
	     prep_roundGroup.setDouble(4,0.0);
	     prep_roundGroup.setDouble(5,0.0);
	     prep_roundGroup.addBatch();
	     iter = ids_to_remove.iterator();
	     while(iter.hasNext())
	     {
		     groupMap.remove(iter.next());
	     }
	} catch (SQLException ex) {
	    logger.log(Level.SEVERE, null, ex);
	}
    }
	   
    @Override
    public void execute()
    {
	try {
	    //data only updated at the beginning of turn
	    if (ec.getCurrentTurnType() != TurnType.firstTurn) return;
		updateAgents();
		updateGroups();
	    if (ec.getRoundsPassed()%10 == 0) {
		prep_newAgent.executeBatch();
		prep_newGroup.executeBatch();
		prep_dieAgent.executeBatch();
		prep_dieGroup.executeBatch();
		prep_roundGroup.executeBatch();
		prep_roundAgent.executeBatch();
		if (ec.getRoundsPassed()%50 == 0) {
		    conn.commit();
		 }
	    }

	} catch (SQLException ex) {
	    logger.log(Level.SEVERE, null, ex);
	}

    }
    
//Creates connections and local DB if not exists.Otherwise, adds to existing
    //DB.
    @Override
    public void initialise(Simulation sim)
    {
	this.sim = sim;
	ec = PublicEnvironmentConnection.getInstance();
	
	if (!saveToRemote) {
	    try {    
		Class.forName("org.sqlite.JDBC");
	    }
	    catch (Exception e)
		{
			System.err.println("SQLite database library not available:" + e);
		}
	}
	try {

	    if (!saveToRemote) {
		//path to /Simulations folder
		String configPath = new File(System.getProperty("user.dir"), "simulations").getAbsolutePath();
		//create connection to local db
		conn = DriverManager.getConnection("jdbc:sqlite:"+ configPath + "/Simulations.db");
		if(clearDB) {
		    DatabaseMetaData md = conn.getMetaData();
		    ResultSet rs = md.getTables(null, null, "%", null);
		    stat = conn.createStatement();
		    //if old table exists, drop database
		    while (rs.next()) {
		      stat.addBatch("DROP TABLE "+ rs.getString(3)+";");
		    }
		    rs.close();
		    stat.executeBatch();
		    createDatabaseSchema();
		}
	    }
	    else {
		conn = DriverManager.getConnection("jdbc:mysql://69.175.26.66:3306/stratra1_isegol",
			    "stratra1_isegol","ise4r3g00d");
	    }
	    
	    stat = conn.createStatement();
	    if (saveToRemote) {
		stat.executeUpdate("INSERT INTO simulations " +
		"(sim_uuid,userid,comment)" +
		"VALUES ('"+ec.getId()+"',9,'"+sim_comment+"');"
			    );
	    }  else {
		stat.executeUpdate("INSERT INTO simulations " +
		"(sim_uuid,userid,comment)" +
		"VALUES ('"+ec.getId()+"','"+System.getProperty("user.name")+"','"+sim_comment+"');"
			    );
	    }
	    ResultSet rs = stat.getGeneratedKeys();         
	    while (rs.next()) {
		simid  = rs.getInt(1);    
	    }
	    rs.close();                           // Close ResultSet
	    logger.log(Level.INFO, "SimulationId in DB = {0}", simid);
	    stat.close();                         // Close Statement

	    //if (!saveToRemote) 
		conn.setAutoCommit(false);
	    prep_newAgent = conn.prepareStatement(
		    "INSERT into agents (simid,a_uuid,name,start)"
		    + " VALUES ("+simid+",?,?,?);");
	    prep_newGroup = conn.prepareStatement(
		    "INSERT into groups (simid,g_uuid,start)"
		    + " VALUES ("+simid+",?,?);");
	    prep_dieAgent = conn.prepareStatement("UPDATE agents\n"
					+ "SET end=?\n"
					+ "WHERE simid="+simid+"\n"
					+ "AND a_uuid=?;");
	    prep_dieGroup = conn.prepareStatement("UPDATE groups\n"
					+ "SET end=?\n"
					+ "WHERE simid="+simid+"\n"
					+ "AND g_uuid=?;");
	    prep_roundGroup = conn.prepareStatement(
		    "INSERT into g_data (simid,round,g_uuid,pop,socialPosition,economicPosition)"
		    + " VALUES ("+simid+",?,?,?,?,?);");
	    prep_roundAgent = conn.prepareStatement(
		     "INSERT into a_data (simid,round,a_uuid,g_uuid,foodAmount,lastHunted,socialBelief,economicBelief,happiness,loyalty)"
		    + " VALUES ("+simid+",?,?,?,?,?,?,?,?,?);");
	    prep_newGroup.setString(1,"noGroup");
	    prep_newGroup.setInt(2,0);
	    prep_newGroup.addBatch();
   
	}
	catch (SQLException x) {
	    logger.log(Level.WARNING, "Initialise DB Error", x);
	    return;
	}
    }
 
    @Override
    public String getLabel()
    {
	    return label;
    }

    @Override
    public String getShortLabel()
    {
	    return label;
    }
    
    

    @Deprecated
    @Override
    public void onDelete()
    {
	    // Nothing to see here. Move along, citizen!
    }

    @Override
    public void onSimulationComplete()
    {
	//commits remaining data and updates done flag in DB
	try {
	    //sends left over data to DB
	    prep_newAgent.executeBatch();
	    prep_newGroup.executeBatch();
	    prep_dieAgent.executeBatch();
	    prep_dieGroup.executeBatch();
	    prep_roundGroup.executeBatch();
	    prep_roundAgent.executeBatch();
	    stat = conn.createStatement();
	    //Update agent and group end time to end of simulation, if still lives
	    stat.executeUpdate("UPDATE agents\n"
					+ "SET end="+ec.getRoundsPassed()+"\n"
					+ "WHERE simid="+simid+"\n"
					+ "AND end IS NULL;");
	    stat.executeUpdate("UPDATE groups\n"
					+ "SET end="+ec.getRoundsPassed()+"\n"
					+ "WHERE simid="+simid+"\n"
					+ "AND end IS NULL;");
	    stat.executeUpdate("UPDATE simulations\n"
					+ "SET done=1,rounds="+ec.getRoundsPassed()+"\n"
					+ "WHERE simid="+simid+";");
	    conn.commit();
	    stat.close();
	    conn.close();
	}
	catch (SQLException ex) {
	    logger.log(Level.WARNING,"Database finalize error", ex);
	    try {
		conn.close();
	    } catch (SQLException ex1) {
		logger.log(Level.SEVERE, null, ex1);
	    }

	}
    }
	
}


