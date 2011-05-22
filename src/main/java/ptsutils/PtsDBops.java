/*********************************************************************
 * File path:     petrasys/utils/DBops.java
 * Version:       
 * Description:   
 * Author:        Rick Charon <rickcharon@gmail.com>
 * Created at:    Tue Nov 16 09:22:38 2010
 * Modified at:   Thu Nov 18 09:06:55 2010
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 ********************************************************************/
package ptsutils;

import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.ohlc.OHLCSeries;
//import org.jfree.data.time.RegularTimePeriod;
//import ptscharts.PtsOHLCV;
//import ptscharts.PtsOrder;
//import ptscharts.PtsPaperTrade;
//import ptscharts.PtsSymbolInfo;

/**
 *
 * @author rickcharon
 */
public class PtsDBops {

  static private Connection tradesConnection = null;

  

  public static Connection setuptradesConnection() {

    try {
      Class.forName("org.postgresql.Driver");
      tradesConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/trading", "trader1", "trader1");
    } catch (Exception ex) {
      System.err.println("EXCEPTION: " + ex.getMessage());

    } finally {
      return tradesConnection;
    }

  }

  public static ArrayList distinctSymolsList() {
    CallableStatement callStmt = null;
    ArrayList<String> retList = new ArrayList<String>();
    try {
      callStmt = PtsDBops.setuptradesConnection().prepareCall("select * from distinctQuoteSymbols();",
              ResultSet.TYPE_SCROLL_INSENSITIVE,
              ResultSet.CONCUR_READ_ONLY);
      ResultSet res = callStmt.executeQuery();
      while(res.next()) {
        retList.add(res.getString("symbol"));
      }
    } catch (SQLException ex) {
      System.err.println("SQLException  in distinctSymolsList(): " + ex.getMessage());
    } finally {
      return retList;
    }
  }

  public static ArrayList<SymbolMaxDateLastExpiry> SymbolsMaxDateLastExpiryList() {
    CallableStatement callStmt = null;
    ArrayList<SymbolMaxDateLastExpiry> retList = new ArrayList<SymbolMaxDateLastExpiry>();
    try {
      callStmt = PtsDBops.setuptradesConnection().prepareCall("select * from symbolMaxDateLastExpiryList();",
              ResultSet.TYPE_SCROLL_INSENSITIVE,
              ResultSet.CONCUR_READ_ONLY);
      ResultSet res = callStmt.executeQuery();
      while(res.next()) {
        SymbolMaxDateLastExpiry sme = new SymbolMaxDateLastExpiry();
        sme.symbol = res.getString("symbol");
        sme.beginDateToDownload = res.getTimestamp("maxdate");
        sme.expiry = res.getInt("maxexpiry");
        retList.add(sme);
      }
    } catch (SQLException ex) {
      System.err.println("SQLException  in distinctSymbolMaxDateLastExpiryList(): " + ex.getMessage());
    } finally {
      return retList;
    }
  }

    public static ArrayList<SymbolMaxDateLastExpiry> SymbolsExpirysBetweemDatesList(int beforeE, int afterE) {
    CallableStatement callStmt = null;
    ArrayList<SymbolMaxDateLastExpiry> retList = new ArrayList<SymbolMaxDateLastExpiry>();
    try {
      callStmt = PtsDBops.setuptradesConnection().prepareCall(
              "SELECT distinct symbol, expiry, exchange FROM futuresContractDetails where symbol in" +
                            "(select distinct symbol FROM quotes1min) and " +
                            "expiry >= " +  beforeE + " and expiry <= " + afterE +
                            " order by symbol, expiry;",
              ResultSet.TYPE_SCROLL_INSENSITIVE,
              ResultSet.CONCUR_READ_ONLY);
      ResultSet res = callStmt.executeQuery();
      while(res.next()) {
        SymbolMaxDateLastExpiry sme = new SymbolMaxDateLastExpiry();
        sme.symbol = res.getString("symbol");
        sme.expiry = res.getInt("expiry");
        sme.exchange = res.getString("exchange");
        retList.add(sme);
      }
    } catch (SQLException ex) {
      System.err.println("SQLException  in distinctSymbolMaxDateLastExpiryList(): " + ex.getMessage());
    } finally {
      return retList;
    }
  }


