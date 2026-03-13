import { Routes } from '@angular/router';
import { NoteListComponent } from './note-list.component';
import { NoteDetailComponent } from './note-detail.component';

export const notesRoutes: Routes = [
  {
    path: '',
    component: NoteListComponent
  },
  {
    path: ':id',
    component: NoteDetailComponent
  }
];
