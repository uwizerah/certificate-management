import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TemplateGenerateComponent } from './template-generate.component';
import { ApiService } from '../../core/api/api.service';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { DomSanitizer } from '@angular/platform-browser';

describe('TemplateGenerateComponent', () => {
  let component: TemplateGenerateComponent;
  let fixture: ComponentFixture<TemplateGenerateComponent>;
  let api: jasmine.SpyObj<ApiService>;

  const mockTemplate = {
    htmlTemplate: '<h1>{{name}}</h1>',
    placeholders: ['name']
  };

  beforeEach(async () => {
    api = jasmine.createSpyObj('ApiService', ['getTemplate', 'generateCertificate']);
    api.getTemplate.and.returnValue(of(mockTemplate));
    api.generateCertificate.and.returnValue(of({
        id: 1,
        issuedTo: 'John',
        status: 'ISSUED',
        verificationHash: 'abc123',
        createdAt: '2025-01-01'
    }));


    await TestBed.configureTestingModule({
      imports: [TemplateGenerateComponent],
      providers: [
        { provide: ApiService, useValue: api },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: { get: () => '1' } }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TemplateGenerateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load template on init', () => {
    expect(api.getTemplate).toHaveBeenCalledWith(1);
    expect(component.template).toEqual(mockTemplate);
  });

  it('should prepare values object from placeholders', () => {
    expect(component.values['name']).toBe('');
  });

  it('should replace placeholders in previewHtml', () => {
    component.values['name'] = 'John';
    component.updatePreview();

    const html = component.previewHtml as any;
    expect(html.changingThisBreaksApplicationSecurity).toContain('John');
  });

  it('should call API on generate()', () => {
    component.values['name'] = 'John';
    component.generate();
    expect(api.generateCertificate).toHaveBeenCalledWith(1, {
      data: { name: 'John' }
    });
  });
});
