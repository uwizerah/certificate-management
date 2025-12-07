import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ApiService } from './api.service';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;

  const BASE = 'http://localhost:8080/api';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService]
    });

    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  // ---------------------------
  // BASIC API CALLS
  // ---------------------------

  it('should call /me', () => {
    service.me().subscribe(res => {
      expect(res.name).toBe('Test User');
    });

    const req = httpMock.expectOne(`${BASE}/me`);
    expect(req.request.method).toBe('GET');
    req.flush({ name: 'Test User' });
  });

  it('should fetch templates', () => {
    service.getTemplates().subscribe(res => {
      expect(res.length).toBe(1);
    });

    const req = httpMock.expectOne(`${BASE}/templates`);
    expect(req.request.method).toBe('GET');
    req.flush([{ id: 1, name: 'T', htmlTemplate: '<h1></h1>' }]);
  });

  it('should fetch single template', () => {
    service.getTemplate(10).subscribe();

    const req = httpMock.expectOne(`${BASE}/templates/10`);
    expect(req.request.method).toBe('GET');
    req.flush({});
  });

  it('should create template', () => {
    const payload = { name: 'Cert', htmlTemplate: '<html></html>' };

    service.createTemplate(payload).subscribe(res => {
      expect(res.name).toBe('Cert');
    });

    const req = httpMock.expectOne(`${BASE}/templates`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);

    req.flush({ id: 1, ...payload });
  });

  // ---------------------------
  // CERTIFICATES
  // ---------------------------

  it('should generate certificate', () => {
    const body = { data: { name: 'John' } };

    service.generateCertificate(5, body).subscribe(res => {
      expect(res.id).toBe(99);
    });

    const req = httpMock.expectOne(`${BASE}/certificates/generate?templateId=5`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(body);

    req.flush({ id: 99 });
  });

  it('should download certificate as blob', () => {
    service.downloadCertificate(3).subscribe();

    const req = httpMock.expectOne(`${BASE}/certificates/3/download`);
    expect(req.request.method).toBe('GET');
    expect(req.request.responseType).toBe('blob');

    req.flush(new Blob());
  });

  it('should fetch certificate list', () => {
    service.listCertificates().subscribe(res => {
      expect(res.length).toBe(1);
    });

    const req = httpMock.expectOne(`${BASE}/certificates`);
    expect(req.request.method).toBe('GET');

    req.flush([{ id: 1, issuedTo: 'John', status: 'GENERATED' }]);
  });

  // ---------------------------
  // VERIFY
  // ---------------------------

  it('should verify certificate hash', () => {
    service.verify('abc123').subscribe();

    const req = httpMock.expectOne(`${BASE}/verify/abc123`);
    expect(req.request.method).toBe('GET');
    req.flush({ valid: true });
  });

  // ---------------------------
  // CUSTOMER
  // ---------------------------

  it('should create customer', () => {
    const payload = { name: 'John Doe' };

    service.createCustomer(payload).subscribe(res => {
      expect(res.name).toBe('John Doe');
    });

    const req = httpMock.expectOne(`${BASE}/customers`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);

    req.flush({ id: 1, name: 'John Doe', apiKey: 'key123' });
  });

  // ---------------------------
  // API KEY STORAGE
  // ---------------------------

  it('should store api key', () => {
    service.setApiKey('SECRET123');
    expect(localStorage.getItem('api_key')).toBe('SECRET123');
  });

  it('should retrieve api key', () => {
    localStorage.setItem('api_key', 'ABC');
    expect(service.getApiKey()).toBe('ABC');
  });

  it('should clear api key on logout', () => {
    localStorage.setItem('api_key', 'ABC');
    service.logout();
    expect(localStorage.getItem('api_key')).toBeNull();
  });

});
