package ise.gameoflife.plugins;

import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.participants.PublicAgentDataModel;
import ise.gameoflife.tokens.TurnType;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
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
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import presage.Plugin;
import presage.Simulation;

/**
 * 
 * @author Benedict
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
			domainAxis.setVisible(false);
			chart.getXYPlot().getRangeAxis().setRange(new Range(0, 50), true, true);

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
			foodHistorySeries.addOrUpdate(ec.getRoundsPassed(), dm.getFoodAmount());
			domainAxis.setRange(ec.getRoundsPassed() - 25, ec.getRoundsPassed());
		}
	}

	private final static String label = "Hunter Logs";
	
	private Simulation sim;
	private PublicEnvironmentConnection ec = null;

	private final JPanel window = new JPanel();
	private final JScrollPane pane = new JScrollPane(window, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private final HashMap<String, HunterPanel> panels = new HashMap<String, HunterPanel>();
	private int barWidth;

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

		for (String aid : sim.getactiveParticipantIdSet("hunter"))
		{
			if (!panels.containsKey(aid))
			{
				panels.put(aid, new HunterPanel(ec.getAgentById(aid)));
			}
			panels.get(aid).updateData();
		}
		validate();
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
