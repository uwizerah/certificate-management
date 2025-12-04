import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
    CertificateResponse, 
    CertificateSummary,
    CreateCustomerRequest,
    CustomerResponse
 } from './models';


const BASE = 'http://localhost:8080/api';

export interface MeResponse { name: string; }
export interface Template { id: number; name: string; htmlTemplate: string; }
export interface CreateTemplateReq { name: string; htmlTemplate: string; }

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}

  me(): Observable<MeResponse> {
    return this.http.get<MeResponse>(`${BASE}/me`);
  }

  getTemplates(): Observable<Template[]> {
    return this.http.get<Template[]>(`${BASE}/templates`);
  }

  createTemplate(body: CreateTemplateReq): Observable<Template> {
    return this.http.post<Template>(`${BASE}/templates`, body);
  }

  getTemplate(id: number) {
    return this.http.get<any>(`${BASE}/templates/${id}`);
  }

  generateCertificate(
    templateId: number,
    body: { data: Record<string, string> }
  ): Observable<CertificateResponse> {
    return this.http.post<CertificateResponse>(
      `${BASE}/certificates/generate?templateId=${templateId}`,
      body
    );
  }

  downloadCertificate(id: number) {
    // GET /api/certificates/{id}/download
    return this.http.get(`${BASE}/certificates/${id}/download`, { responseType: 'blob' });
  }

  verify(hash: string) {
    return this.http.get(`${BASE}/verify/${hash}`);
  }

  listCertificates(): Observable<CertificateSummary[]> {
    return this.http.get<CertificateSummary[]>(`${BASE}/certificates`);
  }

  createCustomer(body: CreateCustomerRequest): Observable<CustomerResponse> {
    return this.http.post<CustomerResponse>(`${BASE}/customers`, body);
  }
}
