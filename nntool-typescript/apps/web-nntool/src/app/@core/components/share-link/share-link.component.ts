import { Component, Input } from '@angular/core';

@Component({
  selector: 'nntool-cher',
  templateUrl: './share-link.component.html'
})
export class ShareLinkComponent {
  @Input() message: string;
  @Input() selfUrl: string;
  @Input() medias: MediaEnum[];

  public getLink(media: MediaEnum) {
    return MediaUtils.getLink(media, this.message, this.selfUrl);
  }
}

export enum MediaEnum {
  FACEBOOK,
  TWITTER,
  MAIL
}

const MEDIA_MAP = {
  FACEBOOK: "https://facebook.com/sharer/sharer.php?u=_SELFURL_",
  TWITTER: "https://twitter.com/intent/tweet?text=_PLACEHOLDER_",
  MAIL: "mailto:?body=_PLACEHOLDER_%0A_SELFURL_"
}

class MediaUtils { 
  public static getLink(media: MediaEnum, message: string, url: string) {
    return MEDIA_MAP[media].replace(/_PLACEHOLDER_/g, message.replace(/\n/g, "%0A").replace(/,/g, "%2C")).replace(/_SELFURL_/g, url);
  }
}