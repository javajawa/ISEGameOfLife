package ise.gameoflife.plugins;

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.util.SortedSet;
import java.io.File;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.simpleframework.xml.Element;

import presage.Plugin;
import presage.Simulation;
import presage.annotations.PluginConstructor;
import presage.util.ObjectCloner;

import ise.gameoflife.enviroment.EnvironmentDataModel;

/**
 *
 * @author harry
 */
public class LineChartPlugin extends JPanel implements Plugin
{

	private static final long serialVersionUID = 1L;

	private final static String title = "Population";
	private final static String xaxis = "Simulation cycle";
	private final static String yaxis = "Num People";
	private final static String label = "Num People";

	private Simulation sim;
	private EnvironmentDataModel dm;

	@Element
	private String outputpath;
	@Element
	private int outputwidth;
	@Element
	private int outputheight;

	private XYSeriesCollection data;
	private XYSeriesCollection datatempcopy;
	private JPanel control = new JPanel();
	private ChartPanel chartPanel;

	/**
	 * SimpleXML no-arg Constructor. Do not use this constructor, it is only for
	 * the purpose of SimpleXML being able to create the object through inflection
	 * @deprecated Serialisation constructor
	 */
	@Deprecated
	public LineChartPlugin()
	{
		// Nothing to see here. Move along, citizen.
	}

	/**
	 * Creates a new instance of the LineChartPlugin
	 * @param outputpath Path to write the final image to
	 * @param outputwidth Width of the outputted image
	 * @param outputheight Height of the outputted image
	 */
	@PluginConstructor(
	{
		"outputpath", "outputwidth", "outputheight"
	})
	public LineChartPlugin(String outputpath, int outputwidth,
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

		// simply get the environment datamodel
		dm = (EnvironmentDataModel)sim.getEnvDataModel();

		double population = getNumHunters();

		synchronized (this)
		{

			XYSeries series;
			try
			{
				series = data.getSeries("population");
				series.add(dm.time, population);
			}
			catch (org.jfree.data.UnknownKeyException e)
			{
				series = new XYSeries("population");
				data.addSeries(series);
				series.add(dm.time, population);
			}

			//if (dmodel.time % 100 == 0) {
			// this will cause the gui to update?
			updateChart();
			//}
		}
	}

	public void updateChart()
	{

		this.remove(chartPanel);

		synchronized (this)
		{
			datatempcopy = clonedata(data);
		}

		chartPanel = newChart(datatempcopy);

		add(chartPanel, BorderLayout.CENTER);

		chartPanel.updateUI();
	}

	public XYSeriesCollection clonedata(XYSeriesCollection data)
	{

		try
		{
			return (XYSeriesCollection)ObjectCloner.deepCopy(data);
		}
		catch (Exception e)
		{
			System.err.println("Exception in execute() - "
							+ data.getClass().getCanonicalName()
							+ " is not serializable" + e);
			return null;
		}
	}

	public ChartPanel newChart(XYSeriesCollection datatempcopy)
	{

		JFreeChart chart = ChartFactory.createXYLineChart(title, xaxis, yaxis,
						datatempcopy, PlotOrientation.VERTICAL, true, true, false);

		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(this.getWidth(),
						this.getHeight()));

		return chartPanel;

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

		data = new XYSeriesCollection();

		datatempcopy = clonedata(data);

		chartPanel = newChart(datatempcopy);

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

		synchronized (this)
		{
			chartPanel = newChart(data);
		}

		this.setLayout(new BorderLayout());
		add(chartPanel, BorderLayout.CENTER);

		chartPanel.updateUI();

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
