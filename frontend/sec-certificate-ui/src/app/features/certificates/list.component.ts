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
    </div>

    <div class="table-card">

      <input
        class="search"
        placeholder="Search by Issued To..."
        [(ngModel)]="query"
      />

      <table>
        <thead>
          <tr>
            <th>Certificate ID</th>
            <th>Issued To</th>
            <th>Issued On</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          <tr *ngFor="let c of filtered()">
            <td>C-{{ c.id }}</td>
            <td>{{ c.issuedTo || 'UNKNOWN' }}</td>
            <td>{{ c.createdAt | date }}</td>

            <!-- Status with colors -->
            <td>
              <span class="badge"
                    [ngClass]="{
                      'pending': c.status === 'PENDING',
                      'generated': c.status === 'GENERATED',
                      'revoked': c.status === 'REVOKED'
                    }">
                {{ c.status }}
              </span>
            </td>

            <td>
              <button class="download"
                      [disabled]="c.status !== 'GENERATED'"
                      (click)="download(c.id)">
                Download
              </button>
            </td>
          </tr>

          <tr *ngIf="filtered().length === 0">
            <td colspan="5" class="empty">
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

.table-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,.06);
  padding: 14px;
}

.search {
  width: 320px;
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

/* Status badges */
.badge {
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 999px;
  display: inline-block;
}

/* GENERATED = green */
.generated {
  background: #dcfce7;
  color: #166534;
}

/* PENDING = amber */
.pending {
  background: #fef3c7;
  color: #92400e;
}

/* REVOKED = red */
.revoked {
  background: #fee2e2;
  color: #991b1b;
}

/* Download button */
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

.download:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
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
    const q = this.query.toLowerCase();
    return this.certs.filter(c =>
      (c.issuedTo || '').toLowerCase().includes(q)
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
