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

  return next(req);
};
