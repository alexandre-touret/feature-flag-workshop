import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {Observable} from 'rxjs';
import {map, shareReplay} from 'rxjs/operators';
import {UserDialogComponent} from '../../dialogs/user-dialog/user-dialog.component';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatDividerModule,
    MatDialogModule,
  ],
  template: `
    <mat-sidenav-container class="sidenav-container">
      <mat-sidenav #drawer class="sidenav" fixedInViewport
          [attr.role]="(isHandset$ | async) ? 'dialog' : 'navigation'"
          [mode]="(isHandset$ | async) ? 'over' : 'side'"
          [opened]="(isHandset$ | async) === false">
        <div class="logo-container">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M9 18V5L21 3V16" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="6" cy="18" r="3" stroke="white" stroke-width="2"/>
            <circle cx="18" cy="16" r="3" stroke="white" stroke-width="2"/>
          </svg>
        </div>
        <mat-divider></mat-divider>
        <mat-nav-list>
          <a mat-list-item routerLink="/instruments" routerLinkActive="active">
            <mat-icon matListItemIcon>music_note</mat-icon>
            <span matListItemTitle>Instruments</span>
          </a>
          <a mat-list-item routerLink="/orders" routerLinkActive="active">
            <mat-icon matListItemIcon>shopping_cart</mat-icon>
            <span matListItemTitle>Orders</span>
          </a>
        </mat-nav-list>
      </mat-sidenav>
      <mat-sidenav-content>
        <mat-toolbar color="primary">
          @if (isHandset$ | async) {
            <button
              type="button"
              aria-label="Toggle sidenav"
              mat-icon-button
              (click)="drawer.toggle()">
              <mat-icon aria-label="Side nav toggle icon">menu</mat-icon>
            </button>
          }
          <span>Music Store Manager</span>
          <span class="spacer"></span>
          <button mat-icon-button (click)="openUserDialog()">
            <mat-icon>account_circle</mat-icon>
          </button>
        </mat-toolbar>

        <main class="content">
          <router-outlet />
        </main>
      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
  styles: `
    .sidenav-container {
      height: 100%;
    }

    .sidenav {
      width: 240px;
      background-color: #3f51b5;
      color: white;
      border-radius: 0 !important;
    }

    .sidenav mat-icon,
    .sidenav .mat-list-item-title,
    .sidenav .logo-text,
    .sidenav .mat-mdc-list-item-title {
      color: white !important;
    }

    .sidenav mat-divider {
      border-top-color: rgba(255, 255, 255, 0.12);
    }

    .logo-container {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 16px;
      height: 80px;
      box-sizing: border-box;
    }

    .logo-text {
      font-weight: 500;
      font-size: 1.2rem;
      color: #3f51b5;
    }

    .sidenav .mat-toolbar {
      background: inherit;
    }

    .mat-toolbar.mat-primary {
      position: sticky;
      top: 0;
      z-index: 1;
    }

    .active {
      background: rgba(255, 255, 255, 0.15) !important;
      color: white !important;
    }

    .content {
      padding: 24px;
      max-width: 1200px;
      margin: 0 auto;
    }
  `
})
export class LayoutComponent {
  private breakpointObserver = inject(BreakpointObserver);
  private dialog = inject(MatDialog);
  private userService = inject(UserService);

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  openUserDialog(): void {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '450px',
      data: this.userService.user()
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.userService.setUser(result);
      }
    });
  }
}
