package com.binse;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;


//@DynamicUpdate
@Entity
@Table(name = "stock_tic")
public class StockTic {

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy="increment")
    private Long stockTicId; //DB only

    @NotNull
    private String name;
    
    @NotNull
	private Date date;
    
    @NotNull
	private float openPrice;
	
    @NotNull
    private float highPrice;
    
    @NotNull
	private float lowPrice;
    
    @NotNull
	private float lastTradedPrice;
    
    @NotNull
	private float closePrice;
    
    @NotNull
	private long totalTradedQuantity;
    
    @NotNull
	private float turnover;
    
	SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
	
	public StockTic(String name, Date date, float openPrice, float highPrice,
			float lowPrice, float lastTradedPrice, float closePrice,
			long totalTradedQuantity, float turnover) {
		super();
		this.name = name;
		this.date = date;
		this.openPrice = openPrice;
		this.highPrice = highPrice;
		this.lowPrice = lowPrice;
		this.lastTradedPrice = lastTradedPrice;
		this.closePrice = closePrice;
		this.totalTradedQuantity = totalTradedQuantity;
		this.turnover = turnover;
	}
	
	public String toString() {
		return "Date: " + dateFormatter.format(date) + 
				", Open Price: " + openPrice + 
				", High Price: " + highPrice + 
				", Last Traded Price: " + lastTradedPrice +
				", Close Price: " + closePrice +
				", Total Traded Quantity: " + totalTradedQuantity +
				", Turnover in lakhs: " + turnover + 
				", code: " + name;
	}
	
	public Date getDate() {
		return date;
	}

	public float getOpenPrice() {
		return openPrice;
	}

	public float getHighPrice() {
		return highPrice;
	}

	public float getLowPrice() {
		return lowPrice;
	}

	public float getLastTradedPrice() {
		return lastTradedPrice;
	}

	public float getClosePrice() {
		return closePrice;
	}
	
	public long getTotalTradedQuantity() {
		return totalTradedQuantity;
	}

	public float getTurnover() {
		return turnover;
	}

	
}
