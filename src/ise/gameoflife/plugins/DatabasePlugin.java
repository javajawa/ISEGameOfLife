package ise.gameoflife.plugins;

import ise.gameoflife.environment.Environment;
import ise.gameoflife.tokens.TurnType;
import org.simpleframework.xml.Element;
import java.util.SortedSet;
import presage.Plugin;
import presage.Simulation;
import presage.annotations.PluginConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
/**
 *
 * @author valdas
 * Creates an SQLite database in appropriate simulation folder. Sends data to DB
 * every 100 cycles and commits every 1000 and at the end, thus reducing writes to disk.
 */
public class DatabasePlugin implements Plugin {

    private static final long serialVersionUID = 1L;

    private final static String title = "DatabasePlugin";
    private final static String label = "DatabasePlugin";

    private Simulation sim;
    private Environment en;
    private int simId; 
    
    private PreparedStatement prep;
    private PreparedStatement prep2;
    //database connection
    private Connection conn;
    //Remote MySQL server
    private Connection rcon;

    @Element
    private String outputpath;

   // private JPanel control = new JPanel();


 
    @Deprecated
    public DatabasePlugin()
    {
	    // Nothing to see here. Move along, citizen.
    }

    /**
     * Creates a new instance of the DatabasePlugin
     * @param outputpath Path to SQLite database file
     */
    @PluginConstructor(
    {
	    "outputpath"
    })
    public DatabasePlugin(String outputpath)
    {
	    super();
	    this.outputpath = outputpath;
    }

    private int getNumHunters()
    {
	    SortedSet<String> participantIdSet = sim.getactiveParticipantIdSet("hunter");
	    return participantIdSet.size();
    }

    @Override
    public void execute()
    {
	//people only updated at the beginning of turn
	if (en.getCurrentTurnType() != TurnType.firstTurn) return;
	try {

		prep.setLong(1, sim.getTime());
		prep.setInt(2,en.getCyclesPassed());
		prep.setInt(3,getNumHunters());
		prep.addBatch();
		prep2.setLong(1, sim.getTime());
		prep2.setInt(2,en.getCyclesPassed());
		prep2.setInt(3,getNumHunters());
		prep2.addBatch();
		//sends data to DB every 100 cycles
		if (sim.getTime()%100 == 0) {
		    prep.executeBatch();
		    prep2.executeBatch();
		    if (sim.getTime()%1000 == 0) {
			conn.commit();
		    }
	        }

	}
	catch (Exception e)
	{
		System.err.println("Database Exception:" + e);
		return;
	}
	
    }

 
    @Override
    public String getLabel()
    {
	    return label;
    }

    @Override
    public String getShortLabel()
    {
	    return label;
    }

    @Override
    public void initialise(Simulation sim)
    {
	this.sim = sim;
	this.en = (Environment)sim.environment;
	try {    
	    Class.forName("org.sqlite.JDBC");
	}
	catch (Exception e)
	    {
		    System.err.println("Database library not available:" + e);
	    }
	
	try {
	    conn = DriverManager.getConnection("jdbc:sqlite:"+outputpath);
	    rcon = DriverManager.getConnection("jdbc:mysql://69.175.26.66:3306/stratra1_isegol",
			"stratra1_isegol","ise4r3g00d");
	    Statement stat = conn.createStatement();
	    stat.executeUpdate("drop table if exists population;");
	    stat.executeUpdate("CREATE TABLE [population] (\n"
		    + "[cycle]  NOT NULL,\n"
		    + "[turn]  NOT NULL,\n"
		    + "[pop]  NOT NULL,\n"
		    + "CONSTRAINT [] PRIMARY KEY ([cycle]));\n"
		    );
	    stat.close();
	    stat = rcon.createStatement();
	    stat.executeUpdate("INSERT INTO simulations " +                             
	       "VALUES (null,12345,'Comment1',null,0)",        // Insert a row
	       Statement.RETURN_GENERATED_KEYS);   // Indicate you want automatically 
	                                           // generated keys
	    ResultSet rs = stat.getGeneratedKeys();         // Retrieve the automatically       
	                                           // generated key value in a ResultSet.
	                                           // Only one row is returned.
                                     // Create ResultSet for query
	    while (rs.next()) {
	      simId  = rs.getInt(1);     // Get automatically generated key 
	                                           // value
	       System.out.println("automatically generated key value = " + simId);
	    }
	    rs.close();                           // Close ResultSet
	    stat.close();                         // Close Statement 
	    
	    conn.setAutoCommit(false);
	    prep = conn.prepareStatement(
		"insert into population values (?, ?, ?);");
	    prep2 = rcon.prepareStatement(
		"insert into data values ("+simId+", ?, ?,?);");

	}
	catch (Exception e)
	    {
		    System.err.println("Database Exception:" + e);
		    return;
	    }


	//JLabel label = new JLabel("Graph will update every " + updaterate
	//		+ " Simulation cycles, to update now click: ");

	//JButton updateButton = new JButton("Update database");
/*
	updateButton.addActionListener(new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent ae)
		{
			
		try {
		    prep.executeBatch();
		} catch (Exception e)
		    {
			    System.err.println("Database Exception:" + e);
			    return;
		    }
		}

	});
*/
	//control.add(updateButton);
	//this.setLayout(new BorderLayout());
	//add(control, BorderLayout.NORTH);

    }

		@Deprecated
    @Override
    public void onDelete()
    {
	    // Nothing to see here. Move along, citizen!
    }

    @Override
    public void onSimulationComplete()
    {
//	this.removeAll();
	try {
	    //sends left over data to DB
	    prep.executeBatch();
	    prep2.executeBatch();
	    //commits all transactions
	    conn.commit();
	    prep.close();
	    conn.close();
	} catch (Exception e)
	    {
		    System.err.println("Database Exception:" + e);
		    return;
	    }
    }
	
}


