import { Component, Input } from '@angular/core';

@Component({
  selector: 'nntool-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.less']
})
export class CardComponent {
  @Input() headerTranslationKey: string;
  @Input() contentTranslationKey: string;

  @Input() icon: string;
}
