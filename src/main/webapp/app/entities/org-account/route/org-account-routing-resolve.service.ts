import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrgAccount, OrgAccount } from '../org-account.model';
import { OrgAccountService } from '../service/org-account.service';

@Injectable({ providedIn: 'root' })
export class OrgAccountRoutingResolveService implements Resolve<IOrgAccount> {
  constructor(protected service: OrgAccountService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOrgAccount> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((orgAccount: HttpResponse<OrgAccount>) => {
          if (orgAccount.body) {
            return of(orgAccount.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new OrgAccount());
  }
}
