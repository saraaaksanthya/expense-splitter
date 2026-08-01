import { Person } from './person.model';

export interface Group {
  id?: number;
  name: string;
  members: Person[];
}
