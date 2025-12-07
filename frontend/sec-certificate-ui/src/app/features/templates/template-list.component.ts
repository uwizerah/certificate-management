import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ApiService, Template } from '../../core/api/api.service';

@Component({
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './template-list.component.html'
})
export class TemplateListComponent implements OnInit {

  templates: Template[] = [];
  loading = true;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load() {
    this.loading = true;
    this.api.getTemplates().subscribe(res => {
      this.templates = res.sort(
        (a: any, b: any) =>
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
      );
      this.loading = false;
    });
  }
}
