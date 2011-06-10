package ise.gameoflife.plugins.database;

import ise.gameoflife.environment.PublicEnvironmentConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import presage.Simulation;

/**
 *
 * @author Benedict
 */
final class ConnectionWrapper
{
	private final static Logger logger = Logger.getLogger("ise.gameofline.dbconn");
	private final Connection conn;
	private final PreparedStatement newAgent;
	private final PreparedStatement dieAgent;
	private final PreparedStatement newGroup;
	private final PreparedStatement dieGroup;
	private final PreparedStatement roundAgent;
	private final PreparedStatement roundGroup;
	private final PublicEnvironmentConnection envConn;

	ConnectionWrapper(String uri, Simulation sim) throws SQLException
	{
		conn = DriverManager.getConnection(uri);
		conn.setAutoCommit(false);

		newAgent = conn.prepareStatement(Statements.addAgent.getPrototype());
		dieAgent = conn.prepareStatement(Statements.dieAgent.getPrototype());
		roundAgent = conn.prepareStatement(Statements.roundAgent.getPrototype());

		newGroup = conn.prepareStatement(Statements.addGroup.getPrototype());
		dieGroup = conn.prepareStatement(Statements.dieGroup.getPrototype());
		roundGroup = conn.prepareStatement(Statements.roundGroup.getPrototype());

		envConn = PublicEnvironmentConnection.getInstance();
		if (envConn == null) throw new IllegalStateException(
							"Connection created before EnviromentConnection was accessible");
		initialiseSimulation(sim);
	}

	private void initialiseSimulation(Simulation sim) throws SQLException
	{
		PreparedStatement simAdd = conn.prepareStatement(
						Statements.addSim.getPrototype());
		int simId = 0;

		simAdd.setString(1, envConn.getId());
		// TODO: Initialise these two params better
		simAdd.setString(2, System.getProperty("user.name"));
		simAdd.setString(3, "Comment");

		simAdd.execute();
		ResultSet rs = simAdd.getGeneratedKeys();

		while (rs.next())
		{
			simId = rs.getInt(1);
		}

		logger.log(Level.INFO, "SimulationId in DB = {0}", simId);

		newAgent.setInt(1, simId);
		dieAgent.setInt(2, simId);
		roundAgent.setInt(1, simId);
		newGroup.setInt(1, simId);
		dieGroup.setInt(2, simId);
		roundGroup.setInt(1, simId);
	}

	void flush()
	{
		try
		{
			conn.commit();
		}
		catch (SQLException ex)
		{
			logger.log(Level.SEVERE, null, ex);
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
			logger.log(Level.SEVERE, null, ex);
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
			logger.log(Level.SEVERE, null, ex);
		}
	}
}
