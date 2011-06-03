/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ise.gameoflife.plugins;

import ise.gameoflife.environment.Environment;
import ise.gameoflife.participants.SimplePoliticalParticipant;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeMap;
import javax.swing.JPanel;
import org.simpleframework.xml.Element;
import presage.EnvDataModel;
import presage.Plugin;
import presage.Simulation;
import presage.annotations.PluginConstructor;

/**
 * Draws a representation of the agents and groups on the political
 * compass, using information from the simulation and its history.
 *
 * @author Harry Eakins
 */
public class PoliticalCompassPlugin extends JPanel implements Plugin {

	private static final long serialVersionUID = 1L;

	private final static String title = "Political Compass";
        private final static String label = "Political Compass";

	private Simulation sim;
	private Environment en;
        private EnvDataModel dmodel;

        // Set of political participants that are active
        private TreeMap<String, SimplePoliticalParticipant> p_players = new TreeMap<String, SimplePoliticalParticipant>();

	@Element(required=false)
	private String outputpath;


	/**
	 * SimpleXML no-arg Constructor. Do not use this constructor, it is only for
	 * the purpose of SimpleXML being able to create the object through inflection
	 * @deprecated Serialisation constructor
	 */
	@Deprecated
	public PoliticalCompassPlugin()
	{
		// Nothing to see here. Move along, citizen.
	}

	/**
	 * Creates a new instance of the HuntersAlivePlugin
	 * @param outputpath Path to write the final video to
	 */
	@PluginConstructor(
	{
		"outputpath"
	})
	public PoliticalCompassPlugin(String outputpath)
	{
		super();
		this.outputpath = outputpath;
	}

	/**
	 * Run per-step-in-simulation code that changes the plugin's state. In this
	 * case, we use the information from the simulation's last step to update
         * the the political view data of the agents.
	 */
	@Override
	public void execute()
	{
                // Add/remove new/old players
                updatePoliticalPlayers();

                // Calculate new political positions
                Random randomGenerator = new Random();
                for(Map.Entry<String, SimplePoliticalParticipant> entry : p_players.entrySet())
                {
                        SimplePoliticalParticipant pp = entry.getValue();

                        // TODO actually implement the measurement of political position
                        pp.economic += (randomGenerator.nextFloat() - 0.5)/10;
                        pp.social += (randomGenerator.nextFloat() - 0.5)/10;
                }

                repaint();
	}

        private void updatePoliticalPlayers()
        {

                SortedSet<String> active_agent_ids = sim.getactiveParticipantIdSet("hunter");
                Iterator<String> iter = active_agent_ids.iterator();

                // Add any new agents
                while(iter.hasNext())
                {
                        String id = iter.next();
                        if(!p_players.containsKey(id))
                        {
                                p_players.put(id, new SimplePoliticalParticipant());
                        }
                }

                // Delete agents which are no longer active
                List<String> ids_to_remove = new LinkedList<String>();
                for(Map.Entry<String, SimplePoliticalParticipant> entry : p_players.entrySet())
                {
                        String id = entry.getKey();
                        if(!active_agent_ids.contains(id))
                        {
                                ids_to_remove.add(id);
                        }
                }
                iter = ids_to_remove.iterator();
                while(iter.hasNext())
                {
                        p_players.remove(iter.next());
                }
        }

        @Override
        public void paint(Graphics g)
        {
                // Clear everything
                g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw social and economic axis
                Rectangle rect = g.getClipBounds();
                g.setColor(Color.YELLOW);
                g.drawLine(rect.width/2, 0, rect.width/2, rect.height);
                g.drawLine(0,  rect.height/2, rect.width, rect.height/2);

                // Draw agents
                for(Map.Entry<String, SimplePoliticalParticipant> entry : p_players.entrySet())
                {
                        drawAgent(g, entry.getValue());
                }
        }

        private void drawAgent(Graphics g, SimplePoliticalParticipant p_player)
        {
                Rectangle rect = g.getClipBounds();
                g.setColor(Color.BLUE);
                g.fillOval((int)((p_player.economic+1)*rect.width/2),
                            (int)((p_player.social+1)*rect.height/2),
                            10, 10
                            );

        }

	/**
	 * Returns the label of this plugin
	 * @return The label of this plugin
	 */
	@Override
	public String getLabel()
	{
		return label;
	}

	/**
	 * Returns the short label of this plugin
	 * @return The short label of this plugin
	 */
	@Override
	public String getShortLabel()
	{
		return label;
	}

	/**
	 * Initialises a plugin that was stored using the SimpleXML framework, making
	 * it ready to be used in the visualisation of a simulation
	 * @param sim The simulation to which this plugin will belong
	 */
	@Override
	public void initialise(Simulation sim)
	{
		System.out.println(" -Initialising Political Compass Plugin....");

		this.sim = sim;

		setBackground(Color.GRAY);

                repaint();
	}

	/**
	 * Is not used by simulation
	 * @deprecated Not used by any calling class
	 */
	@Deprecated
	@Override
	public void onDelete()
	{
		// Nothing to see here. Move along, citizen!
	}

	/**
	 * Preforms actions when a simulation has run to completion, such as
	 * outputting the graph to a file for later viewing
	 */
	@Override
	public void onSimulationComplete()
	{


	}

}
