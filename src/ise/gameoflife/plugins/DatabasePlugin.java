package ise.gameoflife.plugins;

import ise.gameoflife.agents.TestPoliticalAgent;
import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.participants.PublicAgentDataModel;
import ise.gameoflife.tokens.TurnType;
import org.simpleframework.xml.Element;
import java.util.SortedSet;
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
    
    public final static Logger logger = Logger.getLogger("gameoflife.DatabaseLogger");

    private Simulation sim;
    private PublicEnvironmentConnection ec = null;
    
    private TreeMap<String, PublicAgentDataModel> agentMap = new TreeMap<String, PublicAgentDataModel>();
    
    //database connection
    private Connection conn;
    private Statement stat;
    private PreparedStatement prep;
    private PreparedStatement prep_newAgent;
    



    @Element
    private int simId;
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
     * @param simId - Simulation ID inside lDB
     * @param sim_comment - Comment inside DB
     * @param saveToRemote - uses remote DB instead of local
     */
    @PluginConstructor(
    {
	    "simId","sim_comment","saveToRemote"
    })
    public DatabasePlugin(int simId, String sim_comment, Boolean saveToRemote)
    {
	    super();
	    this.simId = simId;
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
		simId  = rs.getInt(1);    
	    }
	    rs.close();                           // Close ResultSet
	    logger.log(Level.INFO, "This simulation is saved in DB with: simId = {0}", simId);
	    stat.close();                         // Close Statement

	    conn.setAutoCommit(false);
	    prep = conn.prepareStatement(
		"INSERT into data values ('"+simId+"', ?, ?, ? );");
	    prep_newAgent = conn.prepareStatement("INSERT into [agents] (simid,a_uuid,name) VALUES(?,?,?)");
	    
	       /*
		  stat.executeUpdate("INSERT INTO simulations " +                             
		   "VALUES (null,'"+System.getProperty("user.name")+"','"+sim_comment+"',null,0);",      
		   Statement.RETURN_GENERATED_KEYS);   // Indicate you want automatically 
						       // generated keys
		rs = stat.getGeneratedKeys();         // Retrieve the automatically       
						       // generated key value in a ResultSet.
						       // Only one row is returned.
					 // Create ResultSet for query
		while (rs.next()) {
		    simId  = rs.getInt(1);     // Get automatically generated key 
						       // value
		    System.out.println("Remote DB simId = " + simId);
		}
		rs.close();                           // Close ResultSet
		stat.close();                         // Close Statement
		prep2 = rcon.prepareStatement(
		"insert into data values ('"+simId+"', ?, ?, ? );");
	    */


	}
	catch (SQLException e) {
	    logger.log(Level.WARNING, "SQL Error: {0}", e);
	    e.printStackTrace();
	    return;
	}
    }
    
    
    
    private void updateAgents()
    {
	   SortedSet<String> active_agent_ids = sim.getactiveParticipantIdSet("hunter");
	   
	   Iterator<String> iter = active_agent_ids.iterator();

	    // Add any new agents
	    while(iter.hasNext())
	    {
		    String id = iter.next();
		    if(!agentMap.containsKey(id))
		    {
			PublicAgentDataModel newAgent = agentMap.put(id, (PublicAgentDataModel) ec.getAgentById(id));
			prep_newAgent.setInt(1,simId);
			prep_newAgent.setString(2,newAgent.getId());
			prep_newAgent.setInt(3,getNumHunters());
			prep_newAgent.addBatch();
			
		    }


	    }

	    // Delete agents which are no longer active
	    List<String> ids_to_remove = new LinkedList<String>();
	    for(Map.Entry<String, TestPoliticalAgent> entry : agentMap.entrySet())
	    {
		    String id = entry.getKey();
		    if(!active_agent_ids.contains(id))
		    {
			    ids_to_remove.add(id);
		    }
	    }
	    iter = ids_to_remove.iterator();
	    while(iter.hasNext())
	    {
		    agentMap.remove(iter.next());
	    }
    }
	   
    private int getNumHunters()
    {
	    SortedSet<String> participantIdSet = sim.getactiveParticipantIdSet("hunter");
	    return participantIdSet.size();
    }

    @Override
    public void execute()
    {
	//people only updated at the beginning of turn
	if (ec.getCurrentTurnType() != TurnType.firstTurn) return;
	try {
		
		//prep.setInt(1,local_simId);
		prep.setLong(1, sim.getTime());
		prep.setInt(2,ec.getRoundsPassed());
		prep.setInt(3,getNumHunters());
		prep.addBatch();
		if (saveToRemote) {
		    //prep2.setInt(1,remote_simId);
		    prep2.setLong(1, sim.getTime());
		    prep2.setInt(2,ec.getRoundsPassed());
		    prep2.setInt(3,getNumHunters());
		    prep2.addBatch();
		}
		//sends data to DB every 100 cycles
		if (sim.getTime()%100 == 0) {
		    prep.executeBatch();
		    if (saveToRemote) prep2.executeBatch();
		    if (sim.getTime()%1000 == 0) {
			conn.commit();
		    }
	        }

	}
	catch (SQLException e) {
	    e.printStackTrace();
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
	    prep.executeBatch();
	    conn.commit();
	    prep.close();
	    stat = conn.createStatement();
	    int executeUpdate = stat.executeUpdate("UPDATE simulations\n"
					+ "SET done='1'\n"
					+ "WHERE simId='"+simId+"';");
	    conn.commit();
	    stat.close();
	    if (saveToRemote) {

	    }
	    //commits all transactions
	    conn.close();
	}
	catch (SQLException e) {
	    e.printStackTrace();
	    return;
	}
    }
	
}


