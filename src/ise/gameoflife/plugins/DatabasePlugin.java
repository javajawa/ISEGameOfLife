package ise.gameoflife.plugins;

import ise.gameoflife.environment.Environment;
import ise.gameoflife.tokens.TurnType;
import org.simpleframework.xml.Element;
import java.util.SortedSet;
import java.io.File;
//import com.google.common.io.Files;
import presage.Plugin;
import presage.Simulation;
import presage.annotations.PluginConstructor;

import java.sql.*;

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
    private int remote_simId;
    private Statement stat;

    
    private PreparedStatement prep;
    private PreparedStatement prep2;
    //database connection
    private Connection conn;
    //Remote MySQL server
    private Connection rcon;

    @Element
    private int local_simId;
    @Element
    private String sim_comment;
    @Element
    private Boolean saveToRemote = false;
     // private JPanel control = new JPanel();


 
    @Deprecated
    public DatabasePlugin()
    {
	
    }

    /**
     * Creates a new instance of the DatabasePlugin
     * @param userId - UID on remote database
     */
    @PluginConstructor(
    {
	    "simId","sim_comment","saveToRemote"
    })
    public DatabasePlugin(int simId, String sim_comment, Boolean saveToRemote)
    {
	    super();
	    local_simId = simId;
	    this.sim_comment = sim_comment;
	    this.saveToRemote = saveToRemote;
	    
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
		
		//prep.setInt(1,local_simId);
		prep.setLong(1, sim.getTime());
		prep.setInt(2,en.getCyclesPassed());
		prep.setInt(3,getNumHunters());
		prep.addBatch();
		if (saveToRemote) {
		    //prep2.setInt(1,remote_simId);
		    prep2.setLong(1, sim.getTime());
		    prep2.setInt(2,en.getCyclesPassed());
		    prep2.setInt(3,getNumHunters());
		    prep2.addBatch();
		}
		//sends data to DB every 100 cycles
		if (sim.getTime()%100 == 0) {
		    prep.executeBatch();
		    if (saveToRemote) prep2.executeBatch();
		    if (sim.getTime()%1000 == 0) {
			conn.commit();
		    }
	        }

	}
	catch (SQLException e) {
	    e.printStackTrace();
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
	    String configPath = new File(System.getProperty("user.dir"), "simulations").getAbsolutePath();

	    
	    conn = DriverManager.getConnection("jdbc:sqlite:"+ configPath + "/Simulations.db");
	    stat = conn.createStatement();
	    stat.executeUpdate("create table if not exists [simulations]("
		    + "[simId] not null,\n"
		    + "[uid] ,\n"
		    + "[comment] TEXT,\n"
		    + "[timestmp] TIME,\n"
		    + "[done] not null,\n"
		    + "CONSTRAINT [] PRIMARY KEY ([simId]));\n");
	    stat.executeUpdate("CREATE TRIGGER if not exists insert_simulations_timestmp "
		    + "AFTER INSERT ON [simulations]"
		    + "BEGIN"
		    + "  UPDATE [simulations] SET timestmp = TIME('NOW')"
		    + "  WHERE rowid = new.rowid; END;");
	    
	    stat.executeUpdate("CREATE TABLE if not exists [data] (\n"
		    + "[simId] NOT NULL REFERENCES [simulations]([simId])"
			+ " ON DELETE CASCADE ON UPDATE CASCADE MATCH FULL DEFERRABLE INITIALLY IMMEDIATE,\n"
		    + "[cycle]  NOT NULL,\n"
		    + "[turn]  NOT NULL,\n"
		    + "[pop]  NOT NULL,\n"
		    + "CONSTRAINT [] PRIMARY KEY ([simId],[cycle])"
		    + ");\n"
		    );
	    stat.executeUpdate("REPLACE INTO [simulations]" +                             
		   "VALUES ('"+local_simId+"','"+System.getProperty("user.name")+"',"
		    + "'"+sim_comment+"',null,0)");
	    stat.close();
	    conn.setAutoCommit(false);
	    prep = conn.prepareStatement(
		"REPLACE into data values ('"+local_simId+"', ?, ?, ? );");
	    
	    if (saveToRemote) {
		rcon = DriverManager.getConnection("jdbc:mysql://69.175.26.66:3306/stratra1_isegol",
			    "stratra1_isegol","ise4r3g00d");
		stat = rcon.createStatement();
		stat.executeUpdate("INSERT INTO simulations " +                             
		   "VALUES (null,'"+System.getProperty("user.name")+"','"+sim_comment+"',null,0);",      
		   Statement.RETURN_GENERATED_KEYS);   // Indicate you want automatically 
						       // generated keys
		ResultSet rs = stat.getGeneratedKeys();         // Retrieve the automatically       
						       // generated key value in a ResultSet.
						       // Only one row is returned.
					 // Create ResultSet for query
		while (rs.next()) {
		    remote_simId  = rs.getInt(1);     // Get automatically generated key 
						       // value
		    System.out.println("Remote DB simId = " + remote_simId);
		}
		rs.close();                           // Close ResultSet
		stat.close();                         // Close Statement
		prep2 = rcon.prepareStatement(
		"insert into data values ('"+remote_simId+"', ?, ?, ? );");
	    }


	}
	catch (SQLException e) {
	    e.printStackTrace();
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
	    conn.commit();
	    prep.close();
	    stat = conn.createStatement();
	    stat.executeUpdate("UPDATE simulations\n"
		    + "SET done='1'\n"
		    + "WHERE simId='"+local_simId+"';");
	    conn.commit();
	    stat.close();
	    if (saveToRemote) {
		prep2.executeBatch();
		prep2.close();
		stat = rcon.createStatement();
		stat.executeUpdate("UPDATE simulations\n"
		    + "SET done=1\n"
		    + "WHERE simId='"+remote_simId+"';");
		rcon.close();
	    }
	    //commits all transactions
	    conn.close();
	}
	catch (SQLException e) {
	    e.printStackTrace();
	    return;
	}
    }
	
}


