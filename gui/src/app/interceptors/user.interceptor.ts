import {HttpInterceptorFn} from '@angular/common/http';
import {inject} from '@angular/core';
import {UserService} from '../services/user.service';

export const userInterceptor: HttpInterceptorFn = (req, next) => {
  const userService = inject(UserService);
  const user = userService.user();

  if (user) {
    const userJson = JSON.stringify(user);
    const authReq = req.clone({
      headers: req.headers.set('User', userJson)
    });
    return next(authReq);
  }

  // To prevent null exception on the backend, ensure at least an empty user structure
  // is passed or use a fallback if user is null.
  // Note: the service is already providing a defaultUser so it shouldn't be null,
  // but as a fail-safe we add a default header if missing.
  const fallbackUserJson = JSON.stringify({
    firstName: 'Unknown',
    lastName: 'Unknown',
    email: 'unknown@example.com',
    country: 'Unknown'
  });

  const safeReq = req.clone({
    headers: req.headers.set('User', fallbackUserJson)
  });

  return next(safeReq);
};
