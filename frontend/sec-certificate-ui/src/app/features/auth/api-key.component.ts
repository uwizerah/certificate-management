import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api/api.service';
import { ApiKeyService } from '../../core/auth/api-key.service';

@Component({
  selector: 'app-api-key',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './api-key.component.html',
  styleUrls: ['./api-key.component.css']
})
export class ApiKeyComponent {
  key = '';
  loading = false;
  error = '';

  constructor(
    private keySvc: ApiKeyService,
    private api: ApiService,
    private router: Router
  ) {}

  save() {
    this.error = '';
    const k = this.key.trim();
    if (!k) { this.error = 'Please paste your API key.'; return; }
    this.keySvc.set(k);

    this.loading = true;
    this.api.me().subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: () => { this.error = 'API key rejected by server.'; this.loading = false; }
    });
  }
}