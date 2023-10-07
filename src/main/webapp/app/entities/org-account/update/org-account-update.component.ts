import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IOrgAccount, OrgAccount } from '../org-account.model';
import { OrgAccountService } from '../service/org-account.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IOrganisation } from 'app/entities/organisation/organisation.model';
import { OrganisationService } from 'app/entities/organisation/service/organisation.service';

@Component({
  selector: 'jhi-org-account-update',
  templateUrl: './org-account-update.component.html',
})
export class OrgAccountUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  organisationsSharedCollection: IOrganisation[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    blocked: [],
    owner: [],
    org: [],
  });

  constructor(
    protected orgAccountService: OrgAccountService,
    protected userService: UserService,
    protected organisationService: OrganisationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orgAccount }) => {
      this.updateForm(orgAccount);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const orgAccount = this.createFromForm();
    if (orgAccount.id !== undefined) {
      this.subscribeToSaveResponse(this.orgAccountService.update(orgAccount));
    } else {
      this.subscribeToSaveResponse(this.orgAccountService.create(orgAccount));
    }
  }

  trackUserById(_index: number, item: IUser): string {
    return item.id!;
  }

  trackOrganisationById(_index: number, item: IOrganisation): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrgAccount>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(orgAccount: IOrgAccount): void {
    this.editForm.patchValue({
      id: orgAccount.id,
      name: orgAccount.name,
      blocked: orgAccount.blocked,
      owner: orgAccount.owner,
      org: orgAccount.org,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, orgAccount.owner);
    this.organisationsSharedCollection = this.organisationService.addOrganisationToCollectionIfMissing(
      this.organisationsSharedCollection,
      orgAccount.org
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('owner')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.organisationService
      .query()
      .pipe(map((res: HttpResponse<IOrganisation[]>) => res.body ?? []))
      .pipe(
        map((organisations: IOrganisation[]) =>
          this.organisationService.addOrganisationToCollectionIfMissing(organisations, this.editForm.get('org')!.value)
        )
      )
      .subscribe((organisations: IOrganisation[]) => (this.organisationsSharedCollection = organisations));
  }

  protected createFromForm(): IOrgAccount {
    return {
      ...new OrgAccount(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      blocked: this.editForm.get(['blocked'])!.value,
      owner: this.editForm.get(['owner'])!.value,
      org: this.editForm.get(['org'])!.value,
    };
  }
}
