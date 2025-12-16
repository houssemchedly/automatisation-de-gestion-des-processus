import { Component } from "@angular/core"

import { RouterModule } from "@angular/router"
import { AppScrumMasterSidebar } from "../../../layout/component/app.scrum-master-sidebar"

@Component({
  selector: "app-scrum-master-dashboard",
  standalone: true,
  imports: [RouterModule, AppScrumMasterSidebar],
  template: `
    <app-scrum-master-sidebar></app-scrum-master-sidebar>
    <router-outlet></router-outlet>
  `,
})
export class ScrumMasterDashboardComponent {}
