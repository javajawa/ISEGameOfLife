package ise.gameoflife.plugins.database;

import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.participants.PublicGroupDataModel;
import java.io.File;
import java.sql.SQLException;
import org.simpleframework.xml.Element;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import presage.Plugin;
import presage.Simulation;
import presage.annotations.PluginConstructor;

/**
 * Updated version of DatabasePlugin that is more more more more... just better.
 * @author Valdas
 */
public class NewDatabasePlugin implements Plugin
{
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger("gameoflife.DatabasePlugin");
	private final static String name = "Database v2";
	
	@Element
	private final Boolean remote;
	@Element
	private final String comment;
	
	/**
	 * Creates a new instance of the DatabasePlugin,
	 * stores data in local db.
	 * 
	 */
	NewDatabasePlugin()
	{
	  //default parameters: local database
	  this("No comment",false);
	}
	
	 /**
	 * Creates a new instance of the DatabasePlugin
	 * 
	 * @param comment Comment for simulation
	 * @param remote Use remote db instead of local
	 * 
	 */
	@PluginConstructor(
	{
		"comment","remote"
	})
	public NewDatabasePlugin(String comment,Boolean remote)
	{
	    this.remote = remote;
	    this.comment = comment;
	}
	

	private ConnectionWrapper wrap;
	private PublicEnvironmentConnection envConn;
	private final TreeMap<String, PublicGroupDataModel> trackedGroups 
					= new TreeMap<String, PublicGroupDataModel>();

	@Override
	public void execute()
	{
	    pruneOldGroups();
	    findNewGroups();
	    if(envConn.getRoundsPassed()%15==0) wrap.flush();
	}

	@Override
	public void initialise(Simulation sim)
	{
		try {
		    String url;
		    if(remote) {
			//url to remote db
			url = "jdbc:mysql://69.175.26.66:3306/stratra1_isegol?user=stratra1_isegol&password=ise4r3g00d";
			logger.log(Level.INFO,"Using remote database");
		    }
		    else {
			//path to /Simulations folder
			String configPath = new File(System.getProperty("user.dir"), "simulations").getAbsolutePath();
			//url to local db
			url = "jdbc:sqlite:"+ configPath + "/Simulations.db";
			logger.log(Level.INFO,"Using local database");
		    }
		    wrap = new ConnectionWrapper(url,comment,remote);

		}
		catch (SQLException ex)
		{
			logger.log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			logger.log(Level.SEVERE,"SQLite JDBC class not found", ex);
		}
		envConn = PublicEnvironmentConnection.getInstance();
	}

	@Override
	public void onDelete()
	{
		wrap.flush();
		wrap.end();
	}

	@Override
	public void onSimulationComplete()
	{
		wrap.flush();
		wrap.end();
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
		// get all active groups in simulation
		TreeSet<String> newGroups = new TreeSet<String>(envConn.availableGroups());
		//remove already tracked groups
		newGroups.removeAll(trackedGroups.keySet());

		for (String g : newGroups)
		{
			//queue group addition sql statement
			wrap.groupAdd(g);
			//add the group to tracked groups
			trackedGroups.put(g, envConn.getGroupById(g));
		}
	}

	
	private void pruneOldGroups()
	{
		//Stop tracking old groups
		TreeSet<String> oldGroups = new TreeSet<String>(trackedGroups.keySet());
		oldGroups.removeAll(envConn.availableGroups());

		for (String g : oldGroups)
		{
			//queue group death sql statement
			wrap.groupDie(g);
			trackedGroups.remove(g);
		}
	}
}
