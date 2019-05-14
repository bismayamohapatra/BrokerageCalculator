package com.sapient.trade.service;

import com.sapient.trade.dto.Brokerage;
import com.sapient.trade.dto.Trade;
import com.sapient.trade.exception.FileParsingException;
import com.sapient.trade.utils.BrokerageUtility;
import com.sapient.trade.utils.TransactionType;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 *
 *  @author  Bismaya Mohapatra
 * @version 1.0
 * @since   14-05-2019
 */
public class CsvBrokerageCalculator implements BrokerageCalculator{

    private static CsvBrokerageCalculator instance = null;
    private static final Logger logger = Logger.getLogger(CsvBrokerageCalculator.class);
    private static final ResourceBundle bundle = ResourceBundle.getBundle("config");
    private static CopyOnWriteArrayList<Trade> tradeList = new CopyOnWriteArrayList();
    private static List<Trade> intraday = new ArrayList<>();
    private Map<String,Brokerage> brokerages = new HashMap<>();

    private CsvBrokerageCalculator() {
    }

    public static CsvBrokerageCalculator getInstance() {
        if (instance == null) {
            synchronized (CsvBrokerageCalculator.class) {
                if (instance == null) {
                    instance = new CsvBrokerageCalculator();
                }
            }
        }
        return instance;
    }

    public Map<String,Brokerage> calculateBrokerage(String tradeBookPath)throws Exception{

        if(BrokerageUtility.isNullOrEmpty(tradeBookPath) || BrokerageUtility.isNullOrEmpty(tradeBookPath)){
            throw new FileParsingException("Failed to parse the file with provided parameters File Path : " + tradeBookPath );
        }

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            br = new BufferedReader(new FileReader(tradeBookPath));

            String headerLine = br.readLine();
            while ((line = br.readLine()) != null) {
                Trade trade = new Trade();
                String[] tradebook = line.split(cvsSplitBy);
                trade.setExternalTransactionId(tradebook[0]);
                trade.setClientId(tradebook[1]);
                trade.setSecurityId(tradebook[2]);
                trade.setTransactionType(TransactionType.valueOf(tradebook[3]));
                trade.setTransactionDate(new Date(tradebook[4]));
                trade.setMarketValue(Float.valueOf(tradebook[5]));
                trade.setPriority(tradebook[6]);
                tradeList.add(trade);
            }

            extractIntraDayTrades();

            calculateIntradayFee(brokerages);

            calculateDeliveryFee(brokerages);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return brokerages;
    }

    /**
     *
     * @param t1
     * @param t2
     * @return
     */
    private static boolean checkIntraday (Trade t1, Trade t2) {
        if(t1.getClientId().equalsIgnoreCase(t2.getClientId()) && t1.getSecurityId().equalsIgnoreCase(t2.getSecurityId())) {
            if (t1.getTransactionDate().compareTo(t1.getTransactionDate()) == 0){
                if ((t1.getTransactionType().toString().equalsIgnoreCase("BUY") && t2.getTransactionType().toString().equalsIgnoreCase("SELL"))
                        || (t1.getTransactionType().toString().equalsIgnoreCase("SELL") && t2.getTransactionType().toString().equalsIgnoreCase("BUY"))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param brokerages
     */
    private static void calculateDeliveryFee(Map<String, Brokerage> brokerages) {
        logger.info("Calculating delivery fee");
        for(Trade trade : tradeList){
            if(brokerages.containsKey(trade.getClientId())){
                Brokerage brokerage = brokerages.get(trade.getClientId());
                Long processingFee = brokerage.getProcessingFee();
                CalcBrokerageAmount(trade, brokerage,processingFee,false);
                brokerage.setProcessingFee(processingFee);
            }else {
                Brokerage brokerage = getBasicBrokerage(trade);
                CalcBrokerageAmount(trade, brokerage,0L,false);
                brokerages.put(trade.getClientId(),brokerage);
            }
        }
    }

    private static void calculateIntradayFee(Map<String, Brokerage> brokerages) {
        logger.info("Calculating intra day fee");
        for(Trade trade : intraday){
            if(brokerages.containsKey(trade.getClientId())){
                Brokerage brokerage = brokerages.get(trade.getClientId());
                CalcBrokerageAmount(trade, brokerage,brokerage.getProcessingFee(),true);
            }else {
                Brokerage brokerage = getBasicBrokerage(trade);
                brokerage.setProcessingFee(Long.valueOf(bundle.getString("INTRADAY_FEE")));
                brokerages.put(trade.getClientId(),brokerage);
            }
        }
    }

    private static void extractIntraDayTrades() {
        logger.info("Extracting intraday trades");
        for(int i=0;i<tradeList.size()-1;i++){
            for(int j=i+1;j<tradeList.size();j++) {
                boolean result = checkIntraday(tradeList.get(i), tradeList.get(j));
                if (result) {
                    intraday.add(tradeList.get(i));
                    intraday.add(tradeList.get(j));
                    tradeList.remove(tradeList.get(i));
                    tradeList.remove(tradeList.get(j));
                }
            }
        }
    }

    /**
     *
     * @param trade
     * @return
     */
    private static Brokerage getBasicBrokerage(Trade trade) {
        Brokerage brokerage = new Brokerage();
        brokerage.setClientId(trade.getClientId());
        brokerage.setTransactionType(trade.getTransactionType());
        brokerage.setTransactionDate(trade.getTransactionDate());
        brokerage.setPriority(trade.isPriority());
        return brokerage;
    }

    /**
     *
     * @param trade
     * @param brokerage
     * @param processingFee
     * @param isIntraDay
     */
    private static void CalcBrokerageAmount(Trade trade, Brokerage brokerage, Long processingFee, boolean isIntraDay) {
        if(isIntraDay){
            brokerage.setProcessingFee(processingFee + Long.valueOf(bundle.getString("INTRADAY_FEE")));
        }else if(trade.isPriority().equalsIgnoreCase("Y")){
            brokerage.setProcessingFee(processingFee + Long.valueOf(bundle.getString("NON_INTRADAY_PRIORITY_FEE")));
        }else {
            if((trade.getTransactionType().toString().equalsIgnoreCase("SELL")) ||  (trade.getTransactionType().toString().equalsIgnoreCase("WITHDRAW")) ){
                brokerage.setProcessingFee(processingFee + Long.valueOf(bundle.getString("NON_INTRADAY_NON_PRIORITY_SELL_FEE")));
            }else{
                brokerage.setProcessingFee(processingFee + Long.valueOf(bundle.getString("NON_INTRADAY_NON_PRIORITY_BUY_FEE")));
            }
        }
    }

}
