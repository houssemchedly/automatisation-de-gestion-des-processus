import { Component, type OnInit, type OnDestroy } from "@angular/core"

import { RouterModule } from "@angular/router"
import type { MenuItem } from "primeng/api"
import { AppMenuitem } from "./app.menuitem"

import { Subscription } from "rxjs"
import  { AuthService } from "../../services/auth.service"
import  { NavigationService } from "../../services/navigation.service"

@Component({
  selector: "app-menu",
  standalone: true,
  imports: [AppMenuitem, RouterModule],
  template: `<ul class="layout-menu">
          @for (item of model; track item; let i = $index) {
            @if (!item.separator) {
              <li app-menuitem [item]="item" [index]="i" [root]="true"></li>
            }
            @if (item.separator) {
              <li class="menu-separator"></li>
            }
          }
        </ul>`,
})
export class AppMenu implements OnInit, OnDestroy {
  model: MenuItem[] = []
  private subscription: Subscription = new Subscription()

  constructor(
    private navigationService: NavigationService,
    private authService: AuthService,
  ) {}

  ngOnInit() {
    this.updateMenu()

    this.subscription.add(
      this.authService.currentUser$.subscribe((user: any) => {
        this.updateMenu()
      }),
    )
  }

  ngOnDestroy() {
    this.subscription.unsubscribe()
  }

  private updateMenu(): void {
    this.model = this.navigationService.getMenuItems()
  }
}
