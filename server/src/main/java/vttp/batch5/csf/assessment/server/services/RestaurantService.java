package vttp.batch5.csf.assessment.server.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.batch5.csf.assessment.server.models.OrdersNotFoundException;
import vttp.batch5.csf.assessment.server.models.PlaceOrders;
import vttp.batch5.csf.assessment.server.models.productItems;
import vttp.batch5.csf.assessment.server.repositories.OrdersRepository;
import vttp.batch5.csf.assessment.server.repositories.RestaurantRepository;

@Service
public class RestaurantService {

  @Autowired
  private OrdersRepository ordersRepo;

  @Autowired
  private RestaurantRepository restaurantRepo;

  private String POST_URL = "https://payment-service-production-a75a.up.railway.app/api/payment";

  // TODO: Task 2.2
  // You may change the method's signature
  public JsonArray getMenu() {

    Optional<List<Document>> docs = ordersRepo.getMenu();

    // Convert to JSON array
    JsonArrayBuilder menuArray = Json.createArrayBuilder();
    if (docs.isPresent()) {

      List<Document> doc2 = docs.get();

      doc2.stream()
          .map(a -> {

            // Create a jsonObject
            JsonObject object = Json.createObjectBuilder()
                .add("id", a.getString("_id"))
                .add("name", a.getString("name"))
                .add("price", a.getDouble("price"))
                .add("description", a.getString("description"))
                .build();

            return object;
          })
          .forEach(b -> menuArray.add(b));

      return menuArray.build();

    }

    throw new OrdersNotFoundException("Menu items cannot be retrieved");

  }

  // TODO: Task 4
  public JsonObject authenticateUser(String payload) {

    // Read the String and convert to JSON Object
    /**
     * {"username":"kdcmdm","password":"dcldc",
     * " lineItems":[{"id":"9aedc2a8","name":"Balik Ekmek","price":9.2,
     * "quantity":4,"totalPrice":36.8,"totalCartPrice":67.6},
     * {"id":"b9f0f5e1","name":"Chicken Bruschetta","price":7.7,
     * "quantity":4,"totalPrice":30.8,"totalCartPrice":67.6}]}
     */

    Reader reader = new StringReader(payload);
    JsonReader jsonReader = Json.createReader(reader);

    JsonObject jsonObject = jsonReader.readObject();
    String username = jsonObject.getString("username");
    String password = jsonObject.getString("password");
    System.out.println("Your username " + username);
    System.out.println("Your password " + password);

    restaurantRepo.authenticateUser(username, password);

    // Sucessfully authenticated

    // generate 8 random character string
    String orderId = UUID.randomUUID().toString().substring(0, 8);

    // Get the totalPrice
    double totalPrice = jsonObject.getJsonArray("lineItems")
        .getJsonObject(0)
        .getJsonNumber("totalCartPrice")
        .doubleValue();

    List<productItems> productItemsList = new ArrayList<>();
    // Get the list of productItems
    JsonArray lineItems = jsonObject.getJsonArray("lineItems");
    for (int i = 0; i < lineItems.size(); i++) {
      JsonObject product = lineItems.getJsonObject(i);
      productItems item = new productItems();
      item.setId(product.getString("id"));
      item.setPrice(product.getJsonNumber("price").doubleValue());
      item.setQuantity(product.getJsonNumber("quantity").intValue());

      productItemsList.add(item);

    }

    // Make payment
    JsonObject paymentDetails = makePayment(totalPrice, orderId, username);

    /*
     * //{"payment_id":"01JPVNFCDJQEPTJETA8JYS9A9Q","order_id":"70b4d6e","timestamp"
     * :1742537733226,"total":67.5999984741211}
     */
    // Create a new PlaceOrder object
    PlaceOrders orderDetails = new PlaceOrders();
    orderDetails.setProductItems(productItemsList);
    orderDetails.setOrderId(orderId);
    orderDetails.setPaymentId(paymentDetails.getString("payment_id"));

    // Format the date
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String formattedDate = dateFormat.format(new Date(paymentDetails.getJsonNumber("timestamp").longValue()));
    System.out.println("formatted Date " + formattedDate);
    orderDetails.setOrderDate(formattedDate);

    double total = Math.round(paymentDetails.getJsonNumber("total").doubleValue() * 100.0) / 100.0;
    System.out.println("formatted total " + total);
    orderDetails.setTotal(total);
    orderDetails.setUsername(username);

    // Send to mySQL Database
    saveDetails(orderDetails);

    JsonObject responseObject = Json.createObjectBuilder()
      .add("orderId",orderDetails.getOrderId())
      .add("paymentId",orderDetails.getPaymentId())
      .add("total",orderDetails.getTotal())
      .add("timeStamp",orderDetails.getOrderDate())
      .build();

    return responseObject;

  }

  @Transactional
  public void saveDetails(PlaceOrders orderDetails) {

    // Save to mySql Database
    restaurantRepo.savePaymentDetails(orderDetails);

    // Save to mongoDB
    Document orders = convertToDocument(orderDetails);
    ordersRepo.saveToMongo(orders);


  }

  public Document convertToDocument(PlaceOrders orderDetails) {

    // Create a document
    Document doc = new Document()
        .append("_id", orderDetails.getOrderId())
        .append("order_id", orderDetails.getOrderId())
        .append("payment_id", orderDetails.getPaymentId())
        .append("username", orderDetails.getUsername())
        .append("total", orderDetails.getTotal())
        .append("timeStamp", orderDetails.getOrderDate())
        .append("productItems", List.of(
            orderDetails.getProductItems().stream()
                .map(item -> new Document()
                    .append("id", item.getId())
                    .append("price", item.getPrice())
                    .append("quantity", item.getQuantity()))
                .collect(Collectors.toList())));

      return doc;

  }

  public JsonObject makePayment(double totalPrice, String orderId, String username) {

    // Create a jsonObject
    JsonObject paymentJson = Json.createObjectBuilder()
        .add("order_id", orderId)
        .add("payer", username)
        .add("payee", "Benjamin Chan")
        .add("payment", totalPrice)
        .build();

    RequestEntity<String> req = RequestEntity
        .post(POST_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .header("X-Authenticate", username)
        .body(paymentJson.toString(), String.class);

    // Step 2: Create a RestTemplate
    RestTemplate template = new RestTemplate();

    // Step 3: Configure the response
    ResponseEntity<String> resp;

    // Step 4: Send the request and get the resposne as a String
    resp = template.exchange(req, String.class);

    /// If required can check for status code 
    // Check for errors
    if (resp.getStatusCode().is4xxClientError()) {
      throw new OrdersNotFoundException("Authentication failed");
    }

    // Step 5: Extract the payload (JSON String format)
    String payload = resp.getBody();

    // Step 6: Read the JSON format payload
    Reader reader = new StringReader(payload);
    JsonReader jsonReader = Json.createReader(reader);

    JsonObject paymentDetails = jsonReader.readObject();

    // Works
    System.out.println(paymentDetails.toString());

    return paymentDetails;

  }

}
