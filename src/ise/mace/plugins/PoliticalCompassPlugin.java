package ise.mace.plugins;

import ise.mace.environment.PublicEnvironmentConnection;
import ise.mace.groups.TestPoliticalGroup;
import ise.mace.participants.PublicGroupDataModel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.simpleframework.xml.Element;
import presage.Plugin;
import presage.Simulation;
import presage.annotations.PluginConstructor;

/**
 * Draws a representation of the agents and groups on the political
 * compass, using information from the simulation and its history.
 *
 * @author Harry Eakins
 */
public class PoliticalCompassPlugin extends JPanel implements Plugin
{

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger("gameofline.Plugins.GroupCompass");

	private final static String label = "Political Compass(groups)";

	@Element(required = false)
	private String outputdirectory;
	private int framecount = 0;

	/**
	 */
	public PoliticalCompassPlugin()
	{
		this.outputdirectory = null; 
	}

	/**
	 * Creates a new instance of the PoliticalCompassPlugin
	 * @param outputdirectory Path to write the final video to
	 */
	@PluginConstructor({"outputdirectory"})
	public PoliticalCompassPlugin(String outputdirectory)
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

		repaint();

		if (this.outputdirectory != null)
		{
			writeToPNG();
		}
	}

	private void writeToPNG()
	{
		BufferedImage bi = new BufferedImage(this.getSize().width,
						this.getSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics big = bi.getGraphics();
		big.setClip(0, 0, this.getSize().width, this.getSize().height);
		this.paint(big);
		try
		{
			File f = new File(this.outputdirectory + "test" + this.framecount + ".png");
			f.mkdirs();
			ImageIO.write(bi, "png", f);
			this.framecount++;
		}
		catch (Exception e)
		{
			System.out.println("Error writing political compass image: " + this.framecount);
		}
	}

	/**
	 * Draw everything to the screen
	 * @param g Graphics object
	 */
	@Override
	public void paint(Graphics g)
	{
		PublicEnvironmentConnection ec = PublicEnvironmentConnection.getInstance();

		// Clear everything
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());

		// Draw social and economic axis
		Rectangle rect = g.getClipBounds();
		g.setColor(Color.YELLOW);
		g.drawLine(rect.width / 2, 0, rect.width / 2, rect.height);
		g.drawLine(0, rect.height / 2, rect.width, rect.height / 2);

		// Draw agents
		for (String group : PublicEnvironmentConnection.getInstance().availableGroups())
		{
			PublicGroupDataModel dm = ec.getGroupById(group);
			double size = 2 * Math.sqrt((double)dm.size());
			logger.log(Level.INFO, "{0} [{1}] '{'{2},{3},{4}'}'", new Object[]{group,
							dm.getName(), dm.getCurrentEconomicPoisition(),
							dm.getEstimatedSocialLocation(), dm.size()});
			drawAgent(g, dm.getEstimatedSocialLocation(), dm.getCurrentEconomicPoisition(), (int)size, dm.getName());
		}

	}

	/**
	 * Draws a circle representing an agent's political views
	 * @param g Graphics objects
	 * @param p_player SimplifiedPoliticalPlayer object to draw
	 */
	private void drawAgent(Graphics g, double social, double economic, int size, String name)
	{
		Rectangle rect = g.getClipBounds();
		int c_x = (int)(economic * rect.width);
		int c_y = (int)(social * rect.height);

		g.setColor(new Color(0.1F, 0.1F, 0.9F, 0.5F));
		g.fillOval(c_x - size, c_y - size, 2 * size, 2 * size);
		g.drawString(name + '[' + size + ']', c_x, c_y);
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
