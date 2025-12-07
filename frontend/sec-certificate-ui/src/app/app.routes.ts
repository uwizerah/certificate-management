import { Routes } from '@angular/router';
import { inject } from '@angular/core';
import { Router } from '@angular/router';

import { ApiKeyComponent } from './features/auth/api-key.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { TemplateListComponent } from './features/templates/template-list.component';
import { TemplateEditorComponent } from './features/templates/template-editor.component';
import { VerifyComponent } from './features/verify/verify.component';
import { CertificateListComponent } from './features/certificates/list.component';
import { ApiKeyService } from './core/auth/api-key.service';
import { TemplateGenerateComponent } from './features/templates/template-generate.component';

export const routes: Routes = [

  // Root → dashboard
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },

  { path: 'auth', component: ApiKeyComponent },

  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [() => {
      const apiKeyService = inject(ApiKeyService);
      const router = inject(Router);
      if (apiKeyService.get()) {
        return true;
      } else {
        router.navigate(['/auth']);
        return false;
      }
    }],
    children: [

      //Redirect dashboard root → templates
      { path: '', redirectTo: 'templates', pathMatch: 'full' },

      // Templates
      { path: 'templates', component: TemplateListComponent },
      { path: 'templates/:id', component: TemplateEditorComponent },
      { path: 'templates/:id/generate', component: TemplateGenerateComponent },

      // Certificates
      { path: 'certificates/list', component: CertificateListComponent },

      // Verify inside dashboard
      { path: 'verify', component: VerifyComponent }
    ]
  },

  // Public verification
  { path: 'verify/:hash', component: VerifyComponent },

  // Fallback
  { path: '**', redirectTo: 'auth' },

];
