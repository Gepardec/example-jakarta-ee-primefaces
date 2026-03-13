import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';

import { NotesService } from '../../api/api/notes.service';
import { NoteDTO } from '../../api/model/noteDTO';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-note-detail',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatChipsModule
  ],
  templateUrl: './note-detail.component.html',
  styleUrl: './note-detail.component.scss'
})
export class NoteDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private notesService = inject(NotesService);
  private notificationService = inject(NotificationService);

  note = signal<NoteDTO | null>(null);
  isLoading = signal<boolean>(true);

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadNote(Number(id));
    } else {
      this.router.navigate(['/notes']);
    }
  }

  loadNote(id: number) {
    this.isLoading.set(true);
    this.notesService.notizblockApiNotesIdGet(id).subscribe({
      next: (note) => {
        this.note.set(note);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading note:', error);
        this.notificationService.showError('Fehler beim Laden der Notiz');
        this.isLoading.set(false);
        this.router.navigate(['/notes']);
      }
    });
  }

  goBack() {
    this.router.navigate(['/notes']);
  }

  formatDate(date: string | undefined): string {
    if (!date) return '';
    return new Date(date).toLocaleString('de-DE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  }
}
