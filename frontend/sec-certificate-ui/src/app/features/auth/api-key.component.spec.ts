import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ApiKeyComponent } from './api-key.component';
import { ApiKeyService } from '../../core/auth/api-key.service';
import { ApiService } from '../../core/api/api.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('ApiKeyComponent', () => {
  let component: ApiKeyComponent;
  let fixture: ComponentFixture<ApiKeyComponent>;
  let apiKeySvc: jasmine.SpyObj<ApiKeyService>;
  let apiSvc: jasmine.SpyObj<ApiService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    apiKeySvc = jasmine.createSpyObj('ApiKeyService', ['set']);
    apiSvc = jasmine.createSpyObj('ApiService', ['me']);
    router = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ApiKeyComponent],
      providers: [
        { provide: ApiKeyService, useValue: apiKeySvc },
        { provide: ApiService, useValue: apiSvc },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ApiKeyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should show error when key is empty', () => {
    component.key = '   ';
    component.save();

    expect(component.error).toBe('Please paste your API key.');
    expect(apiKeySvc.set).not.toHaveBeenCalled();
  });

  it('should save key and navigate on success', () => {
    apiSvc.me.and.returnValue(of({ name: 'Test' }));
    component.key = 'abc123';

    component.save();

    expect(apiKeySvc.set).toHaveBeenCalledWith('abc123');
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
  });

  it('should show error when API call fails', () => {
    apiSvc.me.and.returnValue(throwError(() => new Error('Bad key')));
    component.key = 'bad-key';

    component.save();

    expect(component.error).toBe('API key rejected by server.');
    expect(component.loading).toBeFalse();
  });
});
