import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api/api.service';

@Component({
  standalone: true,
  selector: 'app-template-editor',
  imports: [CommonModule, FormsModule],
  template: `
  <div class="space-y-3">
    <input class="w-full border rounded p-2"
           [(ngModel)]="name"
           placeholder="Template name" />

    <textarea class="w-full border rounded p-2 h-40"
              [(ngModel)]="html"
              placeholder="HTML template"></textarea>

    <div class="flex justify-end gap-2">
      <button class="px-3 py-2 rounded border"
              (click)="cancel.emit()">
        Cancel
      </button>
      <button class="px-4 py-2 rounded bg-black text-white"
              (click)="save()">
        Save
      </button>
    </div>
  </div>
  `
})
export class TemplateEditorComponent {

  name = '';
  html = '';

  @Output() saved = new EventEmitter();
  @Output() cancel = new EventEmitter();

  constructor(private api: ApiService) {}

  save() {
    this.api.createTemplate({
      name: this.name,
      htmlTemplate: this.html
    }).subscribe(() => {
      this.saved.emit();
      this.name = '';
      this.html = '';
    });
  }
}
