package com.sapient.trade.service;

import com.sapient.trade.dto.Brokerage;

import java.util.Map;

public interface BrokerageCalculator {

    Map<String,Brokerage> calculateBrokerage(final String tradeBookPath) throws Exception;

}
