import { Address } from './address.model';

export interface Customer {
  id?: number;
  firstname: string;
  lastname: string;
  email: string;
  address: Address;
}
