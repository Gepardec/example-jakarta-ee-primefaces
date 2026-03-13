import { Injectable, inject } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private snackBar = inject(MatSnackBar);

  private defaultConfig: MatSnackBarConfig = {
    duration: 3000,
    horizontalPosition: 'end',
    verticalPosition: 'top'
  };

  showSuccess(message: string, action: string = 'OK') {
    this.snackBar.open(message, action, {
      ...this.defaultConfig,
      panelClass: ['snackbar-success']
    });
  }

  showError(message: string, action: string = 'OK') {
    this.snackBar.open(message, action, {
      ...this.defaultConfig,
      duration: 5000,
      panelClass: ['snackbar-error']
    });
  }

  showInfo(message: string, action: string = 'OK') {
    this.snackBar.open(message, action, {
      ...this.defaultConfig,
      panelClass: ['snackbar-info']
    });
  }

  showWarning(message: string, action: string = 'OK') {
    this.snackBar.open(message, action, {
      ...this.defaultConfig,
      duration: 4000,
      panelClass: ['snackbar-warning']
    });
  }
}
