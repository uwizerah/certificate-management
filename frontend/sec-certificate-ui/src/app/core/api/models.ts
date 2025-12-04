export interface CertificateResponse {
  id: number;
  issuedTo: string;
  status: string;
  verificationHash: string;
  createdAt: string;
}

export interface CertificateSummary {
  id: number;
  issuedTo: string;
  status: string;
  verificationHash: string;
  createdAt: string;
}

export interface CreateCustomerRequest { name: string; }
export interface CustomerResponse { id: number; name: string; apiKey: string; }
