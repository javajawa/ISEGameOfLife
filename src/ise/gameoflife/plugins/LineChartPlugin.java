/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.gameoflife.plugins;

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.util.Iterator;
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
public class LineChartPlugin extends JPanel implements Plugin {
    private static final long serialVersionUID = 1L;

	Simulation sim;

	@Element
	String outputpath;

	@Element
	int outputwidth;

	@Element
	int outputheight;

	@Element
	int updaterate;

	String title = "Population";

	String xaxis = "Simulation cycle";

	String yaxis = "Num People";

	String label = "Num People";

	EnvironmentDataModel dm;

	XYSeriesCollection data;

	// ChartPanel chartPanel;

	XYSeriesCollection datatempcopy;

	JPanel control = new JPanel();

	ChartPanel chartPanel;

	public LineChartPlugin() {
		// TODO Auto-generated constructor stub
	}

	@PluginConstructor( { "outputpath", "outputwidth", "outputheight", "updaterate" })
	public LineChartPlugin(String outputpath, int outputwidth,
			int outputheight, int updaterate) {
		super();
		this.outputpath = outputpath;
		this.outputwidth = outputwidth;
		this.outputheight = outputheight;
		this.updaterate = updaterate;
	}

	private double getNumPeople(EnvironmentDataModel dm) {

		int n = dm.agents.size();
		int population = 0;
		Iterator<String> iterator = dm.agents.keySet().iterator();
		while (iterator.hasNext()) {
			if (dm.agents.get(iterator.next()).alive())
				population++;
		}
		System.out.println("population = " + population );
		return population;
	}

        @Override
	public void execute() {
		// TODO Auto-generated method stub

		// simply get the environment datamodel
		dm = (EnvironmentDataModel) sim.getEnvDataModel();

		double population = getNumPeople(dm);

		synchronized (this) {

			XYSeries series;
			try {
				series = data.getSeries("population");
				series.add(dm.time, population);
			} catch (org.jfree.data.UnknownKeyException e) {
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

	public void updateChart() {

		this.remove(chartPanel);

		synchronized (this) {
			datatempcopy = clonedata(data);
		}

		chartPanel = newChart(datatempcopy);

		add(chartPanel, (new BorderLayout()).CENTER);

		chartPanel.updateUI();
	}

	public XYSeriesCollection clonedata(XYSeriesCollection data) {

		try {
			return (XYSeriesCollection) ObjectCloner.deepCopy(data);
		} catch (Exception e) {
			System.err.println("Exception in execute() - "
					+ data.getClass().getCanonicalName()
					+ " is not serializable" + e);
			return null;
		}
	}

	public ChartPanel newChart(XYSeriesCollection datatempcopy) {

		JFreeChart chart = ChartFactory.createXYLineChart(title, xaxis, yaxis,
				datatempcopy, PlotOrientation.VERTICAL, true, true, false);

		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(this.getWidth(),
				this.getHeight()));

		return chartPanel;

	}

        @Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return label;
	}

        @Override
	public String getShortLabel() {
		// TODO Auto-generated method stub
		return label;
	}

        @Override
	public void initialise(Simulation sim) {
		// TODO Auto-generated method stub
		System.out.println(" -Initialising....");

		this.sim = sim;

		setBackground(Color.GRAY);

		// dmodel = (StaticEnvDataModel) sim.getEnvDataModel();

		// System.out.println("DataModel Obtained");

		// series = new XYSeries("Agent0000");

		data = new XYSeriesCollection();

		datatempcopy = clonedata(data);

		chartPanel = newChart(datatempcopy);

		JLabel label = new JLabel("Graph will update every " + updaterate
				+ " Simulation cycles, to update now click: ");

		JButton updateButton = new JButton("Update Graph");

		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				updateChart();
			}
		});

		control.add(label);
		control.add(updateButton);

		this.setLayout(new BorderLayout());
		add(control, (new BorderLayout()).NORTH);
		add(chartPanel, (new BorderLayout()).CENTER);
	}

        @Override
	public void onDelete() {
		// TODO Auto-generated method stub

	}

        @Override
	public void onSimulationComplete() {
		// TODO Auto-generated method stub

		this.removeAll();

		synchronized (this) {
			chartPanel = newChart(data);
		}

		this.setLayout(new BorderLayout());
		add(chartPanel, (new BorderLayout()).CENTER);

		chartPanel.updateUI();

		File file;

		try {
			file = new File(outputpath);
		} catch (Exception e) {
			System.err.println("Graph Plugin " + label
					+ " : Invalid output path" + e);
			return;
		}

		try {
			int width = 1920;
			int height = 1200;
			ChartUtilities.saveChartAsPNG(file, chartPanel.getChart(), width,
					height);
		} catch (Exception e) {
			System.out.println("Problem occurred creating chart." + label);
		}

	}

}
