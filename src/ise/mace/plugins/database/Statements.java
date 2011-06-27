package ise.mace.plugins.database;

/**
 *
 */
enum Statements
{   
	addUser("INSERT INTO users (username,first_name,last_name) VALUES (?,?,?)"
		+ " ON DUPLICATE KEY UPDATE userid=LAST_INSERT_ID(userid);"),	
	addSim("INSERT INTO simulations (sim_uuid,userid,comment,loans) VALUES (?,?,?,?);"),
	endSim("UPDATE simulations SET end=CURRENT_TIMESTAMP,done=1,rounds=? WHERE simid=?;"),
	endAgent("UPDATE agents SET end=? WHERE simid=?	AND end IS NULL;"),
	endGroup("UPDATE groups SET end=? WHERE simid=?	AND end IS NULL;"),	   
	addAgent("INSERT INTO agents (simid, agentid, a_uuid, name, start) VALUES (?,?,?,?,?);"),
	addGroup("INSERT INTO groups (simid, groupid, g_uuid, start, greediness) VALUES (?,?,?,?,?);"),
	dieAgent("UPDATE agents SET end=? WHERE simid=? AND agentid=?;"),
	dieGroup("UPDATE groups SET end=? WHERE simid=? AND groupid=?;"),
	
	roundGroup(
	"INSERT INTO g_data (simid,round,groupid,pop,socialPosition,economicPosition)"
	    + " VALUES (?,?,?,?,?,?);"),
	
	roundLoanGroup(
	"INSERT INTO lg_data (simid,round,groupid,pop,socialPosition,economicPosition,reservedFood,happiness,totalgiven,totalborrowed)"
	    + " VALUES (?,?,?,?,?,?,?,?,?,?);"),
	
	roundAgent(
	"INSERT INTO a_data (simid,round,agentid,groupid,foodAmount,lastHunted,"
		+ "socialBelief,economicBelief,happiness,loyalty)"
	    + " VALUES (?,?,?,?,?,?,?,?,?,?);"),
	trustAgent("INSERT INTO a_trust (simid,round,agentid,agentid_other,trust)"
	    + " VALUES (?,?,?,?,?);"),
	
	createSim("CREATE TABLE IF NOT EXISTS [simulations] (\n"+
	"[simid] INTEGER NOT NULL PRIMARY KEY ASC, \n"+
	"[sim_uuid] TEXT NOT NULL, \n"+
	"[userid] TEXT, \n"+
	"[comment] TEXT, \n"+
	"[start] TIMESTAMP DEFAULT (CURRENT_TIMESTAMP), \n"+
	"[end] TIMESTAMP,\n"+
	"[rounds] INTEGER,\n"+
	"[done] INTEGER DEFAULT (0),\n"+
	"[loans] INTEGER DEFAULT (0));"),
	
	createAgents("CREATE TABLE IF NOT EXISTS [agents] (\n"+
	"[simid] INTEGER NOT NULL REFERENCES [simulations]([simid]) ON DELETE CASCADE ON UPDATE CASCADE, \n"+
	"[agentid] INTEGER NOT NULL,\n"+
	"[a_uuid] TEXT NOT NULL, \n"+
	"[name] TEXT NOT NULL,\n"+
	"[start] INTEGER NOT NULL,\n"+
	"[end] INTEGER,\n"+
	"PRIMARY KEY ([simid], [agentid]));"),

	createGroups("CREATE TABLE IF NOT EXISTS [groups] (\n"+
	"[simid] INTEGER NOT NULL REFERENCES [simulations]([simid]) ON DELETE CASCADE ON UPDATE CASCADE, \n"+
	"[groupid] INTEGER NOT NULL,\n"+
	"[g_uuid] TEXT NOT NULL,\n"+
	"[start] INTEGER NOT NULL,\n"+
	"[end] INTEGER,\n"+
	"[greediness] DOUBLE,\n"+
	"PRIMARY KEY ([simid], [groupid]));"),

	createG_data("CREATE TABLE IF NOT EXISTS [g_data] (\n"+
	"[simid] INTEGER NOT NULL,\n"+
	"[round] INTEGER  NOT NULL,\n"+
	"[groupid] INTEGER NOT NULL,\n"+
	"[pop] INTEGER,\n"+
	"[socialPosition] DOUBLE,\n"+
	"[economicPosition] DOUBLE,\n"+
	"FOREIGN KEY([simid],[groupid]) REFERENCES [groups]([simid],[groupid]) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,\n"+
	"PRIMARY KEY ([simid],[round],[groupid]));"),
	
	createLG_data("CREATE TABLE IF NOT EXISTS [lg_data] (\n"+
	"[simid] INTEGER NOT NULL,\n"+
	"[round] INTEGER  NOT NULL,\n"+
	"[groupid] INTEGER NOT NULL,\n"+
	"[pop] INTEGER,\n"+
	"[socialPosition] DOUBLE,\n"+
	"[economicPosition] DOUBLE,\n"+
	"[reservedFood] DOUBLE,\n"+
	"[happiness] DOUBLE,\n"+
	"[totalgiven] DOUBLE,\n"+
	"[totalborrowed] DOUBLE,\n"+
	"FOREIGN KEY([simid],[groupid]) REFERENCES [groups]([simid],[groupid]) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,\n"+
	"PRIMARY KEY ([simid],[round],[groupid]));"),

	createA_data("CREATE TABLE IF NOT EXISTS [a_data] (\n"+
	"[simid] INTEGER NOT NULL,\n"+
	"[round] INTEGER  NOT NULL,\n"+
	"[agentid] INTEGER NOT NULL,\n"+
	"[groupid] INTEGER,\n"+
	"[foodAmount] DOUBLE NOT NULL,\n"+
	"[lastHunted] TEXT, \n"+
	"[socialBelief] DOUBLE,\n"+
	"[economicBelief] DOUBLE,\n"+
	"[happiness] DOUBLE,\n"+
	"[loyalty] DOUBLE,\n"+
	"FOREIGN KEY([simid],[groupid]) REFERENCES [groups]([simid],[groupid]) ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,\n"+
	"FOREIGN KEY([simid],[agentid]) REFERENCES [agents]([simid],[agentid]) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,\n"+
	"PRIMARY KEY ([simid],[round],[agentid]));"),

	createTrust("CREATE TABLE IF NOT EXISTS [a_trust] (\n"+
	"[simid] INTEGER NOT NULL,\n"+
	"[round] INTEGER  NOT NULL,\n"+
	"[agentid] INTEGER NOT NULL,\n"+
	"[agentid_other] INTEGER NOT NULL,\n"+
	"[trust]  DOUBLE NOT NULL,\n"+
	"FOREIGN KEY([simid],[agentid]) REFERENCES [agents]([simid],[agentid]) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,\n"+
	"FOREIGN KEY([simid],[agentid_other]) REFERENCES [agents]([simid],[agentid]) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,\n"+
	"PRIMARY KEY ([simid],[round],[agentid],[agentid_other]));");

	
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
