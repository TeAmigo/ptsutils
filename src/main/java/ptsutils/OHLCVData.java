/**
 * OHLCVData.java Created Feb 15, 2011 by rickcharon.
 * Convenience class for passing data back from PtsOHLCV to IndicatorGroup
 *
 */
package ptsutils;

import java.util.ArrayList;
import org.jfree.data.time.RegularTimePeriod;

public class OHLCVData {

  public double[] opens;
  public double[] closes;
  public double[] highs;
  public double[] lows;
  public double[] volumes;
  public ArrayList<RegularTimePeriod> periods;
}
