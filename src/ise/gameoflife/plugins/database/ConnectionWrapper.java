package ise.gameoflife.plugins.database;

import ise.gameoflife.environment.PublicEnvironmentConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	private final PreparedStatement endAgent;
	private final PreparedStatement newGroup;
	private final PreparedStatement dieGroup;
	private final PreparedStatement roundGroup;
	private final PreparedStatement endGroup;
	private final PreparedStatement endSim;
	private final PublicEnvironmentConnection envConn;

	ConnectionWrapper(String url, String comment, Boolean remote) throws SQLException, ClassNotFoundException
	{
		if (!remote) Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection(url);
		if (!remote) conn.setAutoCommit(false);
		
		newAgent = conn.prepareStatement(Statements.addAgent.getPrototype());
		dieAgent = conn.prepareStatement(Statements.dieAgent.getPrototype());
		roundAgent = conn.prepareStatement(Statements.roundAgent.getPrototype());
		endAgent = conn.prepareStatement(Statements.endAgent.getPrototype());
		newGroup = conn.prepareStatement(Statements.addGroup.getPrototype());
		dieGroup = conn.prepareStatement(Statements.dieGroup.getPrototype());
		roundGroup = conn.prepareStatement(Statements.roundGroup.getPrototype());
		endGroup = conn.prepareStatement(Statements.endGroup.getPrototype());
		endSim = conn.prepareStatement(Statements.endSim.getPrototype());

		envConn = PublicEnvironmentConnection.getInstance();
		if (envConn == null) throw new IllegalStateException(
		    "Connection created before EnviromentConnection was accessible");
		initialiseSimulation(comment,remote);
	}

	private void initialiseSimulation(String comment, Boolean remote) throws SQLException
	{
		PreparedStatement simAdd = conn.prepareStatement(
		    Statements.addSim.getPrototype());
		simAdd.setString(1, envConn.getId());
		
		//if connecting to remote database
		int userId = 0;
		String userName = System.getProperty("user.name");
		logger.log(Level.INFO, "Your userName = {0}.", userName);
		if (remote) {
		    //get a userid from username table
		    PreparedStatement userAdd = conn.prepareStatement(
			Statements.addUser.getPrototype());
		    userAdd.setString(1,userName);
		    userAdd.setString(2,userName);
		    userAdd.setString(3," ");
		    userAdd.execute();
		    ResultSet rs = userAdd.getGeneratedKeys();
		    while (rs.next()) userId = rs.getInt(1);
		    rs.close();
		    logger.log(Level.INFO, "Your remote userId = {0}.",userId);
		    userAdd.close();
  		    simAdd.setInt(2,userId);
		}
		//in local db just use username instead of userId
		else {
		    simAdd.setString(2, userName);
		}
		
		simAdd.setString(3,comment);
		simAdd.execute();
		ResultSet rs = simAdd.getGeneratedKeys();
		int simId = 0;
		while (rs.next())
		{
			simId = rs.getInt(1);
		}
		rs.close();
		simAdd.close();
		logger.log(Level.INFO, "Current SimulationId = {0}.", simId);

		
		newAgent.setInt(1, simId);
		dieAgent.setInt(2, simId);
		roundAgent.setInt(1, simId);
		newGroup.setInt(1, simId);
		dieGroup.setInt(2, simId);
		roundGroup.setInt(1, simId);
		endAgent.setInt(2, simId);
		endGroup.setInt(2, simId);
		endSim.setInt(2, simId);
	}

	void flush()
	{
		try
		{
		    newGroup.executeBatch();
		    dieGroup.executeBatch();
		    newAgent.executeBatch();
		    dieAgent.executeBatch();
		    if (!conn.getAutoCommit()) conn.commit();
		}
		catch (SQLException ex)
		{
		    logger.log(Level.WARNING, null, ex);
		}
	}
	
	void end() {
	    try {
		endAgent.setInt(1, envConn.getRoundsPassed());
		endGroup.setInt(1, envConn.getRoundsPassed());
		endSim.setInt(1, envConn.getRoundsPassed());
		endAgent.execute();
		endGroup.execute();
		endSim.execute();
		if (!conn.getAutoCommit()) conn.commit();
		conn.close();
		logger.log(Level.INFO,"Final writes to database complete.");
	    } catch (SQLException ex) {
		logger.log(Level.WARNING, null, ex);
	    }
	}

	void groupAdd(String id)
	{
		try
		{
		    newGroup.setString(2, id);
		    newGroup.setInt(3, envConn.getRoundsPassed());
		    newGroup.addBatch();
		}
		catch (SQLException ex)
		{
		    logger.log(Level.WARNING, null, ex);
		}
	}

	void groupDie(String id)
	{
		try
		{
			dieGroup.setString(3, id);
			dieGroup.setInt(1, envConn.getRoundsPassed());
			dieGroup.addBatch();
		}
		catch (SQLException ex)
		{
			logger.log(Level.WARNING, null, ex);
		}
	}

	void agentAdd(String id)
	{
		try
		{
		    newAgent.setString(2, id);
		    //agent name
		    newAgent.setString(3,envConn.getAgentById(id).getName());
		    newAgent.setInt(4, envConn.getRoundsPassed());
		    newAgent.addBatch();
		}
		catch (SQLException ex)
		{
		    logger.log(Level.WARNING, null, ex);
		}
	}
	
	void agentDie(String id)
	{
		try
		{
			dieAgent.setString(3, id);
			dieAgent.setInt(1, envConn.getRoundsPassed());
			dieAgent.addBatch();
		}
		catch (SQLException ex)
		{
			logger.log(Level.WARNING, null, ex);
		}
	}
    }
