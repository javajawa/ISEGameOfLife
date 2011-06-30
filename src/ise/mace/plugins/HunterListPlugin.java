package ise.mace.plugins;

import ise.mace.environment.PublicEnvironmentConnection;
import ise.mace.participants.PublicAgentDataModel;
import ise.mace.tokens.TurnType;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import presage.Plugin;
import presage.Simulation;

/**
 *
 */
public class HunterListPlugin extends JPanel implements Plugin
{
	private static final long serialVersionUID = 1L;

	private class HunterPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		private final XYSeries foodHistorySeries;
		private final PublicAgentDataModel dm;
		private final ValueAxis domainAxis;
		private final ValueAxis rangeAxis;

		private JLabel labelise(String s)
		{
			JLabel ret = new JLabel(s);
			ret.setHorizontalAlignment(SwingConstants.CENTER);
			ret.setFont(ret.getFont().deriveFont(6));
			return ret;
		}

		@SuppressWarnings("LeakingThisInConstructor")
		HunterPanel(PublicAgentDataModel dm)
		{
			this.dm = dm;
			this.foodHistorySeries = new XYSeries(dm.getId());

			JFreeChart chart = ChartFactory.createXYLineChart(null, null, null,
							new XYSeriesCollection(foodHistorySeries),
							PlotOrientation.VERTICAL, false, false, false);
			ChartPanel chartPanel = new ChartPanel(chart);

			chart.getXYPlot().setBackgroundAlpha(1);
			domainAxis = chart.getXYPlot().getDomainAxis();
			rangeAxis = chart.getXYPlot().getRangeAxis();

			JPanel dataPanel = new JPanel(new GridLayout(3, 2, 2, -2));
			dataPanel.add(labelise(dm.getName()));
			dataPanel.add(labelise(dm.getPlayerClass()));
			dataPanel.add(labelise("Test 1"));
			dataPanel.add(labelise("Test 2"));
			dataPanel.add(labelise("Test 3"));
			dataPanel.add(labelise("Test 4"));

			chartPanel.setVisible(true);
			this.setLayout(new GridLayout(1, 2));
			this.add(dataPanel);
			this.add(chartPanel);
			this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			window.add(this);
			this.setPreferredSize(new Dimension(getWidth() - barWidth, 125));
		}

		void updateData()
		{
			if (dm.getFoodAmount() > graphHeight) graphHeight += 25;
			foodHistorySeries.addOrUpdate(ec.getRoundsPassed(), dm.getFoodAmount());
			domainAxis.setRange(ec.getRoundsPassed() - 25, ec.getRoundsPassed());
			rangeAxis.setRange(0, graphHeight);
		}
	}

	private final static String label = "Hunter Logs";
	private Simulation sim;
	private PublicEnvironmentConnection ec = null;
	private final JPanel window = new JPanel();
	private final JScrollPane pane = new JScrollPane(window,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private final HashMap<String, HunterPanel> panels = new HashMap<String, HunterPanel>();
	private int barWidth;
	private int graphHeight = 50;

	public HunterListPlugin()
	{
		// Nothing to see here. Move along, citizen!
	}

	@Override
	public void execute()
	{
		if (ec == null) ec = PublicEnvironmentConnection.getInstance();
		if (ec.getCurrentTurnType() != TurnType.firstTurn) return;

		barWidth = this.pane.getVerticalScrollBar().getWidth();

		TreeMap<String, String> name_id_map = new TreeMap<String, String>();

		// Create a set sorted alphabetically by human readable name
		for (String aid : sim.getactiveParticipantIdSet("hunter"))
		{
			name_id_map.put(ec.getAgentById(aid).getName(), aid);
		}

		// Add panels in alphabetical order
		for (Map.Entry<String, String> entry : name_id_map.entrySet())
		{
			String aid = entry.getValue();
			if (!panels.containsKey(aid))
			{
				panels.put(aid, new HunterPanel(ec.getAgentById(aid)));
			}
			panels.get(aid).updateData();
		}
	}

	@Override
	public void initialise(Simulation sim)
	{
		setLayout(new BorderLayout());
		this.sim = sim;
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
