package vttp.batch5.csf.assessment.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import vttp.batch5.csf.assessment.server.services.RestaurantService;

@RestController
@RequestMapping("/api")
public class RestaurantController {

  @Autowired
  private RestaurantService restaurantSvc;


  // TODO: Task 2.2
  // You may change the method's signature
  @GetMapping("/menu")
  public ResponseEntity<String> getMenus() {

    JsonArray menuArray = restaurantSvc.getMenu();

    return ResponseEntity.ok(menuArray.toString());
  }

  // TODO: Task 4
  // Do not change the method's signature
  @PostMapping("/food_order")
  public ResponseEntity<String> postFoodOrder(@RequestBody String payload) {

    

    System.out.println("recieved your order " + payload);

    //Send to service 
    JsonObject response = restaurantSvc.authenticateUser(payload);

    //Succesful alr thn go call Rest APi 

    return ResponseEntity.ok(response.toString());
  }
}
