import { Routes } from '@angular/router';

export const beneficioRoutes: Routes = [
  {
    path: 'gerenciar',
    loadComponent: () => import('../../components/beneficio-slider/beneficio-slider.component')
      .then(m => m.BeneficioSliderComponent)
  },
  {
    path: 'extrato',
    loadComponent: () => import('../../components/extrato/extrato.component')
      .then(m => m.ExtratoComponent)
  }
];