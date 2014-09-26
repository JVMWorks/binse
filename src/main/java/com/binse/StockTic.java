package com.binse;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.criterion.Restrictions;


@Entity
@Table(name = "stock_tic")
public class StockTic {

    public static final String AVG_HIGH_PRICE = "AVG-HIGH-PRICE";
    public static final String AVG_LOW_PRICE = "AVG-LOW-PRICE";
    public static final String AVG_LAST_TRADED_PRICE = "AVG-LAST-TRADED-PRICE";
    public static final String AVG_OPEN_PRICE = "AVG-OPEN-PRICE";
    public static final String AVG_CLOSE_PRICE = "AVG-CLOSE-PRICE";
    public static final String AVG_TURNOVER = "AVG-TURNOVER";
    public static final String AVG_TOTAL_TRADED_QUANTITY = "AVG-TOTAL-TRADED-QUANTITY";

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
    
    @Transient
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
	
	public StockTic() {	}
	
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

	@SuppressWarnings("unchecked")
	public static Map<String, Object> stats(Date from, Date to, String code) {
		Criteria criteria = getSession().createCriteria(StockTic.class);
		criteria.add(Restrictions.eq("name", code));
		criteria.add(Restrictions.ge("date", from));
		criteria.add(Restrictions.le("date", to));
		List<StockTic> stocks = null;
		try {
			stocks = (List<StockTic>) criteria.list();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			getSession().close();
		}
		return computeAverages(stocks);
	}
	
	private static Map<String, Object> computeAverages(List<StockTic> stocks) {
		float sumHighPrice=0, sumLowPrice=0, sumLastTradedPrice=0, sumOpenPrice=0, sumClosePrice=0, sumTurnover=0;
		BigDecimal sumTotalTradedQuantity= new BigDecimal(0);
		
		HashMap<String, Object> hm = new HashMap<String, Object>();
		for(StockTic stock : stocks) {
			sumHighPrice +=stock.getHighPrice(); 
			sumLowPrice += stock.getLowPrice();
			sumLastTradedPrice += stock.getLastTradedPrice();
			sumOpenPrice += stock.getOpenPrice();
			sumClosePrice += stock.getClosePrice();
			sumTotalTradedQuantity = sumTotalTradedQuantity.add(new BigDecimal(stock.getTotalTradedQuantity()));
			sumTurnover += stock.getTurnover();
		}
		System.out.println("====>" + sumTotalTradedQuantity);
		int size = stocks.size();
		if(size==0) size = 1; //To avoid Division-By-Zero Error
		hm.put(AVG_HIGH_PRICE, (sumHighPrice/size));
		hm.put(AVG_LOW_PRICE, (sumLowPrice/size));
		hm.put(AVG_LAST_TRADED_PRICE, (sumLastTradedPrice/size));
		hm.put(AVG_OPEN_PRICE, (sumOpenPrice/size));
		hm.put(AVG_CLOSE_PRICE, (sumClosePrice/size));
		hm.put(AVG_TOTAL_TRADED_QUANTITY, sumTotalTradedQuantity.divide(new BigDecimal(size)).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		hm.put(AVG_TURNOVER, (sumTurnover/size));
		return hm;
	}

	private static Session getSession() {
		return HibernateUtil.getSession();
	}
	
}
