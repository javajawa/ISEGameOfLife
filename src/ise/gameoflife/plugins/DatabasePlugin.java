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
    
    public final static Logger logger = Logger.getLogger(DatabasePlugin.class.getName());

    private Simulation sim;
    private PublicEnvironmentConnection ec = null;
    
    private TreeMap<String, PublicAgentDataModel> agentMap = new TreeMap<String, PublicAgentDataModel>();
    private TreeMap<String, PublicGroupDataModel> groupMap = new TreeMap<String, PublicGroupDataModel>();
    
    //database connection
    private Connection conn;
    private Statement stat;
    private PreparedStatement prep;
    private PreparedStatement prep_newAgent;
    private PreparedStatement prep_dieAgent;
    



    @Element
    private int simid;
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
     * @param simid - Simulation ID inside lDB
     * @param sim_comment - Comment inside DB
     * @param saveToRemote - uses remote DB instead of local
     */
    @PluginConstructor(
    {
	    "simid","sim_comment","saveToRemote"
    })
    public DatabasePlugin(int simid, String sim_comment, Boolean saveToRemote)
    {
	    super();
	    this.simid = simid;
	    this.sim_comment = sim_comment;
	    this.saveToRemote = saveToRemote;
	    
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
		DatabaseMetaData md = conn.getMetaData();
		ResultSet rs = md.getTables(null, null, "data", null);
		stat = conn.createStatement();
		//if old table exists, drop database
		if (rs.next()) {
		  rs.close();
		  stat.executeUpdate("DROP TABLE data; DROP TABLE simulations;");
		  stat.executeUpdate("DROP TABLE simulations;");
		}
		createDatabaseSchema();
	    }
	    else {
		conn = DriverManager.getConnection("jdbc:mysql://69.175.26.66:3306/stratra1_isegol",
			    "stratra1_isegol","ise4r3g00d");
	    }
	    
	    stat = conn.createStatement();
	    stat.executeUpdate("INSERT INTO [simulations] " +
		"(sim_uuid,userid,comment)" +
		"VALUES ('"+ec.getId()+"','"+System.getProperty("user.name")+"','"+sim_comment+"');"
			    );
	    ResultSet rs = stat.getGeneratedKeys();         
	    while (rs.next()) {
		simid  = rs.getInt(1);    
	    }
	    rs.close();                           // Close ResultSet
	    logger.log(Level.INFO, "This simulation is saved in DB with: simid = {0}", simid);
	    stat.close();                         // Close Statement

	    conn.setAutoCommit(false);
	    prep_newAgent = conn.prepareStatement(
		    "INSERT into [agents] (simid,a_uuid,name,start,socialBelief,economicBelief)"
		    + " VALUES ("+simid+",?,?,?,?,?);");
	    prep_dieAgent = conn.prepareStatement("UPDATE [agents]\n"
					+ "SET end=?\n"
					+ "WHERE simid="+simid+"\n"
					+ "AND a_uuid=?;");
	    
	  
	}
	catch (SQLException x) {
	    logger.log(Level.WARNING, "Initialise DB Error", x);
	    return;
	}
    }
    
    private void createDatabaseSchema() {
	try {
	    stat.addBatch(
		"CREATE TABLE IF NOT EXISTS [simulations] (\n" +
			"\t[simid] INTEGER NOT NULL PRIMARY KEY ASC, \n" +
			"\t[sim_uuid] TEXT NOT NULL, \n" +
			"\t[userid] TEXT, \n" +
			"\t[comment] TEXT, \n" +
			"\t[timestmp] TIMESTAMP DEFAULT (CURRENT_TIMESTAMP), \n" +
			"\t[rounds] INTEGER,\n" +
			"\t[done] INTEGER DEFAULT (0));\n");
	    stat.addBatch(
		"CREATE TABLE IF NOT EXISTS [agents]  (\n" +
			"\t[simid] INTEGER NOT NULL REFERENCES [simulations]([simid]) ON DELETE CASCADE ON UPDATE CASCADE, \n" +
			"\t[a_uuid] TEXT NOT NULL, \n" +
			"\t[name] TEXT,\n" +
			"\t[start] INTEGER NOT NULL,\n" +
			"\t[end] INTEGER,\n" +
			"\t[socialBelief] DOUBLE,\n" +
			"\t[economicBelief] DOUBLE,\n" +
			"\tPRIMARY KEY ([simid], [a_uuid]));\n");
	    stat.addBatch(
		"CREATE TABLE IF NOT EXISTS [groups]  (\n" +
			"\t[simid] INTEGER NOT NULL REFERENCES [simulations]([simid]) ON DELETE CASCADE ON UPDATE CASCADE, \n" +
			"\t[g_uuid] TEXT NOT NULL,\n" +
			"\tPRIMARY KEY ([simid], [g_uuid]));\n");
	    stat.addBatch(
		"CREATE TABLE IF NOT EXISTS [g_data]  (\n" +
			"\t[simid] INTEGER NOT NULL,\n" +
			"\t[turn] INTEGER  NOT NULL,\n" +
			"\t[g_uuid] TEXT NOT NULL,\n" +
			"\t[pop] INTEGER,\n" +
			"\t[x_value] DOUBLE,\n" +
			"\t[y_value] DOUBLE,\n" +
			"\tFOREIGN KEY([simid], [g_uuid]) REFERENCES [groups]([simid],[g_uuid]) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,\n" +
			"\tPRIMARY KEY ([simid],[turn],[g_uuid]));\n");
	    stat.addBatch(
		"CREATE TABLE IF NOT EXISTS [a_data]  (\n" +
			"\t[simid] INTEGER NOT NULL,\n" +
			"\t[turn] INTEGER  NOT NULL,\n" +
			"\t[a_uuid] TEXT NOT NULL,\n" +
			"\t[pop]  INTEGER,\n" +
			"\t[x_value] DOUBLE,\n" +
			"\t[y_value] DOUBLE,\n" +
			"\tFOREIGN KEY([simid], [a_uuid]) REFERENCES [agents]([simid],[a_uuid]) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,\n" +
			"\tPRIMARY KEY ([simid],[turn],[a_uuid]));\n");
	    stat.addBatch(
		"CREATE TABLE IF NOT EXISTS [a_trust]  (\n" +
			"\t[simid] INTEGER NOT NULL,\n" +
			"\t[turn] INTEGER  NOT NULL,\n" +
			"\t[a_uuid] TEXT NOT NULL,\n" +
			"\t[other_uuid] TEXT NOT NULL,\n" +
			"\t[trust]  INTEGER,\n" +
			"\tFOREIGN KEY([simid], [a_uuid]) REFERENCES [agents]([simid],[a_uuid]) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,\n" +
			"\tFOREIGN KEY([simid], [other_uuid]) REFERENCES [agents]([simid],[a_uuid]) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,\n" +
			"\tPRIMARY KEY ([simid],[turn],[a_uuid],[other_uuid]));\n");
	    stat.executeBatch();
	    stat.close();
	} catch (SQLException ex) {
	    Logger.getLogger(DatabasePlugin.class.getName()).log(Level.SEVERE,"Error creating new DB schema", ex);
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
			PublicAgentDataModel newAgent = agentMap.put(id, (PublicAgentDataModel) ec.getAgentById(id));
			prep_newAgent.setString(1,id);
			//prep_newAgent.setString(2,newAgent.getName());
			prep_newAgent.setInt(3,ec.getRoundsPassed());
			//prep_newAgent.setDouble(4,newAgent.getSocialBelief());
			//prep_newAgent.setDouble(5,newAgent.getEconomicBelief());
			prep_newAgent.addBatch();
		    } catch (SQLException ex) {
			logger.log(Level.SEVERE, null, ex);
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
			prep_dieAgent.setInt(1,ec.getRoundsPassed());
			prep_dieAgent.setString(2,id);
			prep_dieAgent.addBatch();
		    } catch (SQLException ex) {
			logger.log(Level.SEVERE, null, ex);
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
	   Set<String> active_group_ids = ec.availableGroups();
	   
	   Iterator<String> iter = active_group_ids.iterator();

	    // Add any new agents
	    while(iter.hasNext())
	    {
		String id = iter.next();
		if(!groupMap.containsKey(id))
		{
		    try {
			PublicGroupDataModel newGroup = groupMap.put(id, (PublicGroupDataModel) ec.getGroupById(id));
			//prep_newGroup.setString(1,id);
			//prep_newAgent.setString(2,newAgent.getName());
			//prep_newGroup.setInt(3,ec.getRoundsPassed());
			//prep_newAgent.setDouble(4,newAgent.getSocialBelief());
			//prep_newAgent.setDouble(5,newAgent.getEconomicBelief());
			prep_newAgent.addBatch();
		    } catch (SQLException ex) {
			logger.log(Level.SEVERE, null, ex);
			return;
		    }
		}
	    }

	    // Delete agents which are no longer active
	    List<String> ids_to_remove = new LinkedList<String>();
	    for(Map.Entry<String, PublicAgentDataModel> entry : agentMap.entrySet())
	    {
		String id = entry.getKey();
		if(!active_group_ids.contains(id))
		{
		    try {
			ids_to_remove.add(id);
			prep_dieAgent.setInt(1,ec.getRoundsPassed());
			prep_dieAgent.setString(2,id);
			prep_dieAgent.addBatch();
		    } catch (SQLException ex) {
			logger.log(Level.SEVERE, null, ex);
		    }
		}
	    }
	    iter = ids_to_remove.iterator();
	    while(iter.hasNext())
	    {
		    agentMap.remove(iter.next());
	    }
    }
	   
    @Override
    public void execute()
    {
	try {
	    //data only updated at the beginning of turn
	    if (ec.getCurrentTurnType() != TurnType.firstTurn) return;
	    
		updateAgents();
		if (sim.getTime()%50 == 0) {
		    prep_newAgent.executeBatch();
		    prep_dieAgent.executeBatch();
		}
		if (sim.getTime()%100 == 0) {
		    conn.commit();
		}
	} catch (SQLException ex) {
	    Logger.getLogger(DatabasePlugin.class.getName()).log(Level.SEVERE, null, ex);
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
	    prep_dieAgent.executeBatch();
	    conn.commit();
	    //prep.close();
	    stat = conn.createStatement();
	    //Update agent end time to end of simulation, if still lives
	    stat.executeUpdate("UPDATE [agents]\n"
					+ "SET end="+ec.getRoundsPassed()+"\n"
					+ "WHERE simid="+simid+"\n"
					+ "AND end IS NULL;");
	    stat.executeUpdate("UPDATE [simulations]\n"
					+ "SET done=1,rounds="+ec.getRoundsPassed()+"\n"
					+ "WHERE simid="+simid+";");
	    conn.commit();
	    stat.close();
	    if (saveToRemote) {

	    }
	    //commits all transactions
	    conn.close();
	}
	catch (SQLException ex) {
	    logger.log(Level.WARNING,"Database finalize error", ex);
	    try {
		conn.close();
	    } catch (SQLException ex1) {
		Logger.getLogger(DatabasePlugin.class.getName()).log(Level.SEVERE, null, ex1);
	    }
	    return;
	}
    }
	
}


