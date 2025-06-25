import * as SecureStore from 'expo-secure-store';

const isWeb = typeof window !== 'undefined' && typeof window.localStorage !== 'undefined';

export async function getItemAsync(key: string): Promise<string | null> {
  if (isWeb) {
    return window.localStorage.getItem(key);
  }
  return SecureStore.getItemAsync(key);
}

export async function setItemAsync(key: string, value: string): Promise<void> {
  if (isWeb) {
    window.localStorage.setItem(key, value);
    return;
  }
  return SecureStore.setItemAsync(key, value);
}

export async function deleteItemAsync(key: string): Promise<void> {
  if (isWeb) {
    window.localStorage.removeItem(key);
    return;
  }
  return SecureStore.deleteItemAsync(key);
} 