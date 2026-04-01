import { Routes } from '@angular/router';
import { LayoutComponent } from './components/layout/layout.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      {
        path: '',
        redirectTo: 'instruments',
        pathMatch: 'full'
      },
      {
        path: 'instruments',
        loadComponent: () => import('./pages/instruments/instrument-list/instrument-list.component').then(m => m.InstrumentListComponent)
      },
      {
        path: 'instruments/new',
        loadComponent: () => import('./pages/instruments/instrument-edit/instrument-edit.component').then(m => m.InstrumentEditComponent)
      },
      {
        path: 'instruments/edit/:id',
        loadComponent: () => import('./pages/instruments/instrument-edit/instrument-edit.component').then(m => m.InstrumentEditComponent)
      },
      {
        path: 'orders',
        loadComponent: () => import('./pages/orders/order-list/order-list.component').then(m => m.OrderListComponent)
      },
      {
        path: 'orders/new',
        loadComponent: () => import('./pages/orders/order-edit/order-edit.component').then(m => m.OrderEditComponent)
      },
      {
        path: 'orders/edit/:id',
        loadComponent: () => import('./pages/orders/order-edit/order-edit.component').then(m => m.OrderEditComponent)
      }
    ]
  }
];
