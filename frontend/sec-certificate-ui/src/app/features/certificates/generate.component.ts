// import { Component } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { ActivatedRoute } from '@angular/router';
// import { ApiService } from '../../core/api/api.service';
// import { CertificateResponse } from '../../core/api/models';

// @Component({
//   selector: 'app-generate',
//   standalone: true,
//   imports: [CommonModule, FormsModule],
//   templateUrl: './generate.html',
// })
// export class GenerateComponent {
//   templateId?: number;
//   name = '';
//   lastId?: number;
//   error = '';

//   constructor(private api: ApiService, private route: ActivatedRoute) {
//     // optional: prefill from ?templateId=...
//     const id = this.route.snapshot.queryParamMap.get('templateId');
//     if (id) this.templateId = +id;
//   }

//   // submit() {
//   //   if (!this.templateId) { this.error = 'Pick a template'; return; }
//   //   this.api.generateCertificate(this.templateId, { name: this.name }).subscribe({
//   //     next: (c: CertificateResponse) => { this.lastId = c.id; this.error = ''; },
//   //     error: () => this.error = 'Failed to generate',
//   //   });
//   // }

//   download() {
//     if (!this.lastId) return;
//     this.api.downloadCertificate(this.lastId).subscribe(blob => {
//       const url = URL.createObjectURL(blob);
//       const a = document.createElement('a');
//       a.href = url; a.download = 'certificate.pdf'; a.click();
//       URL.revokeObjectURL(url);
//     });
//   }
// }
