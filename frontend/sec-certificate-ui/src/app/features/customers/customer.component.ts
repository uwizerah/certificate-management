import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api/api.service';
import { CustomerResponse } from '../../core/api/models';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h2>Create Customer (Admin)</h2>

    <input class="border p-2 mr-2" [(ngModel)]="name" placeholder="Customer name" />
    <button class="bg-black text-white px-3 py-2" (click)="create()">Create</button>

    <div *ngIf="result" class="mt-4">
      <p><b>ID:</b> {{ result.id }}</p>
      <p><b>Name:</b> {{ result.name }}</p>
      <p><b>API Key:</b> {{ result.apiKey }}</p>
    </div>
  `
})
export class CustomerComponent {
  name = '';
  result: CustomerResponse | null = null;

  constructor(private api: ApiService) {}

  create() {
    if (!this.name.trim()) return;
    this.api.createCustomer({ name: this.name.trim() })
      .subscribe(res => {
        this.result = res;
        this.name = '';
      });
  }
}
