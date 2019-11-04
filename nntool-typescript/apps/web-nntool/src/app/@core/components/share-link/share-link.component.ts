import { Component, Input } from '@angular/core';

@Component({
  selector: 'nntool-cher',
  templateUrl: './share-link.component.html',
  styleUrls: ['./share-link.component.less']
})
export class ShareLinkComponent {
  @Input() message: string;
  @Input() selfUrl: string;
  @Input() medias: string[];

  public getLink(media: string) {
    return MediaUtils.getLink(media, this.message, this.selfUrl);
  }

  public getClass(media: string) {
    return MediaUtils.getClass(media);
  }

  public getSvgClass(media: string) {
    return MediaUtils.getSvgClass(media);
  }
}

const MEDIA_MAP = {
  FACEBOOK: {
    url: "https://facebook.com/sharer/sharer.php?u=_SELFURL_",
    cssClass: "facebook-share",
    svgClass: "icon-facebook"
  },
  TWITTER: {
    url: "https://twitter.com/intent/tweet?text=_PLACEHOLDER_",
    cssClass: "twitter-share",
    svgClass: "icon-twitter"
  },
  MAIL: {
    url: "mailto:?body=_PLACEHOLDER_%0A_SELFURL_",
    cssClass: "email-share",
    svgClass: "icon-email"
  },
  WHATSAPP: {
    url: "whatsapp://send?text=_SELFURL_",
    cssClass: "whatsapp-share",
    svgClass: "icon-whatsapp"
  }
}

class MediaUtils { 
  public static getLink(media: string, message: string, url: string) {
    debugger;
    return MEDIA_MAP[media].url.replace(/_PLACEHOLDER_/g, message.replace(/\n/g, "%0A").replace(/,/g, "%2C")).replace(/_SELFURL_/g, url);
  }

  public static getClass(media: string) {
    return MEDIA_MAP[media].cssClass;
  }

  public static getSvgClass(media: string) {
    return MEDIA_MAP[media].svgClass;
  }
}
