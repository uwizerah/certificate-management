import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VerifyComponent } from './verify.component';
import { ApiService } from '../../core/api/api.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { of, throwError } from 'rxjs';

describe('VerifyComponent', () => {
  let component: VerifyComponent;
  let fixture: ComponentFixture<VerifyComponent>;
  let api: jasmine.SpyObj<ApiService>;

  beforeEach(async () => {
    api = jasmine.createSpyObj('ApiService', ['verify']);

    await TestBed.configureTestingModule({
      imports: [VerifyComponent, FormsModule, CommonModule],
      providers: [{ provide: ApiService, useValue: api }]
    }).compileComponents();

    fixture = TestBed.createComponent(VerifyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ----------------------
  // BASIC
  // ----------------------
  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  // ----------------------
  // VERIFY SUCCESS
  // ----------------------
  it('should verify certificate successfully', () => {
    const mockResponse = {
      valid: true,
      issuedTo: 'John',
      issuedAt: '2024-01-01',
      status: 'GENERATED',
      templateName: 'Test Template',
      customerName: 'RURA',
      verificationHash: 'abc123',
      message: 'Certificate is valid'
    };

    api.verify.and.returnValue(of(mockResponse));
    component.hash = 'abc123';

    component.verify();

    expect(api.verify).toHaveBeenCalledWith('abc123');
    expect(component.result).toEqual(mockResponse);
    expect(component.loading).toBeFalse();
    expect(component.error).toBe('');
  });

  // ----------------------
  // VERIFY FAILURE
  // ----------------------
  it('should handle verification error', () => {
    api.verify.and.returnValue(throwError(() => new Error('Failed')));
    component.hash = 'bad-hash';

    component.verify();

    expect(component.error).toBe('Verification failed. Please try again.');
    expect(component.loading).toBeFalse();
  });

  // ----------------------
  // EMPTY INPUT GUARD
  // ----------------------
  it('should not call API if hash is empty', () => {
    component.hash = '';
    component.verify();

    expect(api.verify).not.toHaveBeenCalled();
  });

  // ----------------------
  // RESET
  // ----------------------
  it('should reset result and error on input', () => {
    component.result = {};
    component.error = 'Some error';

    component.reset();

    expect(component.result).toBeNull();
    expect(component.error).toBe('');
  });
});
