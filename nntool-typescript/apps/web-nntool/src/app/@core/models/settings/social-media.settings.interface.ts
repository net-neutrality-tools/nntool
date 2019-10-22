import { MediaEnum } from '../../components/share-link/share-link.component';

export interface SocialMediaSettings {
    openData?: {
        medias?: MediaEnum[];
    },

    history?: {
        medias?: MediaEnum[];
    }
}
  