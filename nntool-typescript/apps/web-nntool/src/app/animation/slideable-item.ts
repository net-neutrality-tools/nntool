
export abstract class SlideableItem {
    slideAnimationState: string;

    constructor () {
        this.slideAnimationState = 'hidden';
    }

    //for reasons the method is not always available when it is a class function => constructing it as static method works
    static toggleSlideAnimation(slideableItem: SlideableItem) {
        slideableItem.slideAnimationState = slideableItem.slideAnimationState === 'hidden' ? 'shown' : 'hidden';
    }
}