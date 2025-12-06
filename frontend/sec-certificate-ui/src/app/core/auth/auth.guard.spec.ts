import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { ApiKeyService } from './api-key.service';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let keys: ApiKeyService;
  let router: jasmine.SpyObj<Router>;

  const dummyRoute = {} as ActivatedRouteSnapshot;
  const dummyState = {} as RouterStateSnapshot;

  beforeEach(() => {
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        ApiKeyService,
        { provide: Router, useValue: routerSpy }
      ]
    });

    guard = TestBed.inject(AuthGuard);
    keys = TestBed.inject(ApiKeyService);
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should allow access when key exists', () => {
    keys.set('valid_key');
    expect(guard.canActivate(dummyRoute, dummyState)).toBeTrue();
  });

  it('should block access when key missing', () => {
    keys.clear();
    expect(guard.canActivate(dummyRoute, dummyState)).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});
