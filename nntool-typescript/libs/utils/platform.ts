declare var window;

/**
 * Electron helpers
 */
export function isElectron() {
  return typeof window !== 'undefined' && window.process && window.process.type;
}
