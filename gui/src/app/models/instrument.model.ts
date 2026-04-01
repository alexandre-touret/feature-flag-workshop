import { InstrumentType } from './instrument-type.enum';

export interface Instrument {
  id?: number;
  name: string;
  reference: string;
  manufacturer: string;
  price: number;
  description: string;
  type: InstrumentType;
}
