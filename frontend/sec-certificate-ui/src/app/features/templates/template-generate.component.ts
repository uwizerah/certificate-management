import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../../core/api/api.service';
import { FormsModule } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';

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
    private sanitizer: DomSanitizer,
    private router: Router
  ) {}

  ngOnInit() {
    this.templateId = Number(this.route.snapshot.paramMap.get('id'));

    this.api.getTemplate(this.templateId).subscribe(t => {
      this.template = t;

      // Initialize fields
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

  //EQUIRED PLACEHOLDER ENFORCEMENT
  allFieldsFilled(): boolean {
    return Object.values(this.values).every(v => v && v.trim().length > 0);
  }

  generate() {

    if (!this.allFieldsFilled()) {
      alert("Please fill all required fields before generating the certificate.");
      return;
    }

    this.api.generateCertificate(this.templateId, { data: this.values })
      .subscribe({
        next: () => {
          alert("Certificate generated successfully!");
          this.router.navigate(['/dashboard/certificates/list']);
        },

        error: err => {
          const msg = err?.error?.message || "Certificate generation failed";
          alert(msg);
        }
      });
  }
}
