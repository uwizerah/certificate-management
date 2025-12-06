import { TestBed } from '@angular/core/testing';
import { ApiKeyService } from './api-key.service';

describe('ApiKeyService', () => {

  beforeEach(() => {
    // Always reset storage first
    localStorage.clear();
    TestBed.resetTestingModule();
  });

  function createService(): ApiKeyService {
    TestBed.configureTestingModule({});
    return TestBed.inject(ApiKeyService);
  }

  it('should start with no api key', () => {
    const service = createService();

    expect(service.get()).toBeNull();
    expect(service.hasKey).toBeFalse();
  });

  it('should store api key', () => {
    const service = createService();

    service.set('abc123');

    expect(service.get()).toBe('abc123');
    expect(localStorage.getItem('sec.apiKey')).toBe('abc123');
  });

  it('should clear api key', () => {
    const service = createService();

    service.set('abc123');
    service.clear();

    expect(service.get()).toBeNull();
    expect(localStorage.getItem('sec.apiKey')).toBeNull();
  });

  it('should restore api key from localStorage', () => {
    // ✅ Set storage BEFORE service is created
    localStorage.setItem('sec.apiKey', 'persisted');

    const service = createService();

    expect(service.get()).toBe('persisted');
  });

});
