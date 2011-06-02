package ise.gameoflife.plugins;

import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import ise.gameoflife.environment.Environment;
import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.participants.PublicAgentDataModel;
import ise.gameoflife.tokens.TurnType;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
 * TODO: Document this plugin
 * TODO: Convert each Agents section to a Panel
 * Make the left hand side of the panel display name, group, food, etc.
 * @author Benedict
 */
public class HunterListPlugin extends JPanel implements Plugin
{
	private final static long serialVersionUID = 1L;
	private final static String label = "Hunter Logs";
	
	private Simulation sim;
	private Environment en;
	private final HashMap<String, XYSeries> series = new HashMap<String, XYSeries>();
	private final JPanel window = new JPanel();
	private final JScrollPane pane = new JScrollPane(window, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private int barWidth;

	public HunterListPlugin()
	{
		// Nothing to see here. Move along, citizen!
	}
	
	@Override
	public void execute()
	{
		if (en.getCurrentTurnType() != TurnType.firstTurn) return;

		barWidth = this.pane.getVerticalScrollBar().getWidth();

		for (String aid : sim.getactiveParticipantIdSet("hunter"))
		{
			if (!series.containsKey(aid))
			{
				series.put(aid, createChart(aid));
			}

		}

		for (String aid : series.keySet())
		{
			PublicAgentDataModel dm = PublicEnvironmentConnection.getInstance().getAgentById(aid);
			if (dm != null)
			{
				series.get(aid).add(en.getRoundsPassed(), dm.getFoodAmount());
			}
			else
			{
				series.get(aid).add(en.getRoundsPassed(), 0);
			}
		}

		validate();
	}

	private XYSeries createChart(String id)
	{
		XYSeries g = new XYSeries(id);
		
		JFreeChart chart = ChartFactory.createXYLineChart(null, null, null,	new XYSeriesCollection(g), PlotOrientation.VERTICAL, false, false, false);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(getWidth() - barWidth, 125));

		chartPanel.setVisible(true);
		window.add(chartPanel);

		return g;
	}
	
	@Override
	public void initialise(Simulation sim)
	{
		setLayout(new BorderLayout());
		this.sim = sim;
		this.en = (Environment)sim.environment;
		window.setLayout(new BoxLayout(window, BoxLayout.PAGE_AXIS));
		this.add(pane);
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
		// Nothing to see here. Move along, citizen!
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
