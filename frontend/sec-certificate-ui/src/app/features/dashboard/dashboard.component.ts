import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ApiService } from '../../core/api/api.service';

type MeResponse = { name: string }; // or import your existing MeResponse

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  me?: MeResponse;
  constructor(private api: ApiService) {
    this.api.me().subscribe(m => this.me = m);
  }
}