  public static CallableStatement distinctSymsProc() {
    CallableStatement ret = null;
    try {
      ret = PtsDBops.setuptradesConnection().prepareCall("select * from distinctQuoteSymbols();",
              ResultSet.TYPE_SCROLL_INSENSITIVE,
              ResultSet.CONCUR_READ_ONLY);
    } catch (SQLException ex) {
      System.err.println(ex.getMessage());
    } finally {
      return ret;
    }
  }

  public static CallableStatement datedRangeBySymbol(String sym, Timestamp beginDate, Timestamp endDate) {
    CallableStatement ret = null;
    try {
      String callStr = "select * from datedRangeBySymbol('" + sym + "', '" + beginDate + "', '"
              + endDate + "');";
      ret = PtsDBops.setuptradesConnection().
              prepareCall(callStr, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "SQLException", JOptionPane.ERROR_MESSAGE);
    } finally {
      return ret;
    }
  }

  /**
   * rpc - 3/7/10 10:17 AM - Get a dated range for a symbol with a specific expiry
   * @param sym
   * @param expiry
   * @param beginDate
   * @param endDate
   * @return
   */
  public static PreparedStatement datedRangeBySymbolAndExpiry(String sym, int expiry,
          Timestamp beginDate, Timestamp endDate) {
    PreparedStatement pstmt = null;
    try {
      pstmt = PtsDBops.setuptradesConnection().prepareStatement("SELECT datetime, open, high,low, close, "
              + "volume FROM quotes1min"
              + "where symbol=? and "
              + "datetime >= ? and "
              + "datetime <= ?  and expiry=? order by datetime;");
      pstmt.setString(1, sym);
      pstmt.setTimestamp(2, beginDate);
      pstmt.setTimestamp(3, endDate);
      pstmt.setInt(4, expiry);
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "SQLException", JOptionPane.ERROR_MESSAGE);
    } finally {
      return pstmt;
    }
  }

  public static PreparedStatement minMaxDateBySymbolAndExpiry(String symbol, int expiry) {
    PreparedStatement pstmt = null;
    try {
      pstmt = PtsDBops.setuptradesConnection().prepareStatement("SELECT min(datetime) as minDate, max(datetime) as maxDate FROM quotes1min "
              + "WHERE symbol= ? and expiry= ?");
      pstmt.setString(1, symbol);
      pstmt.setInt(2, expiry);
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "SQLException", JOptionPane.ERROR_MESSAGE);
    } finally {
      return pstmt;
    }
  }

  /**
   * rpc - 3/7/10 10:26 AM - This works because the last symbol in the quotes1min table is
   * assumed to be the current working expiry. If it isn't, this could be a problem,
   * @param symbol the UL
   * @return a PreparedStatement that has 1 row, 1 column, with int max(expiry)
   */
  public static int maxExpiryWithDataBySymbol(String symbol) {
    PreparedStatement pstmt = null;
    int expiry = 0;
    try {
      pstmt = PtsDBops.setuptradesConnection().prepareStatement("SELECT max(expiry) FROM quotes1min  WHERE symbol= ?");
      pstmt.setString(1, symbol);
      ResultSet res = pstmt.executeQuery();
      if (res.next()) {
        expiry = res.getInt(1);
      } else {
        throw new Exception("No result set returned.");
      }
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "SQLException", JOptionPane.ERROR_MESSAGE);
    } finally {
      return expiry;
    }
  }

  /**
   * rpc - 4/18/10 1:19 PM Get the min and max Date in DB by UL
   * @param sym - UL
   * @return The relevant PreparedStatement.
   */
  public static PreparedStatement minMaxDatesBySym(String sym) {
    PreparedStatement pstmt = null;
    try {
      pstmt = PtsDBops.setuptradesConnection().prepareStatement(
              "SELECT min(datetime), max(datetime)  FROM quotes1min where symbol=?");
      pstmt.setString(1, sym);
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "SQLException", JOptionPane.ERROR_MESSAGE);
    } finally {
      return pstmt;
    }
  }

  public static PreparedStatement activeFuturesDetails() {
    PreparedStatement pstmt = null;
    try {
      pstmt = PtsDBops.setuptradesConnection().prepareStatement(
              "select * from activeFuturesDetails()");
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "SQLException", JOptionPane.ERROR_MESSAGE);
    } finally {
      return pstmt;
    }
  }

  public static PreparedStatement distinctSymbolInfos() {
    PreparedStatement pstmt = null;
    try {
      pstmt = PtsDBops.setuptradesConnection().prepareStatement("SELECT distinct  symbol, exchange, multiplier, "
              + "priceMagnifier, minTick, fullName "
              + "FROM futuresContractDetails");
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "SQLException", JOptionPane.ERROR_MESSAGE);
    } finally {
      return pstmt;
    }
  }

  public static PreparedStatement MultiplierAndMagnifier(String sym) {
    PreparedStatement pstmt = null;
    try {
      pstmt = PtsDBops.setuptradesConnection().prepareStatement("SELECT distinct multiplier, priceMagnifier "
              + "FROM futuresContractDetails WHERE symbol = '" + sym + "'");
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "SQLException", JOptionPane.ERROR_MESSAGE);
    } finally {
      return pstmt;
    }
  }

  public static PreparedStatement exchangeBySymbolandExpiry(String symbol, int expiry) {
    PreparedStatement pstmt = null;
    try {
      pstmt = PtsDBops.setuptradesConnection().prepareStatement("SELECT exchange FROM futuresContractDetails WHERE "
              + "symbol= ? and expiry= ?");
      pstmt.setString(1, symbol);
      pstmt.setInt(2, expiry);
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "SQLException", JOptionPane.ERROR_MESSAGE);
    } finally {
      return pstmt;
    }
  }

  public static void getOHLCandVolume(PtsOHLCV ohlcv, String sym, java.util.Date beginDT, java.util.Date endDT,
          int priceMagnifier, int multiplier) {
    OHLCSeries priceSeries = ohlcv.ohlc.getSeries(0);
    TimeSeries volumeSeries = ohlcv.vol.getSeries(0);
    try {
      PreparedStatement datedRangeBySymbol =
              PtsDBops.datedRangeBySymbol(sym, new Timestamp(beginDT.getTime()),
              new Timestamp(endDT.getTime()));
      ResultSet res = datedRangeBySymbol.executeQuery();
      while (res.next()) {
        Minute min = new Minute(res.getTimestamp("datetime"));
        ohlcv.ohlc.getSeries(0).add(
                min,
                res.getDouble("open") / priceMagnifier * multiplier,
                res.getDouble("high") / priceMagnifier * multiplier,
                res.getDouble("low") / priceMagnifier * multiplier,
                res.getDouble("close") / priceMagnifier * multiplier);
        ohlcv.vol.getSeries(0).add(min, res.getLong("volume"));
      }
    } catch (SQLException ex) {
      System.err.println("EXCEPTION: " + ex.getMessage());
    } catch (Exception ex) {
      System.err.println("EXCEPTION: " + ex.getMessage());
    } finally {
      return;
    }
  }

  public static int getOHLCandVolumeCompressed(
          PtsOHLCV ohlcv, PtsSymbolInfo symInfo, long beginDT, long endDT, int compressionFactor) {
    String sym = symInfo.getSymbol();
    int priceMagnifier = (int) symInfo.getPriceMagnifier();
    int multiplier = (int) symInfo.getMultiplier();
    CallableStatement ret = null;
    Timestamp beginTS = new Timestamp(beginDT);
    Timestamp endTS = new Timestamp(endDT);
    int resInt = 0;
    try {
      String callStr = "select * from createCompressedTable('" + sym + "', '" + beginTS.toString() + "', '"
              + endTS.toString() + "', " + compressionFactor + ");";
      ret = PtsDBops.setuptradesConnection().
              prepareCall(callStr, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      ResultSet res = ret.executeQuery();

      while (res.next()) {
        Timestamp ts = res.getTimestamp("datetime");
        if (ts == null) {
          continue;
        }
        Minute min = new Minute(res.getTimestamp("datetime"));
        ohlcv.ohlc.getSeries(0).add(
                min,
                res.getDouble("open") / priceMagnifier * multiplier,
                res.getDouble("high") / priceMagnifier * multiplier,
                res.getDouble("low") / priceMagnifier * multiplier,
                res.getDouble("close") / priceMagnifier * multiplier);
        ohlcv.vol.getSeries(0).addOrUpdate(min, res.getLong("volume"));
        resInt++;
      }
    } catch (SQLException ex) {
      System.err.println("SQLException: " + ex.getMessage());
    } catch (Exception ex) {
      System.err.println("EXCEPTION: " + ex.getMessage());
    } finally {
      return resInt;
    }
  }

  public static PreparedStatement getExpirysForUpdate(Connection con, String ul, int beginDate, int endDate) {
    PreparedStatement pstmt = null;
    try {
      pstmt = con.prepareStatement("SELECT * FROM futuresContractDetails "
              + "where symbol='" + ul + "'and expiry  >= " + beginDate + " and expiry <= " + endDate
              + " order by expiry");
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "SQLException", JOptionPane.ERROR_MESSAGE);
    } finally {
      return pstmt;
    }
  }

  public static PreparedStatement getActiveContracts(Connection con) {
    PreparedStatement pstmt = null;
    try {
      pstmt = con.prepareStatement("SELECT * FROM futuresContractDetails "
              + "where active = 1 order by symbol, expiry");
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "SQLException", JOptionPane.ERROR_MESSAGE);
    } finally {
      return pstmt;
    }
  }


  public static PreparedStatement updateBeginEndDatesForExpiry1(Connection con, String ul, int expiry,
          String beginDate, String endDate) {

    PreparedStatement pstmt = null;
    try {
      pstmt = con.prepareStatement("update futuresContractDetails "
              + " set active=1, beginDate  =  '" + beginDate + "', endDate = '"
              + endDate + "' where symbol='" + ul + "' and expiry = " + expiry);
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "SQLException", JOptionPane.ERROR_MESSAGE);
    } finally {
      return pstmt;
    }
  }

  public static PreparedStatement updateBeginEndDatesForExpiry(Connection con) {

    PreparedStatement pstmt = null;
    try {
      pstmt = con.prepareStatement("update futuresContractDetails "
              + " set active=1, beginDate  = ?, endDate = ? where symbol = ? and expiry = ?");
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "SQLException", JOptionPane.ERROR_MESSAGE);
    } finally {
      return pstmt;
    }
  }

  public static String createCompressionTable(int compressionFactor) {
    String dbTableName = "quotes" + compressionFactor + "min";
    try {
      PreparedStatement createCompressionTable =
              PtsDBops.setuptradesConnection().prepareStatement("CREATE TABLE IF NOT EXISTS "
              + dbTableName
              + " (" + "symbol VARCHAR( 15 ) NOT NULL , "
              + "datetime DATETIME NOT NULL , "
              + "open DOUBLE NOT NULL , "
              + "high DOUBLE NOT NULL , "
              + "low DOUBLE NOT NULL , "
              + "close DOUBLE NOT NULL , "
              + "volume BIGINT( 20 ) NOT NULL, "
              + "PRIMARY KEY(symbol, datetime))");
      createCompressionTable.execute();
      createCompressionTable.close();
    } catch (SQLException ex) {
      System.err.println("EXCEPTION: " + ex.getMessage());
      dbTableName = null;
    } finally {
      return dbTableName;
    }
  }

  public static PreparedStatement insertIntoCompressionTable(String table) {
    PreparedStatement pstmt = null;
    try {
      pstmt =
              PtsDBops.setuptradesConnection().
              prepareStatement("REPLACE INTO " + table + " VALUES (?, ? , ?, ?, ?, ?, ?)");
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "SQLException", JOptionPane.ERROR_MESSAGE);
    } finally {
      return pstmt;
    }
  }


  public static void insertIntoPaperOrdersTable(PtsOrder order) {

    PreparedStatement pstmt = null;
    try {
      String pstr = "INSERT INTO PaperOrders "
              + "(ul, ordertype, price, translatedprice, bartime, lossorgain, "
              + "orderid, parentid, entrytimestamp)"
              + "VALUES (? , ?, ?, ?, ?, ?, ?, ? , ?)";
      pstmt =
              PtsDBops.setuptradesConnection().prepareStatement(pstr);
// For Postgres
//            INSERT INTO paperorders(ul, ordertype, price, translatedprice, bartime, lossorgain, 
//                        orderid, parentid, entrytimestamp) 9 values

      //UL
      pstmt.setString(1, order.getUl());
      //ordertype
      pstmt.setObject(2, (Object) (order.getOrderType()), Types.OTHER);
      //pstmt.setString(2, order.getOrderType());
      //Price
      pstmt.setDouble(3, order.getPrice());
      //TranslatedPrice
      pstmt.setDouble(4, order.getTranslatedPrice());
      //BarTime
      pstmt.setTimestamp(5, (new Timestamp(order.getBarTime().getTime())));
      //Loss or Gain
      pstmt.setDouble(6, order.getLossOrGain());
      // OrderID
      pstmt.setInt(7, order.getOrderID());
      // ParentID
      pstmt.setInt(8, order.getParentID());
      //EntryDateTime
      pstmt.setTimestamp(9, (new Timestamp(order.getEntryDateTime().getTime())));
      pstmt.execute();
      pstmt.close();
    } catch (SQLException ex) {
      System.err.println("EXCEPTION: " + ex.getMessage());
    } finally {
    }
  }

  public static void insertIntoPaperTradesTable(PtsPaperTrade paperTrade) {
    PreparedStatement pstmt = null;
    try {
      pstmt =
              PtsDBops.setuptradesConnection().
              prepareStatement("INSERT INTO PaperTrades "
              + "(EnteredInDB, BeginTradeDateTime, symbol, Position, "
              + "entry, stoploss, stoprisk, stopprofit, "
              + "profitpotential, Outcome, ExitTradeDateTime) "
              + "VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
      //BeginTradeDateTime
      if (paperTrade.getBeginTradeDateTime() == null) {
        pstmt.setNull(1, Types.DATE);
      } else {
        pstmt.setTimestamp(1, new Timestamp(paperTrade.getBeginTradeDateTime().getTime()));
      }
      //symbol
      if (paperTrade.getSymbol() == null) {
        pstmt.setNull(2, Types.VARCHAR);
      } else {
        pstmt.setString(2, paperTrade.getSymbol());
      }
      //Position
      if (paperTrade.getPosition() == null) {
        pstmt.setNull(3, Types.OTHER);
      } else {
        String pos = paperTrade.getPosition();
        if (pos.equals("BuyToOpen")) {
          pos = "BUY";
        } else {
          pos = "SELL";
        }
        pstmt.setObject(3, (Object) pos, Types.OTHER);
//        pstmt.setString(3, paperTrade.getPosition());
      }
      //entry
      pstmt.setDouble(4, paperTrade.getEntry());
      //stop loss
      pstmt.setDouble(5, paperTrade.getStopLoss());
      //stop risk
      pstmt.setDouble(6, paperTrade.getStopRisk());
      //Stop profit
      pstmt.setDouble(7, paperTrade.getProfitStop());
      //profitpotential
      pstmt.setDouble(8, paperTrade.getProfitpotential());
      //Outcome
      pstmt.setDouble(9, paperTrade.getOutcome());
      //ExitTradeDateTime
      pstmt.setTimestamp(10, new Timestamp(paperTrade.getExitTradeDateTime().getTime()));
      pstmt.execute();
      pstmt.close();
    } catch (SQLException ex) {
      System.err.println("EXCEPTION: " + ex.getMessage());
    } finally {
    }
  }

  public static int getNextPaperOrderID() {
    int id = -1;
    try {
      PreparedStatement pstmt =
              PtsDBops.setuptradesConnection().
              prepareStatement("SELECT max(OrderID) FROM PaperOrders");
      ResultSet res = pstmt.executeQuery();
      res.next(); //To get a lastexpiry for loop, so should be one extra early expiry
      id = res.getInt(1);
    } catch (SQLException ex) {
      System.err.println("SQLException in playItForward(): " + ex.getMessage());
    } finally {
      return (id + 1);
    }
  }

  public static CallableStatement playItForwardBySymbol(String sym, Timestamp beginDate, double high, double low) {
    CallableStatement ret = null;

    try {
      String callStr = "select * from playitforward('" + sym + "', '" + beginDate + "', '"
              + high + "', '" + low + "');";
      ret = PtsDBops.setuptradesConnection().
              prepareCall(callStr, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "SQLException", JOptionPane.ERROR_MESSAGE);
    } finally {
      return ret;
    }
  }

  public static void playItForward(PtsPaperTrade pt, PtsSymbolInfo symInfo) {
    try {
      double stopLossPrice = symInfo.getActualPriceFromExpandedPrice(pt.getStopLoss());
      double profitPrice = symInfo.getActualPriceFromExpandedPrice(pt.getProfitStop());
      double lowPrice;
      double highPrice;
      boolean buyTrade = true;
      if (stopLossPrice > profitPrice) { // A Short trade
        buyTrade = false;
        lowPrice = profitPrice;
        highPrice = stopLossPrice;
      } else { // Long trade
        lowPrice = stopLossPrice;
        highPrice = profitPrice;
      }
      String ul = pt.getSymbol();
      java.util.Date beginTradeDT = pt.getBeginTradeDateTime();
      PreparedStatement pstmt =
              PtsDBops.playItForwardBySymbol(ul, new Timestamp(beginTradeDT.getTime()), highPrice, lowPrice);
      ResultSet res = pstmt.executeQuery();
      boolean ret = res.next();
      pt.setExitTradeDateTime(res.getTimestamp("datetime"));
      Double high = symInfo.getExpandedPriceActualPrice(res.getDouble("high"));
      Double low = symInfo.getExpandedPriceActualPrice(res.getDouble("low"));
      if (buyTrade) { // Long trade
        if (high >= pt.getProfitStop()) {
          pt.setOutcome(pt.getProfitpotential());
        } else if (low <= pt.getStopLoss()) {
          pt.setOutcome(pt.getStopRisk());
        }
      } else { // Short trade
        if (high >= pt.getStopLoss()) {
          pt.setOutcome(pt.getStopRisk());
        } else if (low <= pt.getProfitStop()) {
          pt.setOutcome(pt.getProfitpotential());
        }

      }

    } catch (SQLException ex) {
      System.err.println("SQLException in playItForward(): " + ex.getMessage());
    }
  }

  public static ArrayList getPaperTrades() {
    ArrayList paperTrades = new ArrayList<PtsPaperTrade>();
    try {
      PreparedStatement pstmt = PtsDBops.setuptradesConnection().
              prepareStatement("select * from papertrades order by enteredindb desc, symbol, begintradedatetime;");
      ResultSet res = pstmt.executeQuery();
      while (res.next()) {
        PtsPaperTrade trade = new PtsPaperTrade();
        trade.setId(res.getInt("id"));
        trade.setEnteredInDB(res.getTimestamp("enteredindb"));
        trade.setBeginTradeDateTime(res.getTimestamp("begintradedatetime"));
        trade.setEntry(res.getDouble("entry"));
        trade.setExitTradeDateTime(res.getTimestamp("exittradedatetime"));
        trade.setOutcome(res.getDouble("outcome"));
        trade.setPosition(res.getString("position"));
        trade.setProfitStop(res.getDouble("stopprofit"));
        trade.setProfitpotential(res.getDouble("profitpotential"));
        trade.setStopLoss(res.getDouble("stoploss"));
        trade.setStopRisk(res.getDouble("stoprisk"));
        trade.setSymbol(res.getString("symbol"));
        paperTrades.add(trade);
      }
    } catch (SQLException ex) {
      System.err.println("SQLException in getPaperTrades(): " + ex.getMessage());
    } finally {
      return paperTrades;
    }
  }

  public static void main(String[] args) {
    try {
      PtsDBops.setuptradesConnection();
      PtsDBops.getPaperTrades();
      Connection con = PtsDBops.setuptradesConnection();
      con.setAutoCommit(false);
      String ul = "AUD";
      java.util.Date dd = new java.util.Date(110, 00, 03);
      PreparedStatement pstmt = PtsDBops.playItForwardBySymbol(ul, new Timestamp(dd.getTime()), 0.90, 0.88);
      ResultSet res = pstmt.executeQuery();
      boolean ret = res.next();
      Date dout = res.getDate("datetime");
      Double open = res.getDouble("open");
      Double high = res.getDouble("high");
      Double low = res.getDouble("low");
      Double close = res.getDouble("close");
      int vol = res.getInt("volume");
      con.close();

    } catch (SQLException ex) {
      System.err.println("EXCEPTION: " + ex.getMessage());
    }


  }
}


