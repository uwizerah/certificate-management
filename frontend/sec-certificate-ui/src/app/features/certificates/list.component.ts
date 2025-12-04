// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { ApiService } from '../../core/api/api.service';
// import { CertificateSummary } from '../../core/api/models';

// @Component({
//   standalone: true,
//   imports: [CommonModule],
//   template: `
//   <h2>Certificates</h2>
//   <ul>
//     <li *ngFor="let c of certs">
//       {{ c.id }} - {{ c.createdAt }}
//       <button (click)="download(c.id)">Download</button>
//     </li>
//   </ul>
//   `
// })
// export class CertificateListComponent implements OnInit {

//   certs: CertificateSummary[] = [];

//   constructor(private api: ApiService) {}

//   ngOnInit() {
//     this.api.listCertificates().subscribe(res => this.certs = res);
//   }

//   download(id: number) {
//     this.api.downloadCertificate(id).subscribe(blob => {
//       const a = document.createElement('a');
//       a.href = URL.createObjectURL(blob);
//       a.download = 'certificate.pdf';
//       a.click();
//     });
//   }
// }

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 
import { ApiService } from '../../core/api/api.service';
import { CertificateSummary } from '../../core/api/models';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
  <div class="cert-page">

    <div class="header">
      <div>
        <h2>Issued Certificates</h2>
        <p>Track and manage generated certificates.</p>
      </div>
      <button class="export">Export CSV</button>
    </div>

    <div class="table-card">

      <input
        class="search"
        placeholder="Search by ID or Date..."
        [(ngModel)]="query"
      />

      <table>
        <thead>
          <tr>
            <th>Certificate ID</th>
            <th>Issued On</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          <tr *ngFor="let c of filtered()">
            <td>C-{{ c.id }}</td>
            <td>{{ c.createdAt | date }}</td>
            <td><span class="badge">Active</span></td>
            <td>
              <button class="download" (click)="download(c.id)">
                Download
              </button>
            </td>
          </tr>

          <tr *ngIf="filtered().length === 0">
            <td colspan="4" class="empty">
              No certificates found.
            </td>
          </tr>
        </tbody>
      </table>

    </div>
  </div>
  `,
  styles: [
`
.cert-page {
  padding: 10px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
}

.header p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

.export {
  padding: 7px 12px;
  background: white;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  cursor: pointer;
}

.table-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,.06);
  padding: 14px;
}

.search {
  width: 250px;
  padding: 7px 10px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  margin-bottom: 12px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  text-align: left;
  font-size: 12px;
  color: #64748b;
  padding: 8px;
}

td {
  padding: 10px 8px;
  border-top: 1px solid #e5e7eb;
}

.badge {
  background: #dcfce7;
  color: #166534;
  font-size: 12px;
  padding: 3px 8px;
  border-radius: 999px;
}

.download {
  background: #2563eb;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 6px;
  cursor: pointer;
}

.download:hover {
  background: #1e40af;
}

.empty {
  text-align: center;
  color: #94a3b8;
  padding: 12px;
}
`
]
})
export class CertificateListComponent implements OnInit {

  certs: CertificateSummary[] = [];
  query = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.listCertificates().subscribe(res => this.certs = res);
  }

  filtered() {
    return this.certs.filter(c =>
      c.id.toString().includes(this.query) ||
      c.createdAt.toLowerCase().includes(this.query.toLowerCase())
    );
  }

  download(id: number) {
    this.api.downloadCertificate(id).subscribe(blob => {
      const a = document.createElement('a');
      a.href = URL.createObjectURL(blob);
      a.download = `certificate-${id}.pdf`;
      a.click();
    });
  }

}
