import { Injectable } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { ApiKeyService } from './api-key.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard  {
  constructor(private keys: ApiKeyService, private router: Router) {}
  canActivate: CanActivateFn = () => {
    if (this.keys.hasKey) return true;
    this.router.navigate(['/auth']);
    return false;
  };
}
