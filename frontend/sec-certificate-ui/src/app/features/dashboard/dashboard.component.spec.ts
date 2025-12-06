import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { ApiService } from '../../core/api/api.service';
import { of } from 'rxjs';
import { provideRouter } from '@angular/router';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let apiSvc: jasmine.SpyObj<ApiService>;

  beforeEach(async () => {
    apiSvc = jasmine.createSpyObj('ApiService', ['me']);
    apiSvc.me.and.returnValue(of({ name: 'John' }));

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: ApiService, useValue: apiSvc },
        provideRouter([])   // ✅ ADD THIS
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers API call
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch user info on init', () => {
    expect(apiSvc.me).toHaveBeenCalled();
    expect(component.me?.name).toBe('John');
  });
});
