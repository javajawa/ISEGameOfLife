package ise.gameoflife.plugins.database;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
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

	@Override
	public void execute()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void initialise(Simulation sim)
	{
		try
		{
			wrap = new ConnectionWrapper(DB_URI);
		}
		catch (SQLException ex)
		{
			logger.log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void onDelete()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onSimulationComplete()
	{
		throw new UnsupportedOperationException("Not supported yet.");
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
}
