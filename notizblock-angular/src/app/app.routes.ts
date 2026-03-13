import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/notes',
    pathMatch: 'full'
  },
  {
    path: 'notes',
    loadChildren: () => import('./features/notes/notes.routes').then(m => m.notesRoutes)
  }
];
