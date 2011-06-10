package ise.gameoflife.plugins.database;

import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.participants.PublicGroupDataModel;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import presage.Plugin;
import presage.Simulation;

/**
 *
 * @author Benedict
 */
public class NewDatabasePlugin implements Plugin
{
	private final static String DB_URI;
	private final static Logger logger = Logger.getLogger("ise.gameoflife.dbplugin");
	private final static String name = "Database v2";

	static
	{
		String SCHEME = "jdbc:mysql";
		String USER = "stratra1_isegol";
		String PASSWORD = "stratra1_isegol";
		String HOST = "69.175.26.66";
		int PORT = 3306;
		String PATH = "stratra1_isegol";

		try
		{
			URI u = new URI(SCHEME, USER + ':' + PASSWORD, HOST, PORT, PATH, null,
							null);
			DB_URI = u.toASCIIString();
		}
		catch (URISyntaxException ex)
		{
			throw new Error(ex);
		}
	}

	private ConnectionWrapper wrap;
	private PublicEnvironmentConnection conn;
	private final TreeMap<String, PublicGroupDataModel> trackedGroups 
					= new TreeMap<String, PublicGroupDataModel>();

	@Override
	public void execute()
	{
		findNewGroups();
	}

	@Override
	public void initialise(Simulation sim)
	{
		try
		{
			wrap = new ConnectionWrapper(DB_URI, sim);
		}
		catch (SQLException ex)
		{
			logger.log(Level.SEVERE, null, ex);
		}

		conn = PublicEnvironmentConnection.getInstance();
	}

	@Override
	public void onDelete()
	{
		wrap.flush();
	}

	@Override
	public void onSimulationComplete()
	{
		wrap.flush();
	}

	@Override
	public String getLabel()
	{
		return name;
	}

	@Override
	public String getShortLabel()
	{
		return name;
	}

	private void findNewGroups()
	{
		// Add in any new groups
		TreeSet<String> newGroups = new TreeSet<String>(conn.availableGroups());
		newGroups.removeAll(trackedGroups.keySet());

		for (String g : newGroups)
		{
			wrap.groupAdd(g);
			trackedGroups.put(g, conn.getGroupById(g));
		}
	}

	
	private void pruneOldGroups()
	{
		// Add in any new groups
		TreeSet<String> oldGroups = new TreeSet<String>(trackedGroups.keySet());
		oldGroups.removeAll(conn.availableGroups());

		for (String g : oldGroups)
		{
			wrap.groupDie(g);
			trackedGroups.remove(g);
		}
	}
}
