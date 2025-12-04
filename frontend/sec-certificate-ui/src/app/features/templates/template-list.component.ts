import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ApiService, Template } from '../../core/api/api.service';
import { TemplateEditorComponent } from './template-editor.component';

@Component({
  standalone: true,
  imports: [CommonModule, RouterModule, TemplateEditorComponent],
  templateUrl: './template-list.component.html'
})
export class TemplateListComponent implements OnInit {
  templates: Template[] = [];
  loading = true;
  showModal = false;

  constructor(private api: ApiService) {}
  ngOnInit(): void {
    this.load();
  }

  load() {
    this.loading = true;
    this.api.getTemplates().subscribe(res => {
      this.templates = res;
      this.loading = false;
    });
  }

  reload() {
    this.loading = true;
    this.api.getTemplates().subscribe(res => {
      this.templates = res;
      this.loading = false;
    });
  }

  closeModal(refresh: boolean = false) {
    this.showModal = false;
    if (refresh) {
      this.load();
    }
  }
}
