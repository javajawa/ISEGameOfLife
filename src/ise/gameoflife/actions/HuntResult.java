package ise.gameoflife.actions;

import java.util.UUID;
import presage.Input;

/**
 *
 * @author Olly
 */
public class HuntResult implements Input
{
	private UUID HuntResult;
	
	private double nutrition;
	
	/**
	 * Creates a new instance of the HuntResult class, which gives the result of a 
	 * Hunt action.
	 * @param id
	 * @param nutrition 
	 */
	public HuntResult(UUID id, double nutrition){
		super();
		this.HuntResult = id;
		this.nutrition = nutrition;
	}
	/**
	 * Returns nutrition value of food hunted
	 * @return 
	 */
	public double getNutritionValue(){
		return nutrition;
	}
	/**
	 * Returns UUID of the hunt result
	 * used as token to enable message passing
	 * @return 
	 */
	public UUID getHuntResultUUID(){
		return HuntResult;
	}

	@Override
	public String getPerformative()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public long getTimestamp()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setTimestamp(long timestamp)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
