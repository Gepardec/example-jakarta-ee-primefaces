import { Component, inject, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

import { NotesService } from '../../api/api/notes.service';
import { NoteDTO } from '../../api/model/noteDTO';
import { NotificationService } from '../../core/services/notification.service';

export interface DialogData {
  note: NoteDTO | null;
  editMode: boolean;
}

@Component({
  selector: 'app-note-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './note-form-dialog.component.html',
  styleUrl: './note-form-dialog.component.scss'
})
export class NoteFormDialogComponent implements OnInit {
  private fb = inject(FormBuilder);
  private notesService = inject(NotesService);
  private notificationService = inject(NotificationService);
  public dialogRef = inject(MatDialogRef<NoteFormDialogComponent>);

  noteForm!: FormGroup;
  isSubmitting = false;

  constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) {}

  ngOnInit() {
    this.initForm();
  }

  initForm() {
    this.noteForm = this.fb.group({
      title: [
        this.data.note?.title || '',
        [Validators.required, Validators.maxLength(255)]
      ],
      content: [
        this.data.note?.content || '',
        [Validators.required, Validators.maxLength(5000)]
      ]
    });
  }

  get title() {
    return this.noteForm.get('title');
  }

  get content() {
    return this.noteForm.get('content');
  }

  get dialogTitle(): string {
    return this.data.editMode ? 'Notiz bearbeiten' : 'Neue Notiz erstellen';
  }

  onSubmit() {
    if (this.noteForm.invalid) {
      this.noteForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    const noteData: NoteDTO = {
      ...this.noteForm.value
    };

    const operation = this.data.editMode && this.data.note?.id
      ? this.notesService.notizblockApiNotesIdPut(this.data.note.id, noteData)
      : this.notesService.notizblockApiNotesPost(noteData);

    operation.subscribe({
      next: () => {
        const message = this.data.editMode
          ? 'Notiz erfolgreich aktualisiert'
          : 'Notiz erfolgreich erstellt';
        this.notificationService.showSuccess(message);
        this.dialogRef.close(true);
      },
      error: (error) => {
        console.error('Error saving note:', error);
        this.notificationService.showError('Fehler beim Speichern der Notiz');
        this.isSubmitting = false;
      }
    });
  }

  onCancel() {
    this.dialogRef.close(false);
  }
}
