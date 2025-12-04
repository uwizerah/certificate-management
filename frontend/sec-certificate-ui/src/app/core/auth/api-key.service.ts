import { Injectable, signal, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

const KEY = 'sec.apiKey';

@Injectable({ providedIn: 'root' })
export class ApiKeyService {
  private platformId = inject(PLATFORM_ID);
  private isBrowser = isPlatformBrowser(this.platformId);

  private read(): string | null {
    if (!this.isBrowser) return null;
    try { return localStorage.getItem(KEY); } catch { return null; }
  }

  apiKey = signal<string | null>(this.read());

  set(key: string) {
    if (this.isBrowser) { try { localStorage.setItem(KEY, key); } catch {} }
    this.apiKey.set(key);
  }
  clear() {
    if (this.isBrowser) { try { localStorage.removeItem(KEY); } catch {} }
    this.apiKey.set(null);
  }
  get(): string | null { return this.apiKey(); }
  get hasKey() { return !!this.apiKey(); }
}
