import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api/api.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
  <h2>Verify Certificate</h2>

  <input [(ngModel)]="hash" placeholder="Verification hash"/>
  <button (click)="verify()">Check</button>

  <pre *ngIf="result">{{ result | json }}</pre>
  `
})
export class VerifyComponent {

  hash = '';
  result: any;

  constructor(private api: ApiService) {}

  verify() {
    this.api.verify(this.hash).subscribe(res => this.result = res);
  }
}
