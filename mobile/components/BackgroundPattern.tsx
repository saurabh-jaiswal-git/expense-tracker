import React from 'react';
import { StyleSheet } from 'react-native';
import Svg, { Path, Defs, LinearGradient, Stop, Circle } from 'react-native-svg';

export default function BackgroundPattern() {
  return (
    <Svg
      width="100%"
      height="100%"
      viewBox="0 0 400 800"
      style={StyleSheet.absoluteFill}
      preserveAspectRatio="none"
    >
      <Defs>
        <LinearGradient id="grad1" x1="0" y1="0" x2="1" y2="1">
          <Stop offset="0%" stopColor="#e6f7fa" stopOpacity="1" />
          <Stop offset="100%" stopColor="#f7e6fa" stopOpacity="1" />
        </LinearGradient>
        <LinearGradient id="grad2" x1="0" y1="1" x2="1" y2="0">
          <Stop offset="0%" stopColor="#a40a7e" stopOpacity="0.13" />
          <Stop offset="100%" stopColor="#7ea40a" stopOpacity="0.13" />
        </LinearGradient>
      </Defs>
      {/* Top wave with brand color */}
      <Path
        d="M0,0 Q200,80 400,0 L400,0 L0,0 Z"
        fill="#0a7ea4"
        opacity="0.10"
      />
      {/* Bottom blob with magenta-green gradient */}
      <Path
        d="M0,800 Q200,700 400,800 L400,800 L0,800 Z"
        fill="url(#grad2)"
      />
      {/* Center abstract shape with lime-green */}
      <Path
        d="M100,400 Q200,350 300,400 Q250,500 150,500 Q100,450 100,400 Z"
        fill="#7ea40a"
        opacity="0.10"
      />
      {/* Accent circle with magenta-purple */}
      <Circle
        cx="340"
        cy="120"
        r="60"
        fill="#a40a7e"
        opacity="0.10"
      />
      {/* Accent circle with brand color */}
      <Circle
        cx="60"
        cy="700"
        r="40"
        fill="#0a7ea4"
        opacity="0.13"
      />
      {/* Main background gradient overlay */}
      <Path
        d="M0,0 H400 V800 H0 Z"
        fill="url(#grad1)"
        opacity="0.95"
      />
    </Svg>
  );
} 