import { Component, OnInit } from '@angular/core';
import { Person } from '../../models/person.model';
import { Group } from '../../models/group.model';
import { PersonService } from '../../services/person.service';
import { GroupService } from '../../services/group.service';

@Component({
  selector: 'app-group-list',
  templateUrl: './group-list.component.html'
})
export class GroupListComponent implements OnInit {

  people: Person[] = [];
  groups: Group[] = [];

  newPersonName = '';
  newGroupName = '';
  selectedMemberIds: number[] = [];

  errorMessage = '';

  constructor(
    private personService: PersonService,
    private groupService: GroupService
  ) {}

  ngOnInit(): void {
    this.loadPeople();
    this.loadGroups();
  }

  loadPeople(): void {
    this.personService.getAll().subscribe({
      next: (people) => this.people = people,
      error: () => this.errorMessage = 'Could not load people. Is the backend running on :8080?'
    });
  }

  loadGroups(): void {
    this.groupService.getAll().subscribe({
      next: (groups) => this.groups = groups,
      error: () => this.errorMessage = 'Could not load groups. Is the backend running on :8080?'
    });
  }

  addPerson(): void {
    if (!this.newPersonName.trim()) { return; }
    this.personService.create({ name: this.newPersonName.trim() }).subscribe({
      next: () => {
        this.newPersonName = '';
        this.loadPeople();
      },
      error: (err) => this.errorMessage = err?.error?.message || 'Failed to add person'
    });
  }

  toggleMember(personId: number, checked: boolean): void {
    if (checked) {
      this.selectedMemberIds = [...this.selectedMemberIds, personId];
    } else {
      this.selectedMemberIds = this.selectedMemberIds.filter(id => id !== personId);
    }
  }

  isSelected(personId: number): boolean {
    return this.selectedMemberIds.includes(personId);
  }

  createGroup(): void {
    if (!this.newGroupName.trim() || this.selectedMemberIds.length === 0) {
      this.errorMessage = 'Give the group a name and select at least one member';
      return;
    }
    this.groupService.create(this.newGroupName.trim(), this.selectedMemberIds).subscribe({
      next: () => {
        this.newGroupName = '';
        this.selectedMemberIds = [];
        this.errorMessage = '';
        this.loadGroups();
      },
      error: (err) => this.errorMessage = err?.error?.message || 'Failed to create group'
    });
  }
}
