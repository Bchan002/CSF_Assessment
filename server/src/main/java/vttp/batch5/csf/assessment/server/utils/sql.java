package vttp.batch5.csf.assessment.server.utils;

public class sql {
    
    public static final String CHECK_DATABASE = """
            SELECT * FROM customers WHERE username=? AND password=?;
            """;

    public static final String INSERT_PAYMENT_DETAILS = """
            INSERT into place_orders (order_id,payment_id,order_date,total,username) values (?,?,?,?,?);
            """;
}
