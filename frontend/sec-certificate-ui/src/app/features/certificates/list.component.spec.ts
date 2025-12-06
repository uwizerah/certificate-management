import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CertificateListComponent } from './list.component';
import { ApiService } from '../../core/api/api.service';
import { of } from 'rxjs';

describe('CertificateListComponent', () => {
  let component: CertificateListComponent;
  let fixture: ComponentFixture<CertificateListComponent>;
  let api: jasmine.SpyObj<ApiService>;

  const mockCerts = [
    { id: 1, createdAt: '2024-01-01', status: 'ACTIVE', issuedTo: 'John', verificationHash: 'abc' },
    { id: 2, createdAt: '2024-02-01', status: 'ACTIVE', issuedTo: 'Mary', verificationHash: 'xyz' }
  ];

  beforeEach(async () => {
    api = jasmine.createSpyObj('ApiService', ['listCertificates', 'downloadCertificate']);

    api.listCertificates.and.returnValue(of(mockCerts));
    api.downloadCertificate.and.returnValue(of(new Blob()));

    await TestBed.configureTestingModule({
      imports: [CertificateListComponent],
      providers: [{ provide: ApiService, useValue: api }]
    }).compileComponents();

    fixture = TestBed.createComponent(CertificateListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // --------------------
  // BASIC TEST
  // --------------------
  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  // --------------------
  // INIT LOAD
  // --------------------
  it('should load certificates on init', () => {
    expect(api.listCertificates).toHaveBeenCalled();
    expect(component.certs.length).toBe(2);
  });

  // --------------------
  // FILTERING
  // --------------------
  it('should filter by certificate id', () => {
    component.query = '1';

    const result = component.filtered();

    const ids = result.map(c => c.id);
    expect(ids).toContain(1);
  });

  it('should filter by created date', () => {
    component.query = '2024-02';

    const result = component.filtered();

    expect(result.length).toBe(1);
    expect(result[0].id).toBe(2);
  });

  it('should return empty array if no matches', () => {
    component.query = 'nothing';
    expect(component.filtered().length).toBe(0);
  });

  // --------------------
  // DOWNLOAD
  // --------------------
  it('should call API to download certificate', () => {
    spyOn(URL, 'createObjectURL').and.returnValue('blob:url');
    spyOn(document, 'createElement').and.callFake(() => {
      return { click: jasmine.createSpy('click') } as any;
    });

    component.download(1);

    expect(api.downloadCertificate).toHaveBeenCalledWith(1);
  });

});
