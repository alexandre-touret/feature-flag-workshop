import {Injectable, signal} from '@angular/core';
import {User} from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly USER_KEY = 'music_store_user';
  private userSignal = signal<User | null>(this.loadUser());

  user = this.userSignal.asReadonly();

  setUser(user: User): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this.userSignal.set(user);
  }

  private loadUser(): User | null {
    const storedUser = localStorage.getItem(this.USER_KEY);
    if (storedUser) {
      try {
        return JSON.parse(storedUser);
      } catch (e) {
        console.error('Failed to parse stored user', e);
        return null;
      }
    }
    return null;
  }
}
