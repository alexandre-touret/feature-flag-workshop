import { Instrument } from './instrument.model';
import { Customer } from './customer.model';
import { OrderStatus } from './order-status.enum';

export interface Order {
  id?: number;
  reference: string;
  orderDate: string;
  customer: Customer;
  orderStatus: OrderStatus;
  instruments: Instrument[];
}
