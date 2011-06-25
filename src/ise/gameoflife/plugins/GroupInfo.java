/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.gameoflife.plugins;

import ise.gameoflife.agents.PoliticalAgentGroup;
import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.participants.PublicGroupDataModel;
import ise.gameoflife.tokens.TurnType;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import presage.Plugin;
import presage.Simulation;
/**
 *
 * @author The0s
 */
public class GroupInfo extends JPanel implements Plugin
{
	private static final long serialVersionUID = 1L;

	private class GroupPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		private final PublicGroupDataModel gm;

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

                        String Social = Double.toString(this.gm.getEstimatedSocialLocation());
                        String Economic = Double.toString(this.gm.getCurrentEconomicPoisition());

                        double Happiness = 0;
                        double Loyalty = 0;
                        double Food = 0;
                        String Leader = "Null";

                          double size = this.gm.getMemberList().size();
                        for( String memberId : this.gm.getMemberList())
                        {
                                Happiness += PublicEnvironmentConnection.getInstance().getAgentById(memberId).getCurrentHappiness();
                                Loyalty += PublicEnvironmentConnection.getInstance().getAgentById(memberId).getCurrentLoyalty();
                                Food += PublicEnvironmentConnection.getInstance().getAgentById(memberId).getFoodAmount();
                        }
                        Happiness= Happiness/size;
                        Loyalty = Loyalty/size;
                        Food = Food/size;

                        //Leaders
                        for (String ldr : this.gm.getPanel()){
                            if (Leader.equals("Null") && !this.gm.getPanel().isEmpty()) 
                                Leader= "";

                            if (ec.getAgentById(ldr) != null)
                                Leader = Leader + ec.getAgentById(ldr).getName() + "  ";
                            
                        }

                        JPanel dataPanel = new JPanel(new GridLayout(4, 2, 1, -1));

                        String name = this.gm.getName();
                        if (this.gm.getId() == null ? PoliticalAgentGroup.special == null : this.gm.getId().equals(PoliticalAgentGroup.special))
                        {
                            name = name + " - Special Group-Agents";
                        }

                        dataPanel.add(labelise(name,8));
			dataPanel.add(labelise("Size: "+ this.gm.getMemberList().size()));

                        dataPanel.add(labelise("Economic: "+Economic));
                        dataPanel.add(labelise("Social: "+Social));

			dataPanel.add(labelise("Average Loyalty: "+Loyalty));
			dataPanel.add(labelise("Average Happiness: "+Happiness));

                        dataPanel.add(labelise("Average Food: "+Food));
                        dataPanel.add(labelise("Leaders: "+ Leader));

			this.setLayout(new GridLayout(1,1));
			this.add(dataPanel);
			this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			window.add(this);
			this.setPreferredSize(new Dimension(getWidth() - barWidth, 125));
		}

	}

	private final static String label = "Group logs";

	private PublicEnvironmentConnection ec = null;
        private Simulation sim;
	private final JPanel window = new JPanel();
	private final JScrollPane pane = new JScrollPane(window, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        private final TreeMap<String, GroupPanel> panels = new TreeMap<String, GroupPanel>();
	private int barWidth;

	public GroupInfo()
	{
		// Nothing to see here. Move along, citizen!
	}

	@Override
	public void execute()
	{
		if (ec == null) ec = PublicEnvironmentConnection.getInstance();
		if (ec.getCurrentTurnType() != TurnType.firstTurn) return;

		barWidth = this.pane.getVerticalScrollBar().getWidth();
                panels.clear();
                this.window.removeAll();
		for (String aid : ec.availableGroups())
		{       if (!panels.containsKey(aid) && ec.getGroupById(aid).getMemberList().size() > 0 )
			{
				panels.put(aid, new GroupPanel(ec.getGroupById(aid)));
                        }
		}
		validate();


                this.repaint();
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
