package com.sapient.trade.main;

import com.sapient.trade.dto.Brokerage;
import com.sapient.trade.service.BrokerageCalculator;
import com.sun.xml.internal.ws.util.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.ResourceBundle;


/**
 *
 *  @author  Bismaya Mohapatra
 * @version 1.0
 * @since   14-05-2019
 */
public class TradeCalculationApplication {

   private static final Logger logger = Logger.getLogger(TradeCalculationApplication.class);
   private static ResourceBundle bundle = ResourceBundle.getBundle("config");
   private static BrokerageCalculator brokerageCalculator;
   public static void main(String[] args)throws Exception {

       logger.info("Calculating processing fee for trade book ");

       String fileExtension = FilenameUtils.getExtension(bundle.getString("FILE_NAME"));
       String className = "com.sapient.trade.service." + StringUtils.capitalize(fileExtension) + "BrokerageCalculator";
       try {
           Class c = Class.forName(className);
           Method factoryMethod = c.getDeclaredMethod("getInstance");
           brokerageCalculator = (BrokerageCalculator) factoryMethod.invoke(null, null);
       }catch (ClassNotFoundException e){
           logger.error("Implementation is not present for provided file type of file. Please provide files with .Csv/.Xml");
           throw e;
       }

       Map<String,Brokerage> brokerages = brokerageCalculator.calculateBrokerage(bundle.getString("FILE_NAME"));

       brokerages.forEach((k,v)->System.out.println("Client ID : " + k + " Processing Fee : " + v.getProcessingFee()));

   }




}
