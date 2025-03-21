import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { RestaurantService } from '../restaurant.service';
import { Subscription } from 'rxjs';
import { paymentReceipt } from '../models';

@Component({
  selector: 'app-confirmation',
  standalone: false,
  templateUrl: './confirmation.component.html',
  styleUrl: './confirmation.component.css'
})
export class ConfirmationComponent implements OnInit,OnDestroy {

  private restaurantSvc = inject(RestaurantService)

  private sub!:Subscription
  protected paymentReceipt!:paymentReceipt

  // TODO: Task 5
  ngOnInit(): void {

    this.sub = this.restaurantSvc.paymentReceipt.subscribe({
      next: (result)=>{
        this.paymentReceipt = result
      }
    })

  }



  ngOnDestroy(): void {
      this.sub?.unsubscribe()
  }

}
