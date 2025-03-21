package vttp.batch5.csf.assessment.server.GlobalExceptionHandler;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vttp.batch5.csf.assessment.server.models.OrdersNotFoundException;
import vttp.batch5.csf.assessment.server.models.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(OrdersNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(OrdersNotFoundException ex, 
    HttpServletRequest request, HttpServletResponse response){

        //ApiError apiError = new ApiError(404, ex.getMessage(), new Date(), request.getRequestURI());

        JsonObject apiErrorJson = Json.createObjectBuilder() 
            .add("status", 404)
            .add("message", ex.getMessage())
            .add("timeStamp", new Date().toString())
            .add("path", request.getRequestURI())
            .build();


        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorJson.toString());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(UserNotFoundException ex, 
    HttpServletRequest request, HttpServletResponse response){

        //ApiError apiError = new ApiError(404, ex.getMessage(), new Date(), request.getRequestURI());

        JsonObject apiErrorJson = Json.createObjectBuilder() 
            .add("status", 401)
            .add("message", ex.getMessage())
            .add("timeStamp", new Date().toString())
            .add("path", request.getRequestURI())
            .build();


        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorJson.toString());
    }
}
