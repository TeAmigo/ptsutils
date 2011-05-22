package ptsutils;

import java.util.ArrayList;
import org.apache.commons.lang.ArrayUtils;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;

/*
 * 
 */
/**
 *
 * @author rickcharon
 */
public class PtsOHLCV {

  public OHLCSeriesCollection ohlc;
  public TimeSeriesCollection vol;
  public OHLCSeries PriceBars = new OHLCSeries("PriceBar");
  public TimeSeries Volume = new TimeSeries("Volume");
  private double[] opens = null;
  private double[] closes = null;
  private double[] highs = null;
  private double[] lows = null;
  private double[] volumes = null;
  private ArrayList<RegularTimePeriod> periods = null;
  private int lastItemCount = 0;

  public OHLCVData setupArrays() {
    int sizeNeeded = PriceBars.getItemCount() - lastItemCount;
    OHLCVData datas = new OHLCVData();
    OHLCItem ohlcItem;
    datas.periods = new ArrayList<RegularTimePeriod>();
    for (int i = 0; i < sizeNeeded; i++) {
      ohlcItem = (OHLCItem) PriceBars.getDataItem(lastItemCount + i);
      datas.opens = ArrayUtils.add(datas.opens, ohlcItem.getOpenValue());
      datas.closes = ArrayUtils.add(datas.closes, ohlcItem.getCloseValue());
      datas.highs = ArrayUtils.add(datas.highs, ohlcItem.getHighValue());
      datas.lows = ArrayUtils.add(datas.lows, ohlcItem.getLowValue());
      datas.periods.add(ohlcItem.getPeriod());
      datas.volumes = ArrayUtils.add(datas.volumes, Volume.getDataItem(lastItemCount + i).getValue().doubleValue());
    }
    lastItemCount = PriceBars.getItemCount();
    return datas;
  }



  public int getItemCount() {
    return PriceBars.getItemCount();
  }

  public ArrayList<RegularTimePeriod> getPeriods(int beginIdx) {
    ArrayList<RegularTimePeriod> _periods = new ArrayList<RegularTimePeriod>();
    //int sizeNeeded = PriceBars.getItemCount() - beginIdx;
    OHLCItem ohlcItem;
    for (int i = beginIdx; i < PriceBars.getItemCount(); i++) {
      ohlcItem = (OHLCItem) PriceBars.getDataItem(i);
      _periods.add(ohlcItem.getPeriod());
    }
    return _periods;
  }

  public double[] getCloses(int beginIdx) {
    int sizeNeeded = PriceBars.getItemCount() - beginIdx;
    double[] _closes = new double[sizeNeeded];
    OHLCItem ohlcItem;
    int closesIdx = 0;
    for (int i = beginIdx; i < PriceBars.getItemCount(); i++) {
      ohlcItem = (OHLCItem) PriceBars.getDataItem(i);
      _closes[closesIdx] = ohlcItem.getOpenValue();
      closesIdx++;
    }
    //setupArrays();
    return _closes;
  }

  public double[] getHighs() {
    if (lastItemCount == PriceBars.getItemCount()) {
      return highs;
    } else {
      //setupArrays();
      return highs;
    }
  }

  public int getLastItemCount() {
    return lastItemCount;
  }

  public double[] getLows() {
    if (lastItemCount == PriceBars.getItemCount()) {
      return lows;
    } else {
      //setupArrays();
      return lows;
    }
  }

  public double[] getOpens() {
    if (lastItemCount == PriceBars.getItemCount()) {
      return opens;
    } else {
      //setupArrays();
      return opens;
    }
  }

  public double[] getVolumes() {
    if (lastItemCount == PriceBars.getItemCount()) {
      return volumes;
    } else {
      //setupArrays();
      return volumes;
    }
  }

  public PtsOHLCV() {
    ohlc = new OHLCSeriesCollection();
    ohlc.addSeries(PriceBars);
    vol = new TimeSeriesCollection(Volume);
  }

  public static void main(String[] Args) {
    double b[] = {1, 2, 3, 4, 5};
    double c[] = null;
    c = ArrayUtils.add(c, 7);
    double a[] = new double[b.length];
    System.arraycopy(b, 0, a, 0, b.length);
    a[4] = 6.0;

    boolean contains = ArrayUtils.contains(b, 6);
    b = a;
    int j = 3;
  }
}
