package com.example.training;

import java.math.RoundingMode;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

public class BigDecimalDemo {

    public static void main(String[] args) {
        BigDecimal netPrice = new BigDecimal("99.99");
        BigDecimal vatRate = new BigDecimal("0.19");
        BigDecimal vat = netPrice.multiply(vatRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal grossPrice = netPrice.add(vat);

        System.out.println("Net price : " + netPrice);
        System.out.println("VAT (19%): " + vat);
        System.out.println("Gross     : " + grossPrice);

        StringBuilder builder = new StringBuilder();
        builder.append("Order summary: gross price = ");
        builder.append(grossPrice);

        System.out.println(builder);

        OrderStatus status = OrderStatus.PROCESSING;
        System.out.println("Order status: " + status);

        List<OrderStatus> statuses = new ArrayList<OrderStatus>();
        statuses.add(OrderStatus.NEW);
        statuses.add(OrderStatus.PROCESSING);
        statuses.add(OrderStatus.DONE);

        System.out.println("Possible statuses: " + statuses);
    }

    private enum OrderStatus {
        NEW,
        PROCESSING,
        DONE
    }
}
