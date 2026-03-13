import { Component, inject, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

import { NoteDTO } from '../../api/model/noteDTO';

export interface DeleteDialogData {
  note: NoteDTO;
}

@Component({
  selector: 'app-delete-confirm-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule
  ],
  template: `
    <h2 mat-dialog-title>
      <mat-icon color="warn">warning</mat-icon>
      Notiz löschen
    </h2>

    <mat-dialog-content>
      <p>Möchten Sie die folgende Notiz wirklich löschen?</p>
      <div class="note-info">
        <strong>{{ data.note.title }}</strong>
      </div>
      <p class="warning-text">Diese Aktion kann nicht rückgängig gemacht werden.</p>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">
        <mat-icon>close</mat-icon>
        Abbrechen
      </button>
      <button mat-raised-button color="warn" (click)="onConfirm()">
        <mat-icon>delete</mat-icon>
        Löschen
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    h2 {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    mat-dialog-content {
      padding: 20px 24px;
      min-width: 350px;
    }

    .note-info {
      background-color: #f5f5f5;
      padding: 12px;
      border-radius: 4px;
      margin: 12px 0;
    }

    .warning-text {
      color: #d32f2f;
      font-size: 14px;
      margin-top: 12px;
    }

    mat-dialog-actions {
      padding: 16px 24px;
      gap: 8px;
    }
  `]
})
export class DeleteConfirmDialogComponent {
  public dialogRef = inject(MatDialogRef<DeleteConfirmDialogComponent>);

  constructor(@Inject(MAT_DIALOG_DATA) public data: DeleteDialogData) {}

  onConfirm() {
    this.dialogRef.close(true);
  }

  onCancel() {
    this.dialogRef.close(false);
  }
}
