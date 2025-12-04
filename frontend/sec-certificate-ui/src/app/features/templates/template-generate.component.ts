import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../../core/api/api.service';
import { FormsModule } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './template-generate.component.html'
})
export class TemplateGenerateComponent implements OnInit {

  templateId!: number;
  template: any;
  values: Record<string, string> = {};
  previewHtml = '';

  constructor(
    private route: ActivatedRoute,
    private api: ApiService,
    private sanitizer: DomSanitizer
  ) {}


  ngOnInit() {
    this.templateId = Number(this.route.snapshot.paramMap.get('id'));

    // fetch template (must include placeholders + htmlTemplate)
    this.api.getTemplate(this.templateId).subscribe(t => {
      this.template = t;

      // prepare empty fields for placeholders
      t.placeholders.forEach((p: string) => {
        this.values[p] = '';
      });

      this.updatePreview();
    });
  }

  updatePreview() {
    let html = this.template.htmlTemplate;

    for (const key in this.values) {
      const regex = new RegExp(`{{\\s*${key}\\s*}}`, 'g');
      html = html.replace(regex, this.values[key] || `[${key}]`);
    }

    this.previewHtml = this.sanitizer.bypassSecurityTrustHtml(html) as any;

  }
}
