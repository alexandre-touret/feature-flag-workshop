import { Component, inject, input, resource, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { OrderService } from '../../../services/order.service';
import { OrderStatus } from '../../../models/order-status.enum';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-order-edit',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDividerModule,
    MatListModule,
    MatSnackBarModule,
    RouterModule
  ],
  template: `
    <div class="header">
      <button mat-icon-button routerLink="/orders">
        <mat-icon>arrow_back</mat-icon>
      </button>
      <h1>{{ isEditMode() ? 'Edit' : 'New' }} Order</h1>
    </div>

    @if (orderResource.isLoading()) {
      <div class="spinner-container">Loading...</div>
    } @else {
      <form [formGroup]="orderForm" (ngSubmit)="save()">
        <div class="form-container">
          <!-- Main Order Info -->
          <mat-card>
            <mat-card-header>
              <mat-card-title>Order Info</mat-card-title>
            </mat-card-header>
            <mat-card-content>
              <div class="form-row mt-1">
                <mat-form-field appearance="outline">
                  <mat-label>Reference</mat-label>
                  <input matInput formControlName="reference" required>
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Status</mat-label>
                  <mat-select formControlName="orderStatus" required>
                    @for (status of statuses; track status) {
                      <mat-option [value]="status">{{ status }}</mat-option>
                    }
                  </mat-select>
                </mat-form-field>
              </div>
            </mat-card-content>
          </mat-card>

          <!-- Customer Info -->
          <mat-card formGroupName="customer" class="mt-2">
            <mat-card-header>
              <mat-card-title>Customer</mat-card-title>
            </mat-card-header>
            <mat-card-content>
              <div class="form-row mt-1">
                <mat-form-field appearance="outline">
                  <mat-label>Firstname</mat-label>
                  <input matInput formControlName="firstname" required>
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Lastname</mat-label>
                  <input matInput formControlName="lastname" required>
                </mat-form-field>
              </div>
              <mat-form-field appearance="outline" class="full-width">
                <mat-label>Email</mat-label>
                <input matInput type="email" formControlName="email" required email>
              </mat-form-field>

              <div formGroupName="address">
                <mat-form-field appearance="outline" class="full-width">
                  <mat-label>Street</mat-label>
                  <input matInput formControlName="street" required>
                </mat-form-field>
                <div class="form-row">
                  <mat-form-field appearance="outline">
                    <mat-label>Zip Code</mat-label>
                    <input matInput formControlName="zipCode" required>
                  </mat-form-field>
                  <mat-form-field appearance="outline">
                    <mat-label>City</mat-label>
                    <input matInput formControlName="city" required>
                  </mat-form-field>
                  <mat-form-field appearance="outline">
                    <mat-label>Country</mat-label>
                    <input matInput formControlName="country" required>
                  </mat-form-field>
                </div>
              </div>
            </mat-card-content>
          </mat-card>

          <!-- Instruments List -->
          @if (isEditMode() && orderResource.value()?.instruments?.length) {
            <mat-card class="mt-2">
              <mat-card-header>
                <mat-card-title>Instruments</mat-card-title>
              </mat-card-header>
              <mat-card-content>
                <mat-list>
                  @for (instrument of orderResource.value()?.instruments; track instrument.id) {
                    <mat-list-item>
                      <mat-icon matListItemIcon>music_note</mat-icon>
                      <div matListItemTitle>{{ instrument.name }}</div>
                      <div matListItemLine>{{ instrument.manufacturer }} - {{ instrument.price | currency:'EUR' }}</div>
                    </mat-list-item>
                  }
                </mat-list>
              </mat-card-content>
            </mat-card>
          }

          <div class="actions mt-2 mb-2">
            <button mat-button type="button" routerLink="/orders">Cancel</button>
            <button mat-flat-button color="primary" type="submit" [disabled]="orderForm.invalid">
              Save
            </button>
          </div>
        </div>
      </form>
    }
  `,
  styles: `
    .header {
      display: flex;
      align-items: center;
      margin: 16px;
      gap: 16px;
    }
    .form-container {
      max-width: 800px;
      margin: 0 auto;
      padding: 16px;
    }
    .form-row {
      display: flex;
      gap: 16px;
    }
    .form-row mat-form-field {
      flex: 1;
    }
    .actions {
      display: flex;
      justify-content: flex-end;
      gap: 8px;
    }
    .mt-1 { margin-top: 8px; }
    .mt-2 { margin-top: 16px; }
    .mb-2 { margin-bottom: 32px; }
    .spinner-container { padding: 40px; text-align: center; }
  `
})
export class OrderEditComponent {
  private fb = inject(FormBuilder);
  private orderService = inject(OrderService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  id = input<string>();
  isEditMode = signal(false);
  statuses = Object.values(OrderStatus);

  orderForm = this.fb.group({
    id: [null as number | null],
    reference: ['', Validators.required],
    orderDate: [new Date().toISOString()],
    orderStatus: [OrderStatus.CREATED, Validators.required],
    customer: this.fb.group({
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      address: this.fb.group({
        street: ['', Validators.required],
        city: ['', Validators.required],
        zipCode: ['', Validators.required],
        country: ['', Validators.required]
      })
    })
  });

  orderResource = resource({
    request: () => this.id(),
    loader: async ({ request: id }) => {
      if (!id || id === 'new') return null;
      this.isEditMode.set(true);
      const data = await firstValueFrom(this.orderService.getOrder(Number(id)));
      this.orderForm.patchValue(data as any);
      return data;
    }
  });

  async save() {
    if (this.orderForm.invalid) return;

    const data = this.orderForm.value as any;
    try {
      if (this.isEditMode() && this.id()) {
        await firstValueFrom(this.orderService.updateOrder(Number(this.id()), data));
        this.snackBar.open('Order updated', 'Close', { duration: 3000 });
      } else {
        await firstValueFrom(this.orderService.createOrder(data));
        this.snackBar.open('Order created', 'Close', { duration: 3000 });
      }
      this.router.navigate(['/orders']);
    } catch (error) {
      this.snackBar.open('Error saving order', 'Close', { duration: 3000 });
    }
  }
}
