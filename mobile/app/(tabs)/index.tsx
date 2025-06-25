import React, { useEffect, useState } from 'react';
import { Text, View, ScrollView, ActivityIndicator } from 'react-native';
import Constants from 'expo-constants';
import axios from 'axios';

type Category = {
  id: number;
  name: string;
  // Add other fields if needed
};

const API_BASE_URL = Constants?.expoConfig?.extra?.API_BASE_URL || 'http://localhost:8080';

export default function HomeScreen() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    axios.get(`${API_BASE_URL}/api/categories`)
      .then(res => {
        console.log('Fetched categories response:', res);
        console.log('Fetched categories res.data:', res.data);
        const arr = Array.isArray(res.data) ? res.data : Array.isArray(res.data.data) ? res.data.data : [];
        setCategories(arr);
        setLoading(false);
      })
      .catch(err => {
        console.error('Error fetching categories:', err);
        setError('Failed to fetch categories');
        setLoading(false);
      });
  }, []);

  console.log('Categories state before render:', categories);
  return (
    <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center', padding: 20, backgroundColor: '#222' }}>
      <Text style={{ color: 'white', fontWeight: 'bold', fontSize: 18, marginBottom: 10 }}>DEBUG: HomeScreen Mounted</Text>
      <Text style={{ fontWeight: 'bold', fontSize: 18, marginBottom: 10 }}>Expense Tracker Mobile</Text>
      <Text>API Base URL: {API_BASE_URL}</Text>
      <Text style={{ color: 'white' }}>Categories count: {categories.length}</Text>
      <Text style={{ marginVertical: 10, fontWeight: 'bold' }}>Categories:</Text>
      {loading && <ActivityIndicator />}
      {error ? <Text style={{ color: 'red' }}>{error}</Text> : null}
      <ScrollView style={{ width: '100%' }}>
        {categories.map(cat => (
          <Text key={cat.id} style={{ color: 'white', padding: 5, borderBottomWidth: 1, borderColor: '#eee' }}>
            {cat.name}
          </Text>
        ))}
      </ScrollView>
    </View>
  );
} 