package ise.gameoflife.plugins;

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
 *
 * @author Harry Eakins
 */
public final class HuntersAlivePlugin extends JPanel implements Plugin
{

	private static final long serialVersionUID = 1L;

	private final static String title = "Population";
	private final static String xaxis = "Simulation cycle";
	private final static String yaxis = "Population";
	private final static String label = "Population";

	private Simulation sim;

	@Element
	private String outputpath;
	@Element
	private int outputwidth;
	@Element
	private int outputheight;

	private final XYSeriesCollection data = new XYSeriesCollection();;
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
		this.outputpath = outputpath;
		this.outputwidth = outputwidth;
		this.outputheight = outputheight;
	}

	private double getNumHunters()
	{
		SortedSet<String> participantIdSet = sim.getactiveParticipantIdSet("hunter");
		return participantIdSet.size();
	}

	@Override
	public void execute()
	{
		// Make sure the chart isn't currently being updated
		synchronized (data)
		{
			try
			{
				data.getSeries("population").add(sim.getTime(), getNumHunters());
			}
			catch (org.jfree.data.UnknownKeyException e)
			{
				XYSeries series = new XYSeries("population");
				data.addSeries(series);
				series.add(sim.getTime(), getNumHunters());
			}
			updateChart();
		}
	}

	public void updateChart()
	{
		// Make sure the data set isn't currently being updated
		synchronized (data)
		{
			chartPanel.updateUI();
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
		System.out.println(" -Initialising....");

		this.sim = sim;

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

	@Override
	public void onDelete()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onSimulationComplete()
	{

		this.removeAll();

		synchronized (data)
		{
			JFreeChart chart = ChartFactory.createXYLineChart(title, xaxis, yaxis,
						data, PlotOrientation.VERTICAL, true, true, false);

			chartPanel = new ChartPanel(chart);
			chartPanel.setPreferredSize(new Dimension(outputwidth, outputheight));

			chartPanel.updateUI();
		}

		this.setLayout(new BorderLayout());
		add(chartPanel, BorderLayout.CENTER);

		File file;

		try
		{
			file = new File(outputpath);
		}
		catch (Exception e)
		{
			System.err.println("Graph Plugin " + label
							+ " : Invalid output path" + e);
			return;
		}

		try
		{
			ChartUtilities.saveChartAsPNG(file, chartPanel.getChart(),
							this.outputwidth,
							this.outputheight);
		}
		catch (Exception e)
		{
			System.out.println("Problem occurred creating chart " + label + ": " + e.getMessage());
		}

	}

}
