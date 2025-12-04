// import { Routes } from '@angular/router';
// import { inject } from '@angular/core';
// import { Router } from '@angular/router';
// import { ApiKeyComponent } from '../app/features/auth/api-key.component';
// import { DashboardComponent } from './features/dashboard/dashboard.component';
// import { TemplateListComponent } from './features/templates/template-list.component';
// import { TemplateEditorComponent } from './features/templates/template-editor.component';
// import { VerifyComponent } from './features/verify/verify.component';
// import { GenerateComponent } from './features/certificates/generate.component';
// import { CertificateListComponent } from './features/certificates/list.component';
// import { CustomerComponent } from './features/customers/customer.component';
// import { ApiKeyService } from './core/auth/api-key.service';

// export const routes: Routes = [
//   { path: '', pathMatch: 'full', redirectTo: 'auth' },
//   { path: 'auth', component: ApiKeyComponent },
//   {
//     path: 'dashboard',
//     component: DashboardComponent,
//     children: [
//       { path: '', component: TemplateListComponent },        // default content
//       { path: 'templates', component: TemplateListComponent },
//       { path: 'certificates/generate', component: GenerateComponent },
//       { path: 'certificates/list', component: CertificateListComponent },
//       { path: 'verify', component: VerifyComponent }
//     ],
//     canActivate: [() => {
//       const apiKeyService = inject(ApiKeyService);
//       const router = inject(Router);
//       if (apiKeyService.get()) {
//         return true;
//       } else {
//         router.navigate(['/auth']);
//         return false;
//       }
//     }]
//   },
//   {
//     path: 'templates',
//     component: TemplateListComponent,
//     canActivate: [() => {
//       const apiKeyService = inject(ApiKeyService);
//       const router = inject(Router);
//       if (apiKeyService.get()) {
//         return true;
//       } else {
//         router.navigate(['/auth']);
//         return false;
//       }
//     }]
//   },
//   {
//     path: 'templates/new',
//     component: TemplateEditorComponent,
//     canActivate: [() => {
//       const apiKeyService = inject(ApiKeyService);
//       const router = inject(Router);
//       if (apiKeyService.get()) {
//         return true;
//       } else {
//         router.navigate(['/auth']);
//         return false;
//       }
//     }]
//   },
//   {
//     path: 'certificates/generate',
//     component: GenerateComponent,
//     canActivate: [() => {
//       const apiKeyService = inject(ApiKeyService);
//       const router = inject(Router);
//       if (apiKeyService.get()) {
//         return true;
//       } else {
//         router.navigate(['/auth']);
//         return false;
//       }
//     }]
//   },
//   {
//     path: 'certificates/list',
//     component: CertificateListComponent,
//     canActivate: [() => {
//       const apiKeyService = inject(ApiKeyService);
//       const router = inject(Router);
//       if (apiKeyService.get()) {
//         return true;
//       } else {
//         router.navigate(['/auth']);
//         return false;
//       }
//     }]
//   },
//   { path: 'customers', component: CustomerComponent },
//   { path: 'verify/:hash', component: VerifyComponent },
//   { path: '**', redirectTo: 'auth' },
// ];

import { Routes } from '@angular/router';
import { inject } from '@angular/core';
import { Router } from '@angular/router';

import { ApiKeyComponent } from '../app/features/auth/api-key.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { TemplateListComponent } from './features/templates/template-list.component';
import { TemplateEditorComponent } from './features/templates/template-editor.component';
import { VerifyComponent } from './features/verify/verify.component';
import { GenerateComponent } from './features/certificates/generate.component';
import { CertificateListComponent } from './features/certificates/list.component';
import { CustomerComponent } from './features/customers/customer.component';
import { ApiKeyService } from './core/auth/api-key.service';
import { TemplateGenerateComponent } from './features/templates/template-generate.component';

export const routes: Routes = [

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
      { path: '', component: TemplateListComponent }, // default page
      { path: 'templates', component: TemplateListComponent },
      // { path: 'templates/new', component: TemplateEditorComponent },
      { path: 'templates/:id', component: TemplateEditorComponent },
      { path: 'templates/:id/generate', component: TemplateGenerateComponent },
      { path: 'certificates/generate', component: GenerateComponent },
      { path: 'certificates/list', component: CertificateListComponent },
      { path: 'verify', component: VerifyComponent },
      { path: 'customers', component: CustomerComponent }
    ]
  },

  // Public verification
  { path: 'verify/:hash', component: VerifyComponent },

  // Fallback
  { path: '**', redirectTo: 'auth' },

];
