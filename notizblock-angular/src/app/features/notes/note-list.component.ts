import { Component, inject, OnInit, ViewChild, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Router } from '@angular/router';

import { NotesService } from '../../api/api/notes.service';
import { NoteDTO } from '../../api/model/noteDTO';
import { NotificationService } from '../../core/services/notification.service';
import { NoteFormDialogComponent } from './note-form-dialog.component';
import { DeleteConfirmDialogComponent } from './delete-confirm-dialog.component';

@Component({
  selector: 'app-note-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatCardModule,
    MatDialogModule
  ],
  templateUrl: './note-list.component.html',
  styleUrl: './note-list.component.scss'
})
export class NoteListComponent implements OnInit {
  private notesService = inject(NotesService);
  private notificationService = inject(NotificationService);
  private dialog = inject(MatDialog);
  private router = inject(Router);

  displayedColumns: string[] = ['title', 'content', 'createdAt', 'actions'];
  dataSource = new MatTableDataSource<NoteDTO>();
  notesCount = signal<number>(0);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  ngOnInit() {
    this.loadNotes();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  loadNotes() {
    this.notesService.notizblockApiNotesGet().subscribe({
      next: (notes) => {
        this.dataSource.data = notes;
        this.notesCount.set(notes.length);
      },
      error: (error) => {
        console.error('Error loading notes:', error);
        this.notificationService.showError('Fehler beim Laden der Notizen');
      }
    });
  }

  openCreateDialog() {
    const dialogRef = this.dialog.open(NoteFormDialogComponent, {
      width: '600px',
      data: { note: null, editMode: false }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadNotes();
      }
    });
  }

  openEditDialog(note: NoteDTO) {
    const dialogRef = this.dialog.open(NoteFormDialogComponent, {
      width: '600px',
      data: { note: { ...note }, editMode: true }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadNotes();
      }
    });
  }

  openDeleteDialog(note: NoteDTO) {
    const dialogRef = this.dialog.open(DeleteConfirmDialogComponent, {
      width: '400px',
      data: { note }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteNote(note);
      }
    });
  }

  deleteNote(note: NoteDTO) {
    if (!note.id) return;

    this.notesService.notizblockApiNotesIdDelete(note.id).subscribe({
      next: () => {
        this.notificationService.showSuccess('Notiz erfolgreich gelöscht');
        this.loadNotes();
      },
      error: (error) => {
        console.error('Error deleting note:', error);
        this.notificationService.showError('Fehler beim Löschen der Notiz');
      }
    });
  }

  viewDetail(note: NoteDTO) {
    this.router.navigate(['/notes', note.id]);
  }

  getTruncatedContent(content: string | undefined, maxLength: number = 100): string {
    if (!content) return '';
    return content.length > maxLength
      ? content.substring(0, maxLength) + '...'
      : content;
  }

  formatDate(date: string | undefined): string {
    if (!date) return '';
    return new Date(date).toLocaleString('de-DE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
