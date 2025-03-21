import { Component, inject, OnInit } from '@angular/core';
import { RestaurantService } from '../restaurant.service';
import { Observable, Subscription } from 'rxjs';
import { LineItems, menuItems } from '../models';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu',
  standalone: false,
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements OnInit {
  // TODO: Task 2

  //Populate the view 
  private restaurantSvc = inject(RestaurantService)
  private router = inject(Router)
  private sub!:Subscription
  protected menuItems!: menuItems[]

  protected quantity: number = 1
  protected countQuantity: number = 0
  protected totalPrice: number = 0

  ngOnInit():void{

      //this.menuItems$ = this.restaurantSvc.getMenuItems() 

      this.sub = this.restaurantSvc.getMenuItems().subscribe({
        next: (data)=>{
          this.menuItems = data.map(item => ({
            ...item,
            quantity: 0,
            totalPrice: 0
          }));
        }
      })
  } 

  addItem($index: number): void {
    this.menuItems = [...this.menuItems];
    const item = this.menuItems[$index];
    
    item.quantity += 1;
    this.countQuantity++;
    
    // Calculate total price based on original price
    const itemTotal = item.price * item.quantity;
    item.totalPrice = itemTotal;
    
    // Update overall total
    this.totalPrice = this.menuItems.reduce((sum, item) => sum + item.totalPrice, 0);
  }

  placeOrder(): void {
    // Filter items with quantity > 0 and map to LineItems format
    const orderItems: LineItems[] = this.menuItems
      .filter(item => item.quantity > 0)
      .map(item => ({
        id:item.id,
        name: item.name,
        price: item.price,
        quantity: item.quantity,
        totalPrice: item.totalPrice,
        totalCartPrice: this.totalPrice
      }));

    if (orderItems.length > 0) {
      console.info('Order items:', orderItems);
      console.info('Total quantity:', this.countQuantity);
      console.info('Total price:', this.totalPrice);
      // Send order to restaurant service
      
    } else {
      console.info('No items selected');
    }

    this.restaurantSvc.sendLineItems(orderItems)

    this.router.navigate(['/placeOrder']);

  }

  removeItem($index:number):void{
    console.info("This is your index to remove ", $index)
     
    this.menuItems = [...this.menuItems]

    this.menuItems.splice($index,1)

  }

  disabled(): boolean {
    return this.menuItems.filter(item => item.quantity > 0).length === 0;
}

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }



   



}
