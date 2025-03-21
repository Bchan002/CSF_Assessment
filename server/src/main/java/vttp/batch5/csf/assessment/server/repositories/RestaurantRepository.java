package vttp.batch5.csf.assessment.server.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttp.batch5.csf.assessment.server.models.OrdersNotFoundException;
import vttp.batch5.csf.assessment.server.models.PlaceOrders;
import vttp.batch5.csf.assessment.server.models.UserNotFoundException;
import vttp.batch5.csf.assessment.server.utils.sql;

// Use the following class for MySQL database
@Repository
public class RestaurantRepository {

    @Autowired
    private JdbcTemplate template;

    public void authenticateUser(String username, String password){

        System.out.println("authenticate user...");
        try {
            SqlRowSet sqlRowSet = template.queryForRowSet(sql.CHECK_DATABASE,username,password);
            
            if(!sqlRowSet.next()){
                throw new UserNotFoundException("Invalid username and/or password");
            }
        } catch (Exception e) {
            // TODO: handle exception
            throw new UserNotFoundException("Invalid username and/or password");
        }
       
    }


    public Boolean savePaymentDetails(PlaceOrders orderDetails){
        
        System.out.println("Adding orderDetails into database... ");

        try {
            int saved = template.update(sql.INSERT_PAYMENT_DETAILS, 
            orderDetails.getOrderId(),orderDetails.getPaymentId(), orderDetails.getOrderDate(),orderDetails.getTotal(), 
            orderDetails.getUsername());
    
            if(saved>0){
                return true;
            }
    
            return false;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new OrdersNotFoundException("Cannot save orders into database Error");
        }
    }
}
