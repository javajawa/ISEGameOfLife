/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.mace.plugins;

import ise.mace.environment.PublicEnvironmentConnection;
import ise.mace.participants.PublicGroupDataModel;
import ise.mace.tokens.TurnType;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.lang.String;
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
public class GroupGraphs extends JPanel implements Plugin
{
	private static final long serialVersionUID = 1L;

	private class GroupPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		private final XYSeries EconomicHistorySeries;
		private final PublicGroupDataModel gm;
		private final ValueAxis domainAxis1;
		private final ValueAxis rangeAxis1;

                private final XYSeries SocialHistorySeries;
                private final ValueAxis domainAxis2;
		private final ValueAxis rangeAxis2;

                private final XYSeries FoodHistorySeries;
                private final ValueAxis domainAxis3;
		private final ValueAxis rangeAxis3;

                private final XYSeries LoyaltyHistorySeries;
                private final ValueAxis domainAxis4;
		private final ValueAxis rangeAxis4;

                private final XYSeries HappinessHistorySeries;
                private final ValueAxis domainAxis5;
		private final ValueAxis rangeAxis5;



		private JLabel labelise(String s)
		{
			JLabel ret = new JLabel(s);
			ret.setHorizontalAlignment(SwingConstants.CENTER);
			ret.setFont(ret.getFont().deriveFont(6));
			return ret;
		}
                private JLabel labelise(String s,int font)
		{
			JLabel ret = new JLabel(s);
			ret.setHorizontalAlignment(SwingConstants.CENTER);
			ret.setFont(ret.getFont().deriveFont(font));
			return ret;
		}

		@SuppressWarnings("LeakingThisInConstructor")
		GroupPanel(PublicGroupDataModel gm)
		{
                        this.gm = gm;

                        //Economic Graph
			this.EconomicHistorySeries = new XYSeries(gm.getId());
			JFreeChart chart1 = ChartFactory.createXYLineChart(null, null, null,
							new XYSeriesCollection(EconomicHistorySeries),
							PlotOrientation.VERTICAL, false, false, false);
			ChartPanel chartPanel1 = new ChartPanel(chart1);
			chart1.getXYPlot().setBackgroundAlpha(1);
			domainAxis1 = chart1.getXYPlot().getDomainAxis();
			rangeAxis1 = chart1.getXYPlot().getRangeAxis();

                        //Social Graph
                        this.SocialHistorySeries = new XYSeries(gm.getId());
			JFreeChart chart2 = ChartFactory.createXYLineChart(null, null, null,
							new XYSeriesCollection(SocialHistorySeries),
							PlotOrientation.VERTICAL, false, false, false);
			ChartPanel chartPanel2 = new ChartPanel(chart2);
			chart2.getXYPlot().setBackgroundAlpha(1);
			domainAxis2 = chart2.getXYPlot().getDomainAxis();
			rangeAxis2 = chart2.getXYPlot().getRangeAxis();

                        //Food Graph
                        this.FoodHistorySeries = new XYSeries(gm.getId());
			JFreeChart chart3 = ChartFactory.createXYLineChart(null, null, null,
							new XYSeriesCollection(FoodHistorySeries),
							PlotOrientation.VERTICAL, false, false, false);
			ChartPanel chartPanel3 = new ChartPanel(chart3);
			chart3.getXYPlot().setBackgroundAlpha(1);
			domainAxis3 = chart3.getXYPlot().getDomainAxis();
			rangeAxis3 = chart3.getXYPlot().getRangeAxis();

                        //Loyalty Graph
                        this.LoyaltyHistorySeries = new XYSeries(gm.getId());
			JFreeChart chart4 = ChartFactory.createXYLineChart(null, null, null,
							new XYSeriesCollection(LoyaltyHistorySeries),
							PlotOrientation.VERTICAL, false, false, false);
			ChartPanel chartPanel4 = new ChartPanel(chart4);
			chart4.getXYPlot().setBackgroundAlpha(1);
			domainAxis4 = chart4.getXYPlot().getDomainAxis();
			rangeAxis4 = chart4.getXYPlot().getRangeAxis();

                        //Happiness Graph
                        this.HappinessHistorySeries = new XYSeries(gm.getId());
			JFreeChart chart5 = ChartFactory.createXYLineChart(null, null, null,
							new XYSeriesCollection(HappinessHistorySeries),
							PlotOrientation.VERTICAL, false, false, false);
			ChartPanel chartPanel5 = new ChartPanel(chart5);
			chart5.getXYPlot().setBackgroundAlpha(1);
			domainAxis5 = chart5.getXYPlot().getDomainAxis();
			rangeAxis5 = chart5.getXYPlot().getRangeAxis();

//
//                        String Social = Double.toString(this.gm.getEstimatedSocialLocation());
//                        String Economic = Double.toString(this.gm.getCurrentEconomicPoisition());
//
//                        double Happiness = 0;
//                        double Loyalty = 0;
//                        double Food = 0;
//
//
//                          double size = this.gm.getMemberList().size();
//                        for( String memberId : this.gm.getMemberList())
//                        {
//                           //if (PublicEnvironmentConnection.getInstance().getAgentById(memberId).getCurrentHappiness() != null)
//                                Happiness += PublicEnvironmentConnection.getInstance().getAgentById(memberId).getCurrentHappiness();
//                          //  if (PublicEnvironmentConnection.getInstance().getAgentById(memberId).getCurrentLoyalty() != null)
//                                Loyalty += PublicEnvironmentConnection.getInstance().getAgentById(memberId).getCurrentLoyalty();
//                            Food += PublicEnvironmentConnection.getInstance().getAgentById(memberId).getFoodAmount();
//                        }
//                        Happiness= Happiness/size;
//                        Loyalty = Loyalty/size;
//                        Food = Food/size;

                        JPanel dataPanel = new JPanel(new GridLayout(3, 1));

                        dataPanel.add(labelise(this.gm.getName(),8));
                        dataPanel.add(labelise("1.Economic Belief Graph"));
                        dataPanel.add(labelise("2.Social Belief Graph"));
                        dataPanel.add(labelise("3.Average Food Graph"));
                        dataPanel.add(labelise("4.Average Loyalty Graph"));
                        dataPanel.add(labelise("5.Average Happiness Graph"));
			//dataPanel.add(labelise("Size: "+ this.gm.getMemberList().size()));

                        //dataPanel.add(labelise("Economic: "+Economic));
                        //dataPanel.add(labelise("Social: "+Social));

			//dataPanel.add(labelise("Average Loyalty: "+Loyalty));
			//dataPanel.add(labelise("Average Happiness: "+Happiness));

                        //dataPanel.add(labelise("Average Food: "+Food));


			chartPanel1.setVisible(true);
                        chartPanel2.setVisible(true);
                        chartPanel3.setVisible(true);
                        chartPanel4.setVisible(true);
                        chartPanel5.setVisible(true);

			this.setLayout(new GridLayout(1,6));
			this.add(dataPanel);
			this.add(chartPanel1);
                        this.add(chartPanel2);
                        this.add(chartPanel3);
                        this.add(chartPanel4);
                        this.add(chartPanel5);
			this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			window.add(this);
			this.setPreferredSize(new Dimension(getWidth() - barWidth, 125));
		}

