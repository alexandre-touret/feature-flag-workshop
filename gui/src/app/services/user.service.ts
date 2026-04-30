import {Injectable, signal} from '@angular/core';
import {User} from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly USER_KEY = 'music_store_user';
  private readonly defaultUser: User = {
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    country: 'US'
  };

  private userSignal = signal<User | null>(this.loadUser());

  user = this.userSignal.asReadonly();

  setUser(user: User): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this.userSignal.set(user);
  }

  getCurrentUser(): User | null {
    return this.userSignal();
  }


  private loadUser(): User | null {
    const storedUser = localStorage.getItem(this.USER_KEY);
    if (storedUser) {
      try {
        return JSON.parse(storedUser);
      } catch (e) {
        console.error('Failed to parse stored user', e);
        return this.defaultUser;
      }
    }
    return this.defaultUser;
  }
}
