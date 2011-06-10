package ise.gameoflife.plugins.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Benedict
 */
final class ConnectionWrapper
{
	private final Connection conn;
	private final PreparedStatement newAgent;
	private final PreparedStatement dieAgent;
	private final PreparedStatement newGroup;
	private final PreparedStatement dieGroup;
	private final PreparedStatement roundAgent;
	private final PreparedStatement roundGroup;

	ConnectionWrapper(String uri) throws SQLException
	{
		conn = DriverManager.getConnection(uri);
		conn.setAutoCommit(false);

		newAgent = conn.prepareStatement(Statements.addAgent.getPrototype());
		dieAgent = conn.prepareStatement(Statements.dieAgent.getPrototype());
		roundAgent = conn.prepareStatement(Statements.roundAgent.getPrototype());

		newGroup = conn.prepareStatement(Statements.addGroup.getPrototype());		
		dieGroup = conn.prepareStatement(Statements.dieGroup.getPrototype());
		roundGroup = conn.prepareStatement(Statements.roundGroup.getPrototype());

		initialiseSimulation();
	}

	private void initialiseSimulation()
	{
	}
}
