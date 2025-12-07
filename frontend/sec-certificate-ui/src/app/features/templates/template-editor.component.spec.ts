import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TemplateEditorComponent } from './template-editor.component';
import { ApiService } from '../../core/api/api.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';

describe('TemplateEditorComponent', () => {
  let component: TemplateEditorComponent;
  let fixture: ComponentFixture<TemplateEditorComponent>;
  let api: jasmine.SpyObj<ApiService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    api = jasmine.createSpyObj('ApiService', ['createTemplate']);
    router = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [TemplateEditorComponent, FormsModule],
      providers: [
        { provide: ApiService, useValue: api },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TemplateEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should NOT save if fields are empty', () => {
    component.name = '';
    component.html = '';

    component.save();

    expect(component.error).toBeTruthy();
    expect(api.createTemplate).not.toHaveBeenCalled();
  });

  it('should save and redirect on success', () => {
    api.createTemplate.and.returnValue(of({
      id: 1,
      name: 'Test Template',
      htmlTemplate: '<p>Hello</p>'
    }));


    component.name = 'Cert';
    component.html = '<p>Hello</p>';

    component.save();

    expect(api.createTemplate).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard/templates']);
  });

  it('should show error when API fails', () => {
    api.createTemplate.and.returnValue(throwError(() => ({
      error: { message: 'Backend error' }
    })));

    component.name = 'Test';
    component.html = '<p>HTML</p>';

    component.save();

    expect(component.error).toBe('Backend error');
  });

  it('should navigate on cancel', () => {
    component.cancel();

    expect(router.navigate).toHaveBeenCalledWith(['/dashboard/templates']);
  });
});
