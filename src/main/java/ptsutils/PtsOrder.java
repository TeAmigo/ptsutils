/*
 *  Copyright (C) 2010 Rick Charon
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ptsutils;

import java.util.Date;

/**
 * 12/31/10 3:36 PM Changed the definition in DB
 * Order.java Created Aug 25, 2010 4:23:55 PM in Project PeTraSys
 *  
 * @author Rick Charon
 * 
 */
public class PtsOrder {
//   idx serial NOT NULL,
//   ul character varying(12) NOT NULL,
//   ordertype paperordertype NOT NULL,
//   price numeric NOT NULL DEFAULT 0::numeric,
//   translatedprice numeric NOT NULL,
//   bartime timestamp without time zone NOT NULL,
//   lossorgain numeric,
//   orderid integer NOT NULL,
//   parentid integer,
//   entrytimestamp timestamp without time zone NOT NULL,

  private Integer idx = null;
  private String ul = null;
  private String orderType = null;
  private Double price = 0.0;
  private Double translatedPrice = null;
  private Date barTime = null;
  private Double lossOrGain = null;
  private Integer orderID = null;
  private Integer parentID = null;
  private Date entryDateTime = null;

  public Date getBarTime() {
    return barTime;
  }

  public void setBarTime(Date barTime) {
    this.barTime = barTime;
  }

  public Date getEntryDateTime() {
    return entryDateTime;
  }

  public void setEntryDateTime(Date entryDateTime) {
    this.entryDateTime = entryDateTime;
  }

  public Integer getIdx() {
    return idx;
  }

  public Double getLossOrGain() {
    return lossOrGain;
  }

  public void setLossOrGain(Double lossOrGain) {
    this.lossOrGain = lossOrGain;
  }

  public Integer getOrderID() {
    return orderID;
  }

  public void setOrderID(Integer orderID) {
    this.orderID = orderID;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public Integer getParentID() {
    return parentID;
  }

  public void setParentID(Integer parentID) {
    this.parentID = parentID;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getTranslatedPrice() {
    return translatedPrice;
  }

  public void setTranslatedPrice(Double translatedPrice) {
    this.translatedPrice = translatedPrice;
  }

  public String getUl() {
    return ul;
  }

  public void setUl(String ul) {
    this.ul = ul;
  }



  public PtsOrder() {
  }

  

}
