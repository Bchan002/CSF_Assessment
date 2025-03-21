import { Injectable } from "@angular/core";
import { menuItems } from "./models";
import { ComponentStore } from "@ngrx/component-store";


export interface MenuSlice {
    menuItems: menuItems[]
}


@Injectable()
export class MenuStore extends ComponentStore<MenuSlice>{
    
        
}