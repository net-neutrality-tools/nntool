export abstract class SlideableItem {
  // for reasons the method is not always available when it is a class function => constructing it as static method works
  public static toggleSlideAnimation(slideableItem: SlideableItem) {
    slideableItem.showSlideableItem = !slideableItem.showSlideableItem;
  }
  public showSlideableItem = false;

  constructor() {
    this.showSlideableItem = false;
  }
}
