import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TemplateListComponent } from './template-list.component';
import { ApiService } from '../../core/api/api.service';
import { of } from 'rxjs';
import { provideRouter } from '@angular/router';

describe('TemplateListComponent', () => {
  let component: TemplateListComponent;
  let fixture: ComponentFixture<TemplateListComponent>;
  let api: jasmine.SpyObj<ApiService>;

  beforeEach(async () => {
    api = jasmine.createSpyObj('ApiService', ['getTemplates']);
    api.getTemplates.and.returnValue(of([
      { id: 1, name: 'A', htmlTemplate: '<p>A</p>' },
      { id: 2, name: 'B', htmlTemplate: '<p>B</p>' }
    ]));

    await TestBed.configureTestingModule({
      imports: [TemplateListComponent],
      providers: [
        { provide: ApiService, useValue: api },
        provideRouter([])   // ✅ ADD THIS
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TemplateListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load templates on init', () => {
    expect(api.getTemplates).toHaveBeenCalled();
    expect(component.templates.length).toBe(2);
    expect(component.loading).toBeFalse();
  });

  it('should reload templates when load() is called', () => {
    component.load();
    expect(api.getTemplates).toHaveBeenCalledTimes(2);
  });
});
