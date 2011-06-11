package ise.gameoflife.plugins.database;

/**
 *
 * @author Valdas
 */
enum Statements
{   
	addUser("INSERT INTO users (username,first_name,last_name) VALUES (?,?,?)"
		+ " ON DUPLICATE KEY UPDATE userid=LAST_INSERT_ID(userid);"),	
	addSim("INSERT INTO simulations (sim_uuid,userid,comment) VALUES (?,?,?);"),
	endSim("UPDATE simulations SET end=CURRENT_TIMESTAMP,done=1,rounds=? WHERE simid=?;"),
	endAgent("UPDATE agents SET end=? WHERE simid=?	AND end IS NULL;"),
	endGroup("UPDATE groups SET end=? WHERE simid=?	AND end IS NULL;"),	   
	addAgent("INSERT INTO agents (simid, agentid, a_uuid, name, start) VALUES (?,?,?,?,?);"),
	addGroup("INSERT INTO groups (simid, groupid, g_uuid, start) VALUES (?,?,?,?);"),
	dieAgent("UPDATE agents SET end=? WHERE simid=? AND agentid=?;"),
	dieGroup("UPDATE groups SET end=? WHERE simid=? AND groupid=?;"),
	roundGroup(
	"INSERT INTO g_data (simid,round,groupid,pop,socialPosition,economicPosition)"
	    + " VALUES (?,?,?,?,?,?);"),
	roundAgent(
	"INSERT INTO a_data (simid,round,agentid,groupid,foodAmount,lastHunted,"
		+ "socialBelief,economicBelief,happiness,loyalty)"
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
