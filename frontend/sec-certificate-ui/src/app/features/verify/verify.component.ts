import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api/api.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
  <div class="verify-container">

    <h2>Verify Certificate</h2>

    <input
      [(ngModel)]="hash"
      placeholder="Enter verification hash"
      (input)="reset()"
    />

    <button
      (click)="verify()"
      [disabled]="!hash || loading">
      {{ loading ? 'Checking...' : 'Verify' }}
    </button>

    <!-- Error -->
    <p class="error" *ngIf="error">{{ error }}</p>

    <!-- Result -->
    <div *ngIf="result" class="result">

      <h3 [class.valid]="result.valid" [class.invalid]="!result.valid">
        {{ result.valid ? 'Certificate is valid' : 'Invalid certificate' }}
      </h3>

      <p class="msg">{{ result.message }}</p>

      <div class="details" *ngIf="result.valid">
        <p><b>Issued To:</b> {{ result.issuedTo }}</p>
        <p><b>Date Issued:</b> {{ result.issuedAt | date:'medium' }}</p>
        <p><b>Status:</b> {{ result.status }}</p>
        <p><b>Template:</b> {{ result.templateName }}</p>
        <p><b>Customer:</b> {{ result.customerName }}</p>
      </div>

      <p class="hash" *ngIf="result.valid">
        Hash: {{ result.verificationHash }}
      </p>

    </div>

  </div>
  `,
  styles: [`
    .verify-container {
      max-width: 480px;
      margin: auto;
      padding: 24px;
      background: white;
      border-radius: 12px;
      box-shadow: 0 4px 20px rgba(0,0,0,.05);
    }

    h2 {
      margin-bottom: 12px;
      font-size: 22px;
      font-weight: 700;
      color: #0f172a;
    }

    input {
      width: 100%;
      padding: 12px;
      font-family: monospace;
      border-radius: 8px;
      border: 1px solid #ddd;
      font-size: 14px;
    }

    button {
      width: 100%;
      padding: 12px;
      margin-top: 12px;
      background: #4f46e5;
      color: white;
      border: none;
      border-radius: 8px;
      font-weight: 600;
      cursor: pointer;
    }

    button:disabled {
      background: #aaa;
      cursor: not-allowed;
    }

    .error {
      color: red;
      margin-top: 10px;
    }

    .result {
      border-left: 5px solid green;
      background: #f9fafb;
      padding: 16px;
      border-radius: 6px;
      margin-top: 15px;
    }

    .valid {
      color: green;
      font-weight: 700;
    }

    .invalid {
      color: red;
      font-weight: 700;
    }

    .details p {
      margin: 4px 0;
    }

    .hash {
      margin-top: 8px;
      font-size: 12px;
      color: #555;
      word-break: break-all;
    }
  `]
})
export class VerifyComponent {

  hash = '';
  result: any = null;
  error = '';
  loading = false;

  constructor(private api: ApiService) {}

  verify() {
    if (!this.hash.trim()) return;

    this.loading = true;
    this.error = '';
    this.result = null;

    this.api.verify(this.hash.trim()).subscribe({
      next: res => {
        this.result = res;
        this.loading = false;
      },
      error: () => {
        this.error = 'Verification failed. Please try again.';
        this.loading = false;
      }
    });
  }

  reset() {
    this.result = null;
    this.error = '';
  }
}
