package ise.mace.plugins;

import ise.mace.environment.Environment;
import ise.mace.environment.PublicEnvironmentConnection;
import ise.mace.tokens.TurnType;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.SortedSet;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.simpleframework.xml.Element;
import presage.Plugin;
import presage.Simulation;
import presage.annotations.PluginConstructor;

/**
 * Simple graph plugin, based on the org.jfree.chart libraries, that plots a
 * line graph of population vs simulation time. The graph is save to a file on
 * the completion of the simulation
 */
public final class HuntersAlivePlugin extends JPanel implements Plugin
{
	private static final long serialVersionUID = 1L;
	private final static String title = "Population";
	private final static String xaxis = "Simulation turn";
	private final static String yaxis = "Population";
	private final static String label = "Population";
	private Simulation sim;
	private Environment en;
	@Element(required = false)
	private String outputpath;
	@Element
	private int outputwidth;
	@Element
	private int outputheight;
	private final XYSeriesCollection data = new XYSeriesCollection();
	;
	private JPanel control = new JPanel();
	private ChartPanel chartPanel;

	/**
	 * SimpleXML no-arg Constructor. Do not use this constructor, it is only for
	 * the purpose of SimpleXML being able to create the object through inflection
	 * @deprecated Serialisation constructor
	 */
	@Deprecated
	public HuntersAlivePlugin()
	{
		// Nothing to see here. Move along, citizen.
	}

	/**
	 * Creates a new instance of the HuntersAlivePlugin
	 * @param outputpath Path to write the final image to
	 * @param outputwidth Width of the outputted image
	 * @param outputheight Height of the outputted image
	 */
	@PluginConstructor(
	{
		"outputpath", "outputwidth", "outputheight"
	})
	public HuntersAlivePlugin(String outputpath, int outputwidth,
					int outputheight)
	{
		super();
		if (outputpath != null)
		{
			if (outputwidth <= 0 || outputheight <= 0)
				throw new IllegalArgumentException(
								"Height and Width must both be greater than 0");
			this.outputpath = outputpath;
			this.outputwidth = outputwidth;
			this.outputheight = outputheight;
		}
	}

	private double getNumHunters()
	{
		SortedSet<String> participantIdSet = sim.getactiveParticipantIdSet("hunter");
		PublicEnvironmentConnection ec = PublicEnvironmentConnection.getInstance();
                String SpecialID = "";
                for(String group : ec.getGroups())
                   if(ec.getGroupById(group).getName().equals("Group #2"))
                       SpecialID = group;   
                if(SpecialID.equals(""))
                    return participantIdSet.size();
                else
                    return participantIdSet.size() - ec.getGroupById(SpecialID).size();
	}

	/**
	 * Run per-step-in-simulation code that changes the plugin's state. In this
	 * case, we add a new data point to our series, and then update the char view
	 */
	@Override
	public void execute()
	{
		if (en.getCurrentTurnType() != TurnType.firstTurn) return;
		// Make sure the chart isn't currently being updated
		synchronized (data)
		{
			try
			{
				data.getSeries("population").add(en.getRoundsPassed(), getNumHunters());

			}
			catch (org.jfree.data.UnknownKeyException e)
			{
				XYSeries series = new XYSeries("population");
				data.addSeries(series);
				series.add(en.getRoundsPassed(), getNumHunters());
			}
			updateChart();
		}
	}

	private void updateChart()
	{
		// Make sure the data set isn't currently being updated
		synchronized (data)
		{
			chartPanel.updateUI();
		}
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
		System.out.println(" -Initialising....");

		this.sim = sim;
		this.en = (Environment)sim.environment;

		setBackground(Color.GRAY);

		synchronized (data)
		{
			JFreeChart chart = ChartFactory.createXYLineChart(title, xaxis, yaxis,
							data, PlotOrientation.VERTICAL, true, true, false);

			chartPanel = new ChartPanel(chart);
			chartPanel.setPreferredSize(new Dimension(getWidth(), getHeight()));
		}

		//JLabel label = new JLabel("Graph will update every " + updaterate
		//		+ " Simulation cycles, to update now click: ");

		JButton updateButton = new JButton("Update Graph");

		updateButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				updateChart();
			}
		});

		//control.add(label);
		control.add(updateButton);

		this.setLayout(new BorderLayout());
		add(control, BorderLayout.NORTH);
		add(chartPanel, BorderLayout.CENTER);
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

		// Firstly, remove the update button, and (forcibly) refresh the chart
		this.remove(control);
		this.updateChart();

		// Output the final chart to file
		if (outputpath == null) return;
		try
		{
			File file = new File(outputpath);
			synchronized (data)
			{
				ChartUtilities.saveChartAsPNG(file, chartPanel.getChart(), outputwidth,
								outputheight);
			}
		}
		catch (Throwable e)
		{
			System.err.println("Erorr outputting '" + label + "' Graph: " + e);
		}
	}
}
