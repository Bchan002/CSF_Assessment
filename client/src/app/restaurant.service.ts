import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { inject } from "@angular/core";
import { LineItems, menuItems, paymentReceipt, submitOrder } from "./models";
import { BehaviorSubject, catchError, Observable, Subject, tap, throwError } from "rxjs";

export class RestaurantService {

    //Inject http client first 
    private http = inject(HttpClient)
    
    private GET_MENU_ITEMS = "/api/menu"
    private POST_ORDER = "/api/food_order"


    //Create a Subject service to populate view 2 
    lineItems =  new BehaviorSubject<LineItems[]>([])
    paymentReceipt = new BehaviorSubject<paymentReceipt>({} as paymentReceipt); // or provide an initial value


  // TODO: Task 2.2
  // You change the method's signature but not the name
  getMenuItems():Observable<menuItems[]>{

    return this.http.get<menuItems[]>(this.GET_MENU_ITEMS).pipe(
      tap(result=>{
        console.info("This is your result ", result)
      })
    )
  }

  sendLineItems(lineItems: LineItems[]): void {
    //Send as observable 
    console.info("Sending items over..")
    this.lineItems.next(lineItems)
  }

  // TODO: Task 3.2
  sendOrder(submitOrder:submitOrder): Observable<paymentReceipt>{

    return this.http.post<paymentReceipt>(this.POST_ORDER,submitOrder).pipe(
      tap(result=>{
        console.info("This is your response",result)
        this.paymentReceipt.next(result)
      }), 
      catchError(this.handleError)
    )
  }




  private handleError(error: HttpErrorResponse): Observable<any> {

    let errorMessage  = ""
    
    if(error.status==0){

        // Client side error 
        console.error("An error occured ", error.error)
    } else {

        // Server side error 
        // To access the custom message from your ApiError JSON object 
        if(error.status==404){
            errorMessage = error.error.message
        } 

        if(error.status==401){
            errorMessage = error.error.message
        }

        if(error.status==500){
          errorMessage = "Error"
        }
    }   
    
    // return an observabele with a user facing error message 
    return throwError( () => errorMessage) 

}
}
