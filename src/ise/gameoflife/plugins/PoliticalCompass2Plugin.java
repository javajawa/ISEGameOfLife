/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.gameoflife.plugins;

import ise.gameoflife.agents.TestPoliticalAgent;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;

import ise.gameoflife.environment.Environment;
import ise.gameoflife.participants.SimplePoliticalParticipant;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeMap;
import javax.imageio.ImageIO;
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
 * @author The0s
 */
public class PoliticalCompass2Plugin extends JPanel implements Plugin{

	private static final long serialVersionUID = 1L;

	private final static String title = "Political Compass2";
        private final static String label = "Political Compass2";


	private Simulation sim;
	private Environment en;
        private EnvDataModel dmodel;

        // Set of political participants that are active
        private TreeMap<String, TestPoliticalAgent> p_players = new TreeMap<String, TestPoliticalAgent>();

	@Element(required=false)
	private String outputdirectory;

        private int framecount = 0;

	/**
	 */
	public PoliticalCompass2Plugin()
	{
		this.outputdirectory = null;
	}

	/**
	 * Creates a new instance of the PoliticalCompassPlugin
	 * @param outputpath Path to write the final video to
	 */
	@PluginConstructor(
	{
		"outputdirectory"
	})
	public PoliticalCompass2Plugin(String outputdirectory)
	{
		super();
		this.outputdirectory = outputdirectory;
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
               // for(Map.Entry<String, TestPoliticalAgent> entry : p_players.entrySet())
               // {
               //        TestPoliticalAgent p_player = entry.getValue();
               //        System.out.println(p_player.getDataModel().getEconomicBelief()+ "hello" + p_player.getDataModel().getSocialBelief());
                      //   TODO actually implement the measurement of political position
                      //  pp.getDataModel().getEconomicBelief() +=  //(randomGenerator.nextFloat() - 0.5)/10;
                      //  pp.social += //(randomGenerator.nextFloat() - 0.5)/10;
               // }
                
                repaint();

                if(this.outputdirectory != null)
                {
                        writeToPNG();
                }
	}

        private void writeToPNG() {
                BufferedImage bi = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB);
                Graphics big = bi.getGraphics();
                big.setClip(0, 0, 500, 500);
                this.paint(big);
                try
                {
                        File f =  new File(this.outputdirectory + "test"+this.framecount+".png");
                        f.mkdirs();
                        ImageIO.write(bi, "png",f);
                        this.framecount++;
                }
                catch (Exception e)
                {
                        System.out.println("Error writing political compass image: " + this.framecount);
                }
        }

        /**
         * Adds new players and removes dead players since the last cycle.
         */
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
                                p_players.put(id, (TestPoliticalAgent) sim.getPlayer(id));
                        }
                }

                // Delete agents which are no longer active
                List<String> ids_to_remove = new LinkedList<String>();
                for(Map.Entry<String, TestPoliticalAgent> entry : p_players.entrySet())
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

        /**
         * Draw everything to the screen
         * @param g Graphics object
         */
        @Override
        public void paint(Graphics g)
        {
                // Clear everything
                g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());

                // Draw social and economic axis
                Rectangle rect = g.getClipBounds();
                g.setColor(Color.DARK_GRAY);
                g.drawLine(rect.width/2, 0, rect.width/2, rect.height);
                g.drawLine(0,  rect.height/2, rect.width, rect.height/2);

                // Draw agents
                for(Map.Entry<String,TestPoliticalAgent> entry : p_players.entrySet())
                {
                        drawAgent(g, entry.getValue());
                }

        }

        /**
         * Draws a circle representing an agent's political views
         * @param g Graphics objects
         * @param p_player SimplifiedPoliticalPlayer object to draw
         */
        private void drawAgent(Graphics g, TestPoliticalAgent p_player)
        {
                Rectangle rect = g.getClipBounds();
                g.setColor(Color.BLUE);
                g.fillOval((int)((p_player.getDataModel().getEconomicBelief())*rect.width/2),
                            (int)((p_player.getDataModel().getSocialBelief())*rect.height/2),
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

		setBackground(Color.CYAN);

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
