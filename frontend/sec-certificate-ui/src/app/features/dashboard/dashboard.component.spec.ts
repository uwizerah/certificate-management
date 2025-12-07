import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { ApiService } from '../../core/api/api.service';
import { ApiKeyService } from '../../core/auth/api-key.service';
import { of } from 'rxjs';
import { provideRouter, Router } from '@angular/router';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;

  let apiSvc: jasmine.SpyObj<ApiService>;
  let keySvc: jasmine.SpyObj<ApiKeyService>;
  let router: Router;

  beforeEach(async () => {
    apiSvc = jasmine.createSpyObj('ApiService', ['me']);
    keySvc = jasmine.createSpyObj('ApiKeyService', ['clear']);

    apiSvc.me.and.returnValue(of({ name: 'John' }));

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: ApiService, useValue: apiSvc },
        { provide: ApiKeyService, useValue: keySvc },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);

    spyOn(router, 'navigate');

    fixture.detectChanges(); // ngOnInit equivalent for standalone
  });

  // ------------------------
  // BASIC CREATION
  // ------------------------

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  // ------------------------
  // API LOAD
  // ------------------------

  it('should fetch user info on init', () => {
    expect(apiSvc.me).toHaveBeenCalled();
    expect(component.me?.name).toBe('John');
  });

  // ------------------------
  // LOGOUT
  // ------------------------

  it('should clear API key and redirect on logout', () => {
    component.logout();

    expect(keySvc.clear).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/auth']);
  });

});