		void updateData()
		{
                        //Update Economic
			if (gm.getCurrentEconomicPoisition() > graphHeight) graphHeight += 1;
			EconomicHistorySeries.addOrUpdate(ec.getRoundsPassed(), gm.getCurrentEconomicPoisition());
			domainAxis1.setRange(ec.getRoundsPassed() - 25, ec.getRoundsPassed());
			rangeAxis1.setRange(0, graphHeight);

                        //Update Social
                       if (gm.getEstimatedSocialLocation() > graphHeight) graphHeight += 1;
			SocialHistorySeries.addOrUpdate(ec.getRoundsPassed(), gm.getEstimatedSocialLocation());
			domainAxis2.setRange(ec.getRoundsPassed() - 25, ec.getRoundsPassed());
			rangeAxis2.setRange(0, graphHeight);

                        //Update Food
                        double size = gm.getMemberList().size();
                        for( String memberId : gm.getMemberList())
                        {
                            Happiness += PublicEnvironmentConnection.getInstance().getAgentById(memberId).getCurrentHappiness();
                            Loyalty += PublicEnvironmentConnection.getInstance().getAgentById(memberId).getCurrentLoyalty();
                            Food += PublicEnvironmentConnection.getInstance().getAgentById(memberId).getFoodAmount();
                        }
                        if (size != 0)
                        {
                            Happiness= Happiness/size;
                            Loyalty = Loyalty/size;
                            Food = Food/size;
                        }
                        else{
                            Happiness= 0;
                            Loyalty = 0;
                            Food = 0;
                        }

                        if (Food > graphHeightFood) graphHeightFood += 25;
			FoodHistorySeries.addOrUpdate(ec.getRoundsPassed(), Food);
			domainAxis3.setRange(ec.getRoundsPassed() - 25, ec.getRoundsPassed());
			rangeAxis3.setRange(0, graphHeightFood);

                        if (Loyalty > graphHeight) graphHeight += 1;
			LoyaltyHistorySeries.addOrUpdate(ec.getRoundsPassed(), Loyalty);
			domainAxis4.setRange(ec.getRoundsPassed() - 25, ec.getRoundsPassed());
			rangeAxis4.setRange(0, graphHeight);

                        if (Happiness > graphHeight) graphHeight += 1;
			HappinessHistorySeries.addOrUpdate(ec.getRoundsPassed(), Happiness);
			domainAxis5.setRange(ec.getRoundsPassed() - 25, ec.getRoundsPassed());
			rangeAxis5.setRange(0, graphHeight);


                }
	}

	private final static String label = "Group Graphs";

	private Simulation sim;
	private PublicEnvironmentConnection ec = null;

	private final JPanel window = new JPanel();
	private final JScrollPane pane = new JScrollPane(window, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	//private final HashMap<String, GroupPanel> panels = new HashMap<String, GroupPanel>();
        private final TreeMap<String, GroupPanel> panels = new TreeMap<String, GroupPanel>();
	private int barWidth;
	private int graphHeight = 1;
        private int graphHeightFood = 50;


        private double Happiness = 0;
        private double Loyalty = 0;
        private double Food = 0;

	public GroupGraphs()
	{
		// Nothing to see here. Move along, citizen!
	}

	@Override
	public void execute()
	{
		if (ec == null) ec = PublicEnvironmentConnection.getInstance();
		if (ec.getCurrentTurnType() != TurnType.firstTurn) return;

		barWidth = this.pane.getVerticalScrollBar().getWidth();
             //panels.clear();
              //this.removeAll();
              //this.window.removeAll();
		for (String aid : ec.getGroups())
		{       if (!panels.containsKey(aid))
			{
				panels.put(aid, new GroupPanel(ec.getGroupById(aid)));
                        }
                        Happiness= 0;
                        Loyalty = 0;
                        Food = 0;
			panels.get(aid).updateData();
                        //this.add(panels.get(aid));

		}
		validate();


                this.repaint();

                //panels.clear();




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

