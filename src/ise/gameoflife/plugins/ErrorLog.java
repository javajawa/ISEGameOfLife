package ise.gameoflife.plugins;

import example.ExampleEnvDataModel;
import java.awt.Color;
import javax.swing.JPanel;
import presage.Plugin;
import presage.Simulation;

/**
 *
 * @author Olly
 */
public class ErrorLog extends JPanel implements Plugin
{
	//private static final long serialVersionUID = 1L;
	
	Simulation sim;
	//ExampleEnvDataModel dmodel;
	
	public ErrorLog(){
		//Nothing to see here, move along citizen.
	}
	
	public String getLabel() {
		return "ErrorLog";
	}

	public String getShortLabel() {
		return "ErrorLog";
}
	public void initialise(Simulation sim) {
		System.out.println(" ErrorLog Initialising....");

		this.sim = sim; 
		
		setBackground(Color.GRAY);
		
		//dmodel = (ExampleEnvDataModel)sim.getEnvDataModel();
	}

	@Override
	public void execute()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onDelete()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onSimulationComplete()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}