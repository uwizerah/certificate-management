import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TemplateEditorComponent } from './template-editor.component';
import { ApiService } from '../../core/api/api.service';
import { of } from 'rxjs';

describe('TemplateEditorComponent', () => {
  let component: TemplateEditorComponent;
  let fixture: ComponentFixture<TemplateEditorComponent>;
  let api: jasmine.SpyObj<ApiService>;

  beforeEach(async () => {
    api = jasmine.createSpyObj('ApiService', ['createTemplate']);
    api.createTemplate.and.returnValue(of({
        id: 1,
        name: 'Test Template',
        htmlTemplate: '<h1></h1>'
    }));


    await TestBed.configureTestingModule({
      imports: [TemplateEditorComponent],
      providers: [{ provide: ApiService, useValue: api }]
    }).compileComponents();

    fixture = TestBed.createComponent(TemplateEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call API and emit saved event on save()', () => {
    spyOn(component.saved, 'emit');

    component.name = 'Cert';
    component.html = '<h1>{{name}}</h1>';

    component.save();

    expect(api.createTemplate).toHaveBeenCalledWith({
      name: 'Cert',
      htmlTemplate: '<h1>{{name}}</h1>'
    });
    expect(component.saved.emit).toHaveBeenCalled();
  });

  it('should clear fields after save', () => {
    component.name = 'Test';
    component.html = 'HTML';

    component.save();

    expect(component.name).toBe('');
    expect(component.html).toBe('');
  });

  it('should emit cancel event', () => {
    spyOn(component.cancel, 'emit');
    component.cancel.emit();
    expect(component.cancel.emit).toHaveBeenCalled();
  });
});
