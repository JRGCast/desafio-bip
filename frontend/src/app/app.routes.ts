import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'beneficio',
    loadComponent: () => import('./pages/beneficio/beneficio.component')
      .then(m => m.BeneficioComponent),
    children: [
      {
        path: '',
        redirectTo: 'gerenciar',
        pathMatch: 'full'
      },
      {
        path: 'gerenciar',
        loadComponent: () => import('./components/beneficio-slider/beneficio-slider.component')
          .then(m => m.BeneficioSliderComponent)
      },
      {
        path: 'extrato',
        loadComponent: () => import('./components/extrato/extrato.component')
          .then(m => m.ExtratoComponent)
      }
    ]
  },
  {
    path: '',
    redirectTo: 'beneficio',
    pathMatch: 'full'
  }
];