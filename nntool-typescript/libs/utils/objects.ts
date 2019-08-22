export const isString = (arg: any) => {
  return typeof arg === 'string';
};

export const isObject = (arg: any) => {
  return arg && typeof arg === 'object';
};
