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
  });

  it('should call /me', () => {
    service.me().subscribe();

    const req = httpMock.expectOne(`${BASE}/me`);
    expect(req.request.method).toBe('GET');
    req.flush({ name: 'Test User' });
  });

  it('should fetch templates', () => {
    service.getTemplates().subscribe();

    const req = httpMock.expectOne(`${BASE}/templates`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should verify certificate', () => {
    service.verify('abc123').subscribe();

    const req = httpMock.expectOne(`${BASE}/verify/abc123`);
    expect(req.request.method).toBe('GET');
    req.flush(true);
  });

  it('should create customer', () => {
    service.createCustomer({ name: 'John' }).subscribe();

    const req = httpMock.expectOne(`${BASE}/customers`);
    expect(req.request.method).toBe('POST');
    req.flush({ id: 1, name: 'John', apiKey: 'key123' });
  });
});
