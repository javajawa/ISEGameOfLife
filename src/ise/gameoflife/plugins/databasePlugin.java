/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ise.gameoflife.plugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.SortedSet;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.simpleframework.xml.Element;
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
 */
public class databasePlugin extends JPanel implements Plugin {

    //private static final long serialVersionUID = 1L;

    private final static String title = "DatabasePlugin";
    private final static String label = "DatabasePlugin";

    private Simulation sim;
    private PreparedStatement prep;
    //database connection
    private Connection conn;

    @Element
    private String outputpath;

    private JPanel control = new JPanel();


 
    @Deprecated
    public databasePlugin()
    {
	    // Nothing to see here. Move along, citizen.
    }

    /**
     * Creates a new instance of the databasePlugin
     * @param outputpath Path to SQLite database file
     */
    @PluginConstructor(
    {
	    "outputpath"
    })
    public databasePlugin(String outputpath)
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
	try {
	    prep.setLong(1, sim.getTime());
	    prep.setInt(2,getNumHunters());		
	    prep.executeUpdate();
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
	    // TODO Auto-generated method stub
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
	setBackground(Color.GRAY);
	try {    
	    Class.forName("org.sqlite.JDBC");
	}
	catch (Exception e)
	    {
		    System.err.println("Database library not available:" + e);
	    }
	
	try {
	    conn = DriverManager.getConnection("jdbc:sqlite:"+outputpath);
	    Statement stat = conn.createStatement();
	    stat.executeUpdate("drop table if exists population;");
	    stat.executeUpdate("CREATE TABLE [population] (\n"
		    + "[time]  NOT NULL,\n"
		    + "[pop]  NOT NULL,\n"
		    + "CONSTRAINT [] PRIMARY KEY ([time]));\n"
		    );
	    prep = conn.prepareStatement(
		"insert into population values (?, ?);");

	}
	catch (Exception e)
	    {
		    System.err.println("Database Exception:" + e);
		    return;
	    }


	//JLabel label = new JLabel("Graph will update every " + updaterate
	//		+ " Simulation cycles, to update now click: ");

	JButton updateButton = new JButton("Look nice");

	updateButton.addActionListener(new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent ae)
		{
			//action for button
		}

	});

	control.add(updateButton);
	this.setLayout(new BorderLayout());
	add(control, BorderLayout.NORTH);

    }

    @Override
    public void onDelete()
    {
	    // TODO Auto-generated method stub
    }

    @Override
    public void onSimulationComplete()
    {
	this.removeAll();
	try {
	    Statement stat = conn.createStatement();
	    ResultSet rs = stat.executeQuery("select * from people;");
		while (rs.next()) {
		System.out.println("name = " + rs.getString("name"));
		System.out.println("job = " + rs.getString("occupation"));
	    }
	    rs.close();
	    conn.close();
	} catch (Exception e)
	    {
		    System.err.println("Database Exception:" + e);
		    return;
	    }
    }
	
}


