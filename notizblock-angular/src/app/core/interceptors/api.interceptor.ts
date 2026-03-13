import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export const apiInterceptor: HttpInterceptorFn = (req, next) => {
  // Nur Requests zu relativen URLs modifizieren
  if (!req.url.startsWith('http')) {
    const apiReq = req.clone({
      url: `${environment.apiBaseUrl}${req.url}`
    });
    return next(apiReq);
  }
  return next(req);
};
