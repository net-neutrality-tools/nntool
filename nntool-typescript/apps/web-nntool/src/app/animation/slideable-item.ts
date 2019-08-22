
export abstract class SlideableItem {
    showSlideableItem = false;

    constructor () {
        this.showSlideableItem = false;
    }

    //for reasons the method is not always available when it is a class function => constructing it as static method works
    static toggleSlideAnimation(slideableItem: SlideableItem) {
        slideableItem.showSlideableItem = !slideableItem.showSlideableItem;
    }
}
