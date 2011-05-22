/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ptsutils;

import org.joda.time.DateTime;

/**
 *
 * @author rickcharon
 */
public class PtsSymbolInfo {

  public String symbol;
  public String exchange;
  public int multiplier;
  public int priceMagnifier;
  public String fullName;
  public DateTime minDate;
  public DateTime maxDate;
  public int maxActiveExpiry;
  public Double mintick = null;

  public Double getMintick() {
    return mintick;
  }

  public void setMintick(Double mintick) {
    this.mintick = mintick;
  }

  

  public double getActualPriceFromExpandedPrice(double expandedPrice) {
    //res.getDouble("open") / workingSI.priceMagnifier * workingSI.multiplier,
    return (expandedPrice / getMultiplier()) * getPriceMagnifier();
  }

  public double getExpandedPriceActualPrice(double actualPrice) {
    //res.getDouble("open") / workingSI.priceMagnifier * workingSI.multiplier,
    return (actualPrice / priceMagnifier * multiplier);
  }

  public String getExchange() {
    return exchange;
  }

  public void setExchange(String exchange) {
    this.exchange = exchange;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public DateTime getMaxDate() {
    return maxDate;
  }

  public void setMaxDate(DateTime maxDate) {
    this.maxDate = maxDate;
  }

  public int getMaxActiveExpiry() {
    return maxActiveExpiry;
  }

  public void setMaxExpiry(int maxExpiry) {
    this.maxActiveExpiry = maxExpiry;
  }

  public DateTime getMinDate() {
    return minDate;
  }

  public void setMinDate(DateTime minDate) {
    this.minDate = minDate;
  }

  public int getMultiplier() {
    return multiplier;
  }

  public void setMultiplier(int multiplier) {
    this.multiplier = multiplier;
  }

  public int getPriceMagnifier() {
    return priceMagnifier;
  }

  public void setPriceMagnifier(int priceMagnifier) {
    this.priceMagnifier = priceMagnifier;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }



}
