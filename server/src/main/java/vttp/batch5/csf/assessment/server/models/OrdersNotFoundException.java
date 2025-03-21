package vttp.batch5.csf.assessment.server.models;



public class OrdersNotFoundException extends RuntimeException{
    
    public OrdersNotFoundException(){

        super();
    }

    public OrdersNotFoundException(String message){

        super(message);
    }

    public OrdersNotFoundException(String message, Throwable cause){

        super(message, cause);
    }
}
