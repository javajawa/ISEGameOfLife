package ise.mace.models;

import ise.mace.models.ScaledDouble;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Benedict
 */
public class ScaledDoubleTest
{
	public ScaledDoubleTest()
	{
	}

	/**
	 * Test of intValue method, of class ScaledDouble.
	 */
	@Test
	public void testScaling_1()
	{
		ScaledDouble instance = new ScaledDouble();

		instance.setValue(-1);
		double expResult = 0.4524;
		double result = instance.doubleValue();
		assertEquals(expResult, result, 0.0001);
	}

	/**
	 * Test of intValue method, of class ScaledDouble.
	 */
	@Test
	public void testInit_1()
	{
		ScaledDouble instance = new ScaledDouble(0.54758);

		int expResult = 1;
		int result = instance.intValue();
		assertEquals(expResult, result);
	}

	/**
	 * Test of intValue method, of class ScaledDouble.
	 */
	@Test
	public void testInit_14()
	{
		ScaledDouble instance = new ScaledDouble(0.87670);

		int expResult = 14;
		int result = instance.intValue();
		assertEquals(expResult, result);
	}

	/**
	 * Test of intValue method, of class ScaledDouble.
	 */
	@Test
	public void testInit_m14()
	{
		ScaledDouble instance = new ScaledDouble(0.1233);

		int expResult = -14;
		int result = instance.intValue();
		assertEquals(expResult, result);
	}

	/**
	 * Test of intValue method, of class ScaledDouble.
	 */
	@Test
	public void testScaling_2()
	{
		ScaledDouble instance = new ScaledDouble();

		instance.setValue(-3);
		instance.alterValue(5);
		instance.alterValue(-2);

		double expResult = 0.5;
		double result = instance.doubleValue();
		assertEquals(expResult, result, 0.0);
	}

	/**
	 * Test of floatValue method, of class ScaledDouble.
	 */
	@Test
	public void testInitialValue()
	{
		ScaledDouble instance = new ScaledDouble();
		double expResult = 0.5;
		double result = instance.doubleValue();
		assertEquals(expResult, result, 0.0);
	}

	/**
	 * Test of doubleValue method, of class ScaledDouble.
	 */
	@Test
	public void testTinyNegativeValue()
	{
		ScaledDouble instance = new ScaledDouble(1000000);
		
		instance.setValue(-1);
		double expResult = 0.5;
		double result = instance.doubleValue();
		assertEquals(expResult, result, 0.001);
	}
}
