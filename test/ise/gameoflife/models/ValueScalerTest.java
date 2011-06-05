package ise.gameoflife.models;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Benedict
 */
public class ValueScalerTest
{

	public ValueScalerTest()
	{
	}

	@Test
	public void testScale_1()
	{
		double old = 0.5;
		double amount = 1;

		double expResult = 0.55;
		double result = ValueScaler.scale(old, amount);

		assertEquals(expResult, result, 0.01);
	}

	@Test
	public void testScale_2()
	{
		double old = 0.5;
		double amount = 1;
		double step = 0.5;

		double expResult = 0.75;
		double result = ValueScaler.scale(old, amount, step);

		assertEquals(expResult, result, 0.0);
	}

	@Test
	public void testScale_3()
	{
		double old = 0.5;
		double amount = Double.POSITIVE_INFINITY;
		double step = 0.5;

		double expResult = 1;
		double result = ValueScaler.scale(old, amount, step);

		assertEquals(expResult, result, 0.0);
	}
}
