import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { ApiKeyService } from '../auth/api-key.service';

export const apiKeyInterceptor: HttpInterceptorFn = (req, next) => {
  const keySvc = inject(ApiKeyService);
  const key = keySvc.get();
  return key
    ? next(req.clone({ setHeaders: { 'X-API-KEY': key } }))
    : next(req);
};