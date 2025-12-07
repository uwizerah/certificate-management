import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ApiService } from '../../core/api/api.service';
import { Router } from '@angular/router';
import { ApiKeyService } from '../../core/auth/api-key.service';

type MeResponse = { name: string };

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  me?: MeResponse;

  constructor(
    private api: ApiService,
    private keyService: ApiKeyService,
    private router: Router
  ) {
    this.api.me().subscribe(m => this.me = m);
  }

  logout() {
    this.keyService.clear();
    this.router.navigate(['/auth']);
  }
}
