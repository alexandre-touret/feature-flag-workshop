import { Component, inject, resource, signal, viewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';

import { OrderService } from '../../../services/order.service';
import { Order } from '../../../models/order.model';
import { OrderStatus } from '../../../models/order-status.enum';
import { ConfirmDialogComponent } from '../../../dialogs/confirm-dialog/confirm-dialog.component';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatCardModule,
    MatChipsModule
  ],
  template: `
    <div class="header">
      <h1>Orders</h1>
      <button mat-flat-button color="primary" routerLink="new">
        <mat-icon>add</mat-icon> New Order
      </button>
    </div>

    <mat-card class="m-2">
      <mat-card-content>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Filter</mat-label>
          <input matInput (keyup)="applyFilter($event)" placeholder="Ex. ORDER-123" #input>
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>

        @if (ordersResource.isLoading()) {
          <div class="spinner-container">
            <mat-progress-spinner mode="indeterminate"></mat-progress-spinner>
          </div>
        } @else {
          <div class="table-container">
            <table mat-table [dataSource]="dataSource" matSort>
              <ng-container matColumnDef="id">
                <th mat-header-cell *matHeaderCellDef mat-sort-header> ID </th>
                <td mat-cell *matCellDef="let row"> {{row.id}} </td>
              </ng-container>

              <ng-container matColumnDef="reference">
                <th mat-header-cell *matHeaderCellDef mat-sort-header> Reference </th>
                <td mat-cell *matCellDef="let row"> {{row.reference}} </td>
              </ng-container>

              <ng-container matColumnDef="orderDate">
                <th mat-header-cell *matHeaderCellDef mat-sort-header> Date </th>
                <td mat-cell *matCellDef="let row"> {{row.orderDate | date:'short'}} </td>
              </ng-container>

              <ng-container matColumnDef="customer">
                <th mat-header-cell *matHeaderCellDef mat-sort-header> Customer </th>
                <td mat-cell *matCellDef="let row"> {{row.customer.firstname}} {{row.customer.lastname}} </td>
              </ng-container>

              <ng-container matColumnDef="orderStatus">
                <th mat-header-cell *matHeaderCellDef mat-sort-header> Status </th>
                <td mat-cell *matCellDef="let row">
                  <mat-chip-set>
                    <mat-chip [color]="getStatusColor(row.orderStatus)" highlighted>
                      {{row.orderStatus}}
                    </mat-chip>
                  </mat-chip-set>
                </td>
              </ng-container>

              <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef> Actions </th>
                <td mat-cell *matCellDef="let row">
                  <button mat-icon-button color="primary" [routerLink]="['edit', row.id]">
                    <mat-icon>edit</mat-icon>
                  </button>
                  <button mat-icon-button color="warn" (click)="deleteOrder(row)">
                    <mat-icon>delete</mat-icon>
                  </button>
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
              <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>

              <tr class="mat-row" *matNoDataRow>
                <td class="mat-cell" colspan="6">No data matching the filter "{{input.value}}"</td>
              </tr>
            </table>

            <mat-paginator [pageSizeOptions]="[5, 10, 25, 100]" aria-label="Select page of orders"></mat-paginator>
          </div>
        }
      </mat-card-content>
    </mat-card>
  `,
  styles: `
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin: 16px;
    }
    .spinner-container {
      display: flex;
      justify-content: center;
      padding: 40px;
    }
    .table-container {
      overflow: auto;
    }
    table {
      width: 100%;
    }
  `
})
export class OrderListComponent {
  private orderService = inject(OrderService);
  private dialog = inject(MatDialog);

  displayedColumns: string[] = ['id', 'reference', 'orderDate', 'customer', 'orderStatus', 'actions'];
  dataSource = new MatTableDataSource<Order>();

  paginator = viewChild(MatPaginator);
  sort = viewChild(MatSort);

  ordersResource = resource({
    loader: async () => {
      const data = await firstValueFrom(this.orderService.getOrders());
      this.dataSource.data = data;
      setTimeout(() => {
        this.dataSource.paginator = this.paginator() ?? null;
        this.dataSource.sort = this.sort() ?? null;
      });
      return data;
    }
  });

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  getStatusColor(status: OrderStatus): string {
    switch (status) {
      case OrderStatus.PAID:
      case OrderStatus.DELIVERED: return 'accent';
      case OrderStatus.CANCELED: return 'warn';
      default: return 'primary';
    }
  }

  async deleteOrder(order: Order) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Delete Order',
        message: `Are you sure you want to delete order ${order.reference}?`
      }
    });

    const confirmed = await firstValueFrom(dialogRef.afterClosed());
    if (confirmed && order.id) {
      await firstValueFrom(this.orderService.deleteOrder(order.id));
      this.ordersResource.reload();
    }
  }
}
