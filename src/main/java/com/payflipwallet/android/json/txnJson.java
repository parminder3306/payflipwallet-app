package com.payflipwallet.android.json;

/**
 * Created by Janny Samra.
 */

public class txnJson {
    String  orderid,status,cashback,operator,symbol,amount,date,time,details,type;

    public String getType() {
        return type;
    }
    public String getOrderid() {
        return orderid;
    }
    public String getStatus() {
        return status;
    }
    public String getCashback() {
        return cashback;
    }
    public String getOperator() {
        return operator;
    }
    public String getSymbol() {
        return symbol;
    }
    public String getAmount() {
        return amount;
    }
    public String getDetails() {
        return details;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
}
