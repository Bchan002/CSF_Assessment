package vttp.batch5.csf.assessment.server.repositories;

import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


@Repository
public class OrdersRepository {

  @Autowired
  private MongoTemplate template;




  // TODO: Task 2.2
  // You may change the method's signature
  // Write the native MongoDB query in the comment below
  //
  //  Native MongoDB query here

  //  db.menus.find({}).sort({"name":1})

  public Optional<List<Document>> getMenu() {

      Criteria criteria = new Criteria(); 

      Query query = Query.query(criteria)
        .with(Sort.by(Sort.Direction.ASC,"name"));

      List<Document> results = template.find(query,Document.class,"menus");

      return Optional.ofNullable(results);

  }

  // TODO: Task 4
  // Write the native MongoDB query for your access methods in the comment below
  //
  //  Native MongoDB query here
//   db.orders.insert({
//     // document data
// });
  public void saveToMongo(Document doc){
    
    try {
      template.insert(doc,"orders");

    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    }
   

  }
}
