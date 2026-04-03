import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {User} from '../../models/user.model';

@Component({
  selector: 'app-user-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  template: `
    <h2 mat-dialog-title>{{ data ? 'Edit User' : 'Add User' }}</h2>
    <mat-dialog-content>
      <form [formGroup]="userForm" class="user-form">
        <mat-form-field appearance="outline">
          <mat-label>First Name</mat-label>
          <input matInput formControlName="firstName" placeholder="John">
          @if (userForm.get('firstName')?.hasError('required')) {
            <mat-error>First name is required</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Last Name</mat-label>
          <input matInput formControlName="lastName" placeholder="Doe">
          @if (userForm.get('lastName')?.hasError('required')) {
            <mat-error>Last name is required</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Email</mat-label>
          <input matInput formControlName="email" type="email" placeholder="john.doe@example.com">
          @if (userForm.get('email')?.hasError('required')) {
            <mat-error>Email is required</mat-error>
          }
          @if (userForm.get('email')?.hasError('email')) {
            <mat-error>Invalid email format</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Country</mat-label>
          <input matInput formControlName="country" placeholder="France">
          @if (userForm.get('country')?.hasError('required')) {
            <mat-error>Country is required</mat-error>
          }
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Cancel</button>
      <button mat-raised-button color="primary" [disabled]="userForm.invalid" (click)="onSave()">Save</button>
    </mat-dialog-actions>
  `,
  styles: `
    .user-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
      padding-top: 16px;
      min-width: 400px;
    }
  `
})
export class UserDialogComponent {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<UserDialogComponent>);
  data = inject<User | null>(MAT_DIALOG_DATA);

  userForm = this.fb.group({
    firstName: [this.data?.firstName || '', Validators.required],
    lastName: [this.data?.lastName || '', Validators.required],
    email: [this.data?.email || '', [Validators.required, Validators.email]],
    country: [this.data?.country || '', Validators.required]
  });

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.userForm.valid) {
      this.dialogRef.close(this.userForm.value);
    }
  }
}
