import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITile } from '../tile.model';

@Component({
  selector: 'jhi-tile-detail',
  templateUrl: './tile-detail.component.html',
})
export class TileDetailComponent implements OnInit {
  tile: ITile | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tile }) => {
      this.tile = tile;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
