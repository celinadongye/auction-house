/**
 * 
 */
package auctionhouse;

/**
 * Money is a utility class for currency values.
 * A Money object represents the monetary value to be transferred between the
 * seller and the buyer when a lot is sold, which includes the pounds currency.
 * 
 * @author      pbj
 * @version     %I%, %G%
 */
public class Money implements Comparable<Money> {
    
    /**
     * This variable declaration represents the amount of money that this
     * particular Money object specifies, to be transferred between the seller
     * and buyer.
     */
    private double value;
    
    private static long getNearestPence(double pounds) {
        return Math.round(pounds * 100.0);
    }
    
    private static double normalise(double pounds) {
        return getNearestPence(pounds)/100.0;
        
    }
 
    public Money(String pounds) {
        value = normalise(Double.parseDouble(pounds));
    }
    
    private Money(double pounds) {
        value = pounds;
    }
    
    /**
     * Adds this specified value to the current amount of the value variable.
     * 
     * @param  m        the amount to be added
     * @return          total sum of the current amount in value variable and
     *                  parameter amount
     */
    public Money add(Money m) {
        return new Money(value + m.value);
    }
    
    /**
     * Subtracts this specified parameter value to the amount in the value variable.
     * 
     * @param  m        the amount to be subtracted
     * @return          total amount in value variable after the parameter value
     *                  is subtracted
     */
    
    public Money subtract(Money m) {
        return new Money(value - m.value);
    }
 
    /**
     * Adds the specified percentage amount to the existing monetary amount in
     * value variable.
     * 
     * @param  percent  the amount in percentage to be added
     * @return          resulting amount of adding a percentage to value variable 
     */
    public Money addPercent(double percent) {
        return new Money(normalise(value * (1 + percent/100.0)));
    }
     
    @Override
    public String toString() {
        return String.format("%.2f", value);
    }
    
    /**
     * Compares the saved monetary amount in the value variable to this Money
     * object.
     * 
     * @param  m        amount to compare with the value variable
     * @return          integer 0 is returned when both values match, lesser
     *                  than 0 if m is greater than value variable amount,
     *                  greater than 0 if m is lesser
     */
    public int compareTo(Money m) {
        return Long.compare(getNearestPence(value),  getNearestPence(m.value)); 
    }
    
    /**
     * Checks if the money amount m is greater than or equal to this value variable.
     * 
     * @param  m        amount to check against this value variable
     * @return          <code>true</code> if argument m is greater than or equal to
     *                  this value variable;
     *                  <code>false</code> otherwise
     */
    public Boolean lessEqual(Money m) {
        return compareTo(m) <= 0;
    }
    
    /**
     * Checks if this Money object and argument object are both in Money class,
     * and if their values are the same.
     * 
     * @param  o        object to be checked
     * @return          <code>true</code> if it is of type Money and the amount
     *                  in this value variable equals to argument o
     *                  <code>false</code> otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Money)) return false;
        Money oM = (Money) o;
        return compareTo(oM) == 0;       
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(getNearestPence(value));
    }
      

}
