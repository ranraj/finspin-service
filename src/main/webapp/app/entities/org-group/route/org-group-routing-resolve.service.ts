import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrgGroup, OrgGroup } from '../org-group.model';
import { OrgGroupService } from '../service/org-group.service';

@Injectable({ providedIn: 'root' })
export class OrgGroupRoutingResolveService implements Resolve<IOrgGroup> {
  constructor(protected service: OrgGroupService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOrgGroup> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((orgGroup: HttpResponse<OrgGroup>) => {
          if (orgGroup.body) {
            return of(orgGroup.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new OrgGroup());
  }
}
