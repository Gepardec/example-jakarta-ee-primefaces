import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'Ein unbekannter Fehler ist aufgetreten';

      if (error.error instanceof ErrorEvent) {
        // Client-seitiger Fehler
        errorMessage = `Fehler: ${error.error.message}`;
      } else {
        // Server-seitiger Fehler
        errorMessage = error.error?.message || `Server Fehler: ${error.status}`;
      }

      notificationService.showError(errorMessage);
      return throwError(() => error);
    })
  );
};
