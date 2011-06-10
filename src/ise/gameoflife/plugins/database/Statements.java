package ise.gameoflife.plugins.database;

/**
 *
 * @author Benedict
 */
enum Statements
{
	addAgent("INSERT INTO agents (simid, a_uuid, name, start) VALUES (?,?,?,?);"),
	addGroup("INSERT INTO groups (simid, g_uuid, start) VALUES (?,?,?);"),
	dieAgent("UPDATE agents SET end=? WHERE simid=? AND a_uuid=?;"),
	dieGroup("UPDATE groups SET end=? WHERE simid=? AND g_uuid=?;"),
	roundGroup(
	"INSERT into g_data (simid,round,g_uuid,pop,socialPosition,economicPosition)"
	+ " VALUES (?,?,?,?,?,?);"),
	roundAgent(
	"INSERT into a_data (simid,round,a_uuid,g_uuid,foodAmount,lastHunted,socialBelief,economicBelief,happiness,loyalty)"
	+ " VALUES (?,?,?,?,?,?,?,?,?,?);");
	private String prototype;

	private Statements(String prototype)
	{
		this.prototype = prototype;
	}

	public String getPrototype()
	{
		return prototype;
	}
}
