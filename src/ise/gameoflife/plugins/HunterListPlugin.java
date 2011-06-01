package ise.gameoflife.plugins;

import ise.gameoflife.models.PublicAgentDataModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import presage.Plugin;
import presage.Simulation;

/**
 * TODO: Make this plugin work
 * TODO: Document this plugin
 * TODO: Convert each Agents section to a Panel
 * TODO: Make the left hand side of the panel display name, group, food, etc.
 * TODO: Make the right hand side of the panel show the little graph
 * @author Benedict
 */
public class HunterListPlugin extends JPanel implements Plugin
{
	private final static long serialVersionUID = 1L;
	private final static String label = "Hunter Logs";
	
	private Simulation sim;
	private final HashMap<PublicAgentDataModel, XYSeries> datas = new HashMap<PublicAgentDataModel, XYSeries>();
	private final JScrollPane window = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	public HunterListPlugin()
	{
		// Nothing to see here. Move along, citizen!
	}
	
	@Override
	public void execute()
	{
		for (String aId : sim.getactiveParticipantIdSet("hunter"))
		{
			final PublicAgentDataModel dm = (PublicAgentDataModel)sim.getPlayerDataModel(aId);
			if (!datas.containsKey(dm))
			{
				createChart(dm);
			}
		}
		window.validate();
		
		synchronized(datas)
		{
			for (Entry<PublicAgentDataModel,XYSeries> e : datas.entrySet())
			{
				e.getValue().add(sim.getTime(), e.getKey().getFoodAmount());
			}
		}
	}

	private void createChart(PublicAgentDataModel dm)
	{
		XYSeries g = new XYSeries(dm.getId());
		
		JFreeChart chart = ChartFactory.createXYLineChart(null, null, null,	new XYSeriesCollection(g), PlotOrientation.VERTICAL, false, false, false);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(getWidth(), 80));
		
		chartPanel.setVisible(true);
		window.add(chartPanel);
		synchronized(datas)
		{
			datas.put(dm, g);
		}
	}
	
	@Override
	public void initialise(Simulation sim)
	{
		this.sim = sim;
		this.setLayout(new BorderLayout());
		window.setVisible(true);
		this.add(window);
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
		// TODO: Anything here?
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
	
}
