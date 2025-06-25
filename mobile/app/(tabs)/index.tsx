import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import BackgroundPattern from '@/components/BackgroundPattern';

export default function HomeScreen() {
  return (
    <View style={styles.container}>
      <BackgroundPattern />
      <View style={styles.logoContainer}>
        <View style={styles.logoCircle}>
          <Text style={styles.logoText}>💸</Text>
        </View>
      </View>
      <Text style={styles.title}>Welcome to WalletBuddy</Text>
      <Text style={styles.subtitle}>Your smart, AI-powered expense tracker</Text>
      <View style={styles.card}>
        <Text style={styles.cardTitle}>Get Started</Text>
        <Text style={styles.cardBody}>Add your first transaction, set a budget, or explore analytics from the tabs below.</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'transparent',
    padding: 24,
  },
  logoContainer: {
    marginBottom: 18,
    alignItems: 'center',
  },
  logoCircle: {
    width: 72,
    height: 72,
    borderRadius: 36,
    backgroundColor: '#0a7ea4',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 8,
    shadowColor: '#0a7ea4',
    shadowOpacity: 0.2,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 2 },
    elevation: 4,
  },
  logoText: {
    fontSize: 36,
    color: '#fff',
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#0a7ea4',
    marginBottom: 8,
    textAlign: 'center',
  },
  subtitle: {
    fontSize: 18,
    color: '#fff',
    marginBottom: 18,
    textAlign: 'center',
  },
  card: {
    backgroundColor: '#2d2d2d',
    borderRadius: 16,
    padding: 24,
    alignItems: 'center',
    shadowColor: '#0a7ea4',
    shadowOpacity: 0.08,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 2 },
    elevation: 2,
    marginTop: 8,
    maxWidth: 340,
  },
  cardTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#4ECDC4',
    marginBottom: 6,
    textAlign: 'center',
  },
  cardBody: {
    fontSize: 15,
    color: '#bbb',
    textAlign: 'center',
  },
}); 