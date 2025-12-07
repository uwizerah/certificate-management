import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api/api.service';
import { Router } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-template-editor',
  imports: [CommonModule, FormsModule],
  template: `
  <div class="max-w-3xl mx-auto bg-white rounded-xl shadow p-6 space-y-5">

    <h2 class="text-xl font-semibold text-gray-800">
      Create Certificate Template
    </h2>

    <!-- Template Name -->
    <div>
      <label class="block text-sm text-gray-600 mb-1">
        Template Name <span class="text-red-500">*</span>
      </label>

      <input
        class="w-full border rounded-lg p-2 transition"
        [(ngModel)]="name"
        placeholder="Template name"
        required
        [class.border-red-500]="!name.trim()"
        [class.border-gray-300]="name.trim()"
      />
    </div>

    <!-- HTML Template -->
    <div>
      <label class="block text-sm text-gray-600 mb-1">
        HTML Template <span class="text-red-500">*</span>
      </label>

      <textarea
        class="w-full border rounded-lg p-2 h-56 font-mono transition"
        [(ngModel)]="html"
        placeholder="Paste your certificate HTML here"
        required
        [class.border-red-500]="!html.trim()"
        [class.border-gray-300]="html.trim()"
      ></textarea>
    </div>

    <!-- Error message -->
    <p *ngIf="error" class="text-red-600 text-sm">
      {{ error }}
    </p>

    <!-- Buttons -->
    <div class="flex justify-end gap-3 pt-2">

      <button
        class="px-4 py-2 rounded-lg border border-gray-300 text-gray-700 hover:bg-gray-50 transition"
        (click)="cancel()">
        Cancel
      </button>

      <button
        class="px-5 py-2 rounded-lg bg-blue-600 text-white font-semibold hover:bg-blue-700 transition disabled:bg-gray-300 disabled:cursor-not-allowed"
        [disabled]="!name.trim() || !html.trim()"
        (click)="save()">
        Save Template
      </button>

    </div>
  </div>
  `
})
export class TemplateEditorComponent {

  name = '';
  html = '';
  error = '';

  constructor(
    private api: ApiService,
    private router: Router
  ) {}

  save() {

    if (!this.name.trim() || !this.html.trim()) {
      this.error = 'Name and HTML template are required.';
      return;
    }

    this.api.createTemplate({
      name: this.name.trim(),
      htmlTemplate: this.html.trim()
    }).subscribe({
      next: () => this.router.navigate(['/dashboard/templates']),
      error: err => {
        this.error = err?.error?.message || 'Failed to create template';
      }
    });
  }

  cancel() {
    this.router.navigate(['/dashboard/templates']);
  }
}
