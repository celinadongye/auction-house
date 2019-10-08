/**
 * 
 */
package auctionhouse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author pbj
 *
 */
public class MoneyTest {

    @Test    
    public void testAdd() {
        Money val1 = new Money("12.34");
        Money val2 = new Money("0.66");
        Money result = val1.add(val2);
        assertEquals("13.00", result.toString());
    }

    /*
     ***********************************************************************
     * BEGIN MODIFICATION AREA
     ***********************************************************************
     * Add all your JUnit tests for the Money class below.
     */
    
    @Test
    public void testSubtract() {
        Money val1 = new Money("12.34");
        Money val2 = new Money("0.66");
        Money result = val1.subtract(val2);
        assertEquals("11.68", result.toString());
    }
    
    @Test
    public void addPercent() {
        Money val = new Money("14.65");
        double percent = 20;
        Money result = val.addPercent(percent);
        assertEquals("17.58", result.toString());
    }
    
    @Test
    public void testcompareTo() {
        Money val1 = new Money("12.34");
        Money same = new Money("12.34");
        Money greater = new Money("14.64");
        Money smaller = new Money("4.66");
        int sameInt = val1.compareTo(same);
        int greaterInt = val1.compareTo(greater);
        int smallerInt = val1.compareTo(smaller);
        assertTrue(sameInt == 0);
        assertTrue(greaterInt < 0);
        assertTrue(smallerInt > 0);
    }
    
    @Test
    public void lessEqual() {
        Money mGreater = new Money("21.45");
        Money mLesser = new Money("11.23");
        Money mEqual = new Money("17.45");
        Money val = new Money("17.45");
        assertTrue(val.lessEqual(mEqual));
        assertTrue(val.lessEqual(mGreater));
        assertFalse(val.lessEqual(mLesser));
    }
    
    @Test
    public void testEquals() {
        Money greater = new Money("14.64");
        Money same = new Money("14.64");
        Money smaller = new Money("4.66");
        Object someObject = new Object();
        assertFalse(same.equals(someObject));
        assertFalse(same.equals(smaller));
        assertTrue(same.equals(greater));
    }

    /*
     * Put all class modifications above.
     ***********************************************************************
     * END MODIFICATION AREA
     ***********************************************************************
     */


}
