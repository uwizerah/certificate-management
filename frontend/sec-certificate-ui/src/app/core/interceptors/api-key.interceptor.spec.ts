import { TestBed } from '@angular/core/testing';
import { HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { apiKeyInterceptor } from './api-key.interceptor';
import { ApiKeyService } from '../auth/api-key.service';
import { EnvironmentInjector, runInInjectionContext } from '@angular/core';

describe('apiKeyInterceptor', () => {
  let service: ApiKeyService;
  let injector: EnvironmentInjector;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ApiKeyService],
    });

    service = TestBed.inject(ApiKeyService);
    injector = TestBed.inject(EnvironmentInjector);
  });

  it('adds X-API-KEY header when key exists', () => {
    service.set('test-key');

    const req = new HttpRequest('GET', '/test');

    const next: HttpHandlerFn = (request) => {
      expect(request.headers.get('X-API-KEY')).toBe('test-key');
      return {} as any;
    };

    runInInjectionContext(injector, () => {
      apiKeyInterceptor(req, next);
    });
  });

  it('does NOT add header when key is missing', () => {
    service.clear();

    const req = new HttpRequest('GET', '/test');

    const next: HttpHandlerFn = (request) => {
      expect(request.headers.has('X-API-KEY')).toBeFalse();
      return {} as any;
    };

    runInInjectionContext(injector, () => {
      apiKeyInterceptor(req, next);
    });
  });
});
