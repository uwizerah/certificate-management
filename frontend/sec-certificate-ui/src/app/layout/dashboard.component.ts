import { Component } from '@angular/core';
import { ApiService, MeResponse } from '../core/api/api.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.html'
})
export class DashboardComponent {
  me?: MeResponse;
  constructor(private api: ApiService) {
    this.api.me().subscribe(m => this.me = m);
  }
}
