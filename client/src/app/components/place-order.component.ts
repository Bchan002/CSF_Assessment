import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { RestaurantService } from '../restaurant.service';
import { Subscription } from 'rxjs';
import { LineItems, submitOrder } from '../models';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-place-order',
  standalone: false,
  templateUrl: './place-order.component.html',
  styleUrl: './place-order.component.css'
})
export class PlaceOrderComponent implements OnInit, OnDestroy {

  private restaurantSvc = inject(RestaurantService)
  private router = inject(Router)
  private fb = inject(FormBuilder)
  protected form!:FormGroup
  protected lineItemInfo!: LineItems[]
  private lineItemSub!:Subscription
  private sendOrderSub!:Subscription

  protected totalCartPrice!:number
  // TODO: Task 3

  ngOnInit(): void {
      
    //Subscribe to the service as observable 
      this.lineItemSub = this.restaurantSvc.lineItems.subscribe({
        next: (data)=>{
          console.info("Here is your data ", data)
          this.lineItemInfo = data
          this.totalCartPrice = data[0].totalCartPrice
        }
      }
    )

    this.form = this.createForm()
    

  }


  submitOrder():void {

      const username = this.form.controls['username']?.value
      const password = this.form.controls['password']?.value

      const sendOrder: submitOrder = {
        username:username,
        password:password,
        lineItems: this.lineItemInfo
    
      }

      //Send order to svc subscribe 
      this.sendOrderSub = this.restaurantSvc.sendOrder(sendOrder).subscribe({
        next:(response)=>{
          console.info("Here is your response")
          this.router.navigate(['/confirmation'])
        }, 
        error: (error)=>{
          console.info("here is your error ", error)
          alert(error)
        }
      })

  }

  startOver():void {
    
    //Discard all item selection 
    this.form.reset()

    this.router.navigate(['/'])

  }

  isCtrlInvalid(ctrlName: string):boolean {
    return !!this.form.get(ctrlName)?.invalid
  }

  disabled():boolean {
    return !!this.form?.invalid
  }

  createForm():FormGroup{
    return this.fb.group({
      username: this.fb.control<string>("",Validators.required),
      password: this.fb.control<string>("",Validators.required)
    })
  }
  ngOnDestroy(): void {
      this.lineItemSub?.unsubscribe()
  }
}
