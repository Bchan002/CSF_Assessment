// You may use this file to create any models

//Create a model for the menu items 
export interface menuItems {
    id:string
    name:string
    description:string
    price:number
    quantity: number
    totalPrice:number
}

export interface LineItems {
    id:string
    name: string
    price: number
    quantity: number 
    totalPrice:number
    totalCartPrice:number
}

export interface submitOrder {
    username:string
    password:string
    lineItems: LineItems[]
    
}

export interface paymentReceipt{
    orderId:string
    paymentId:string
    total:number
    timeStamp:string
}