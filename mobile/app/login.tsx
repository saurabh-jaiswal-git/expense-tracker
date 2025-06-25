import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, ActivityIndicator, Alert, KeyboardAvoidingView, Platform, ScrollView } from 'react-native';
import * as SecureStore from '@/utils/SecureStore';
import axios from 'axios';
import Constants from 'expo-constants';
import BackgroundPattern from '@/components/BackgroundPattern';

const API_BASE_URL = Constants?.expoConfig?.extra?.API_BASE_URL || 'http://localhost:8080';

function validateEmail(email: string) {
  return /^\S+@\S+\.\S+$/.test(email);
}

export default function LoginScreen({ onLogin }: { onLogin: () => void }) {
  const [isSignup, setIsSignup] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [firstName, setFirstName] = useState('');
  const [loading, setLoading] = useState(false);

  const resetForm = () => {
    setEmail('');
    setPassword('');
    setConfirmPassword('');
    setFirstName('');
  };

  const handleLogin = async () => {
    if (!validateEmail(email)) {
      Alert.alert('Invalid Email', 'Please enter a valid email address.');
      return;
    }
    if (password.length < 6) {
      Alert.alert('Invalid Password', 'Password must be at least 6 characters.');
      return;
    }
    setLoading(true);
    try {
      const res = await axios.post(`${API_BASE_URL}/api/auth/login`, { email, password });
      if (res.data && res.data.token) {
        await SecureStore.setItemAsync('authToken', res.data.token);
        resetForm();
        onLogin();
      } else {
        Alert.alert('Login Failed', 'Invalid response from server.');
      }
    } catch (e: any) {
      const msg = e?.response?.data?.error || 'Invalid email or password.';
      Alert.alert('Login Failed', msg);
    } finally {
      setLoading(false);
    }
  };

  const handleSignup = async () => {
    if (!validateEmail(email)) {
      Alert.alert('Invalid Email', 'Please enter a valid email address.');
      return;
    }
    if (!firstName.trim()) {
      Alert.alert('First Name Required', 'Please enter your first name.');
      return;
    }
    if (password.length < 6) {
      Alert.alert('Invalid Password', 'Password must be at least 6 characters.');
      return;
    }
    if (password !== confirmPassword) {
      Alert.alert('Password Mismatch', 'Passwords do not match.');
      return;
    }
    setLoading(true);
    try {
      const res = await axios.post(`${API_BASE_URL}/api/users/register`, { email, password, firstName });
      if (res.status === 201 || res.status === 200) {
        Alert.alert('Signup Successful', 'You can now log in.');
        setIsSignup(false);
        resetForm();
      } else {
        Alert.alert('Signup Failed', 'Signup failed. Please try again.');
      }
    } catch (e: any) {
      const msg = e?.response?.data?.error || 'Signup failed. Email may already be registered.';
      Alert.alert('Signup Failed', msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView style={{ flex: 1 }} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <ScrollView contentContainerStyle={styles.container} keyboardShouldPersistTaps="handled">
        <BackgroundPattern />
        <View style={styles.logoContainer}>
          <View style={styles.logoCircle}>
            <Text style={styles.logoText} accessibilityRole="image" accessibilityLabel="WalletBuddy logo">💸</Text>
          </View>
        </View>
        <Text style={styles.title}>WalletBuddy</Text>
        <Text style={styles.subtitle}>{isSignup ? 'Create your account' : 'Sign in to your account'}</Text>
        {isSignup && (
          <TextInput
            style={styles.input}
            placeholder="First Name"
            placeholderTextColor="#bbb"
            autoCapitalize="words"
            value={firstName}
            onChangeText={setFirstName}
            accessibilityLabel="First Name"
            textContentType="givenName"
            autoComplete="given-name"
          />
        )}
        <TextInput
          style={styles.input}
          placeholder="Email"
          placeholderTextColor="#bbb"
          autoCapitalize="none"
          keyboardType="email-address"
          value={email}
          onChangeText={setEmail}
          accessibilityLabel="Email"
          textContentType="username"
          autoComplete="email"
        />
        <TextInput
          style={styles.input}
          placeholder="Password"
          placeholderTextColor="#bbb"
          secureTextEntry
          value={password}
          onChangeText={setPassword}
          accessibilityLabel="Password"
          textContentType={isSignup ? 'newPassword' : 'password'}
          autoComplete={isSignup ? 'new-password' : 'password'}
        />
        {isSignup && (
          <TextInput
            style={styles.input}
            placeholder="Confirm Password"
            placeholderTextColor="#bbb"
            secureTextEntry
            value={confirmPassword}
            onChangeText={setConfirmPassword}
            accessibilityLabel="Confirm Password"
            textContentType="newPassword"
            autoComplete="new-password"
          />
        )}
        <TouchableOpacity
          style={styles.button}
          onPress={isSignup ? handleSignup : handleLogin}
          disabled={loading}
          accessibilityRole="button"
          accessibilityLabel={isSignup ? 'Sign up' : 'Login'}
        >
          {loading ? <ActivityIndicator color="#fff" /> : <Text style={styles.buttonText}>{isSignup ? 'Sign Up' : 'Login'}</Text>}
        </TouchableOpacity>
        <TouchableOpacity
          onPress={() => { setIsSignup(!isSignup); resetForm(); }}
          style={styles.linkContainer}
          accessibilityRole="button"
          accessibilityLabel={isSignup ? 'Go to login' : 'Go to signup'}
        >
          <Text style={styles.link}>{isSignup ? 'Already have an account? Log In' : "Don't have an account? Sign Up"}</Text>
        </TouchableOpacity>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: {
    flexGrow: 1,
    backgroundColor: '#222',
    alignItems: 'center',
    justifyContent: 'center',
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
    fontSize: 32,
    fontWeight: 'bold',
    color: '#0a7ea4',
    marginBottom: 4,
    textAlign: 'center',
  },
  subtitle: {
    fontSize: 16,
    color: '#fff',
    marginBottom: 24,
    textAlign: 'center',
  },
  input: {
    width: '100%',
    maxWidth: 340,
    backgroundColor: '#222',
    color: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
    fontSize: 16,
    borderWidth: 2,
    borderColor: '#0a7ea4',
    shadowColor: '#000',
    shadowOpacity: 0.15,
    shadowRadius: 4,
    shadowOffset: { width: 0, height: 2 },
    elevation: 2,
  },
  button: {
    width: '100%',
    maxWidth: 340,
    backgroundColor: '#0a7ea4',
    borderRadius: 12,
    padding: 16,
    alignItems: 'center',
    marginTop: 8,
    shadowColor: '#0a7ea4',
    shadowOpacity: 0.15,
    shadowRadius: 6,
    shadowOffset: { width: 0, height: 2 },
    elevation: 2,
  },
  buttonText: {
    color: '#fff',
    fontWeight: 'bold',
    fontSize: 16,
  },
  linkContainer: {
    marginTop: 18,
  },
  link: {
    color: '#a40a7e',
    fontWeight: 'bold',
    fontSize: 15,
    textAlign: 'center',
  },
}); 