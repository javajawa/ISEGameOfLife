package ise.gameoflife.plugins.database;

import ise.gameoflife.participants.PublicAgentDataModel;
import ise.gameoflife.participants.PublicGroupDataModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Valdas
 */
final class ConnectionWrapper
{
	private final static Logger logger = Logger.getLogger("gameoflife.DatabasePlugin");
	private final Connection conn;
	private final PreparedStatement newAgent;
	private final PreparedStatement dieAgent;
	private final PreparedStatement roundAgent;
	private final PreparedStatement trustAgent;
	private final PreparedStatement endAgent;
	private final PreparedStatement newGroup;
	private final PreparedStatement dieGroup;
	private final PreparedStatement roundGroup;
	private final PreparedStatement endGroup;
	private final PreparedStatement endSim;
	//private final PublicEnvironmentConnection envConn;
	private final int simId;

	ConnectionWrapper(String url, String comment, String sim_uuid, Boolean remote) throws SQLException, ClassNotFoundException
	{
		if (!remote) Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection(url);
		//remote db needs autocommiting so as not to break foreign key constraints
		if (!remote) {
		    //creates tables for database
		    updateDatabaseStructure();
		    conn.setAutoCommit(false);
		}
		newAgent = conn.prepareStatement(Statements.addAgent.getPrototype());
		dieAgent = conn.prepareStatement(Statements.dieAgent.getPrototype());
		roundAgent = conn.prepareStatement(Statements.roundAgent.getPrototype());
		trustAgent = conn.prepareStatement(Statements.trustAgent.getPrototype());
		endAgent = conn.prepareStatement(Statements.endAgent.getPrototype());
		newGroup = conn.prepareStatement(Statements.addGroup.getPrototype());
		dieGroup = conn.prepareStatement(Statements.dieGroup.getPrototype());
		roundGroup = conn.prepareStatement(Statements.roundGroup.getPrototype());
		endGroup = conn.prepareStatement(Statements.endGroup.getPrototype());
		endSim = conn.prepareStatement(Statements.endSim.getPrototype());
	
		simId = initialiseSimulation(comment, sim_uuid, remote);
	}

	private int initialiseSimulation(String comment, String sim_uuid, Boolean remote) throws SQLException
	{
		PreparedStatement simAdd = conn.prepareStatement(
		    Statements.addSim.getPrototype());
		simAdd.setString(1,sim_uuid);
		
		//if connecting to remote database
		
		String userName = System.getProperty("user.name");
		logger.log(Level.INFO, "Your userName = {0}", userName);
		if (remote) {
		    //get a userid from username table
		    PreparedStatement userAdd = conn.prepareStatement(
			Statements.addUser.getPrototype());
		    userAdd.setString(1,userName);
		    userAdd.setString(2,userName);
		    userAdd.setString(3," ");
		    userAdd.executeUpdate();
		    ResultSet rs = userAdd.getGeneratedKeys();
		    int userId = 0;
		    if (rs.next()) userId = rs.getInt(1);
		    else logger.log(Level.WARNING, "Acquiring userId failed. Using default");
		    rs.close();
		    userAdd.close();
		    logger.log(Level.INFO, "Your remote userId = {0}",userId);
  		    simAdd.setInt(2,userId);
		}
		//in local db just use username instead of userId
		else {
		    simAdd.setString(2, userName);
		}
		
		simAdd.setString(3,comment);
		simAdd.executeUpdate();
		logger.log(Level.INFO, "Simulation comment: {0}", comment);
		ResultSet rs = simAdd.getGeneratedKeys();
		int simulationId = 0;
		if (rs.next()) simulationId = rs.getInt(1);
		else logger.log(Level.WARNING, "Acquiring simulationId failed. Using default");
		rs.close();
		simAdd.close();
		logger.log(Level.INFO, "Current simulationId = {0}", simulationId);
		if (!conn.getAutoCommit()) conn.commit();
		if (remote) logger.log(Level.INFO, "ALL WRITES TO REMOTE DB WILL BE DONE AT END OF SIMULATION");  
		newAgent.setInt(1, simulationId);
		dieAgent.setInt(2, simulationId);
		roundAgent.setInt(1, simulationId);
		trustAgent.setInt(1, simulationId);
		newGroup.setInt(1, simulationId);
		dieGroup.setInt(2, simulationId);
		roundGroup.setInt(1, simulationId);
		endAgent.setInt(2, simulationId);
		endGroup.setInt(2, simulationId);
		endSim.setInt(2, simulationId);
		
		return simulationId;
	}
	
	void flush(int round)
	{
		try
		{
		    
		    newGroup.executeBatch();
		    dieGroup.executeBatch();
		    newAgent.executeBatch();
		    dieAgent.executeBatch();
		    roundGroup.executeBatch();
		    roundAgent.executeBatch();
		    trustAgent.executeBatch();
		    if (!conn.getAutoCommit()) conn.commit();
		}
		catch (SQLException ex)
		{
		    logger.log(Level.WARNING, null, ex);
		}
	}
	
	void end(int round) 
	{
		try {
		    endAgent.setInt(1, round);
		    endGroup.setInt(1, round);
		    endSim.setInt(1, round);
		    endAgent.execute();
		    endGroup.execute();
		    endSim.execute();
		    if (!conn.getAutoCommit()) conn.commit();
		    conn.close();
		    logger.log(Level.INFO,"Upload complete. Database connection closed");
		} catch (SQLException ex) {
		    logger.log(Level.WARNING, null, ex);
		}
	}

	void groupAdd(String id,int groupid, int round)
	{
		try
		{
		    newGroup.setInt(1, simId);
		    newGroup.setInt(2, groupid);
		    newGroup.setString(3, id);
		    newGroup.setInt(4, round);
		    newGroup.addBatch();
		}
		catch (SQLException ex)
		{
		    logger.log(Level.WARNING, null, ex);
		}
	}

	void groupDie(int groupid, int round)
	{
		try
		{
			dieGroup.setInt(3, groupid);
			dieGroup.setInt(1, round);
			dieGroup.addBatch();
		}
		catch (SQLException ex)
		{
			logger.log(Level.WARNING, null, ex);
		}
	}

	void agentAdd(String id,int agentid, int round, String name)
	{
		try
		{
		    newAgent.setInt(2, agentid);
		    newAgent.setString(3, id);
		    //agent name
		    newAgent.setString(4,name);
		    newAgent.setInt(5,  round);
		    newAgent.addBatch();
		}
		catch (SQLException ex)
		{
		    logger.log(Level.WARNING, null, ex);
		}
	}
	
	void agentDie(int agentid, int round)
	{
	    try
	    {
		dieAgent.setInt(3, agentid);
		dieAgent.setInt(1,  round);
		dieAgent.addBatch();
	    }
	    catch (SQLException ex)
	    {
		logger.log(Level.WARNING, null, ex);
	    }
	}

	void groupRound(int groupid, int round, PublicGroupDataModel group) {
	     try {
		 //for some reason, needs simId reassigned or error
		 roundGroup.setInt(1, simId);
		 roundGroup.setInt(2, round);
		 roundGroup.setInt(3,groupid);
		 roundGroup.setInt(4,group.getMemberList().size());
		 roundGroup.setDouble(5,group.getEstimatedSocialLocation());
		 roundGroup.setDouble(6,group.getCurrentEconomicPoisition());
		     //logger.log(Level.WARNING,"Economic position not defined for group {1} on round {2}. Set to -1 instead",
			//     new Object[]{group.getId(), round});
		 roundGroup.addBatch();
	    } catch (SQLException ex) {
		logger.log(Level.WARNING, "{0} (db groupid: {2}) error for round {1}:{3}"
		    + " data not stored.", new Object[]{group.getName(), round, groupid, ex});
	    }
	}

	void agentRound(int agentid, int groupid, int round, PublicAgentDataModel agent) {
	    try {
		//for some reason, needs simId reassigned or error
		roundAgent.setInt(1, simId);
		roundAgent.setInt(2, round);
		roundAgent.setInt(3,agentid);
		//sets the database id for group, 0 for no group
		roundAgent.setInt(4,groupid);
		roundAgent.setDouble(5,agent.getFoodAmount());
		if (round==0) {
		    roundAgent.setString(6,null);
		    roundAgent.setDouble(9,-1);
		    roundAgent.setDouble(10,-1);
		}
		else {
		    if(agent.getLastHunted()==null) roundAgent.setString(6,null);
			else roundAgent.setString(6,agent.getLastHunted().getName());
		    roundAgent.setDouble(9,agent.getCurrentHappiness());
		    roundAgent.setDouble(10,agent.getCurrentLoyalty());
		}
		
		roundAgent.setDouble(7,agent.getSocialBelief());
		roundAgent.setDouble(8,agent.getEconomicBelief());
		roundAgent.addBatch();
	    } catch (SQLException ex) {
		logger.log(Level.WARNING, null, ex);
	    } catch (NullPointerException ex) {
		logger.log(Level.WARNING, "Null Exception: Agent {0} data for round {1}"
			+ " not stored.", new Object[]{agent.getName(), round});
	    }
	}
	
	void agentTrust(int agentid, int agentid_other, double trust, int round) {
	    try {
		//updates trust between agents
		trustAgent.setInt(1, simId);
		trustAgent.setInt(2, round);
		trustAgent.setInt(3,agentid);
		trustAgent.setInt(4,agentid_other);
		trustAgent.setDouble(5,trust);
		trustAgent.addBatch();
		
	    } catch (SQLException ex) {
		logger.log(Level.WARNING, null, ex);
	    }
	}

	private void updateDatabaseStructure() 
	{
	    try {
		    //updates or creates local database tables
		    Statement stat = conn.createStatement();
		    stat.addBatch(Statements.createSim.getPrototype());
		    stat.addBatch(Statements.createGroups.getPrototype());
		    stat.addBatch(Statements.createAgents.getPrototype());
		    stat.addBatch(Statements.createG_data.getPrototype());
		    stat.addBatch(Statements.createA_data.getPrototype());
		    stat.addBatch(Statements.createTrust.getPrototype());
		    stat.executeBatch();
		    stat.close();
	    
		} catch (SQLException ex) {
		    logger.log(Level.WARNING,"Failed to create database tables", ex);
		}
	}
	void getFreeAgentGroupData(int round,int pop) {
	         try {
		 //updates the FreeAgensGroup
		 roundGroup.setInt(1, simId);
		 roundGroup.setInt(2, round);
		 roundGroup.setInt(3,0);
		 roundGroup.setInt(4,pop);
		 roundGroup.setDouble(5,-1);
		 roundGroup.setDouble(6,-1);
		 roundGroup.addBatch();
	    } catch (SQLException ex) {
		logger.log(Level.WARNING, null, ex);
	    }
	}
}
