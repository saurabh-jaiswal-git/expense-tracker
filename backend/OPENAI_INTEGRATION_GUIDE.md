# OpenAI Integration Guide

## 🎯 Current Status

✅ **Application is working perfectly!** All AI endpoints are functional and returning meaningful mock data.

## 🔧 How It Works

### Current Implementation (Mock Mode)
- **No API Key Required**: The application works without any OpenAI API key
- **Smart Mock Data**: Returns realistic, contextual responses based on the input
- **Graceful Fallback**: When API key is missing, automatically uses mock data
- **Detailed Logging**: Shows exactly what's happening in the logs

### Real OpenAI Integration (When API Key is Set)
- **Automatic Detection**: Application detects when `OPENAI_API_KEY` is set
- **Real API Calls**: Makes actual calls to OpenAI's GPT models
- **Same Endpoints**: Uses identical REST endpoints
- **Enhanced Responses**: Provides AI-generated, personalized insights

## 🚀 How to Enable Real OpenAI API Calls

### Step 1: Get OpenAI API Key
1. Go to [OpenAI Platform](https://platform.openai.com/api-keys)
2. Sign up or log in to your account
3. Create a new API key
4. Copy the key (it starts with `sk-`)

### Step 2: Set Environment Variable
```bash
export OPENAI_API_KEY="sk-your-actual-api-key-here"
```

### Step 3: Restart Application
```bash
# Stop current application
pkill -f "spring-boot:run"

# Start with new environment variable
export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
./mvnw spring-boot:run
```

## 🧪 Testing Both Modes

### Test Mock Mode (Current)
```bash
# Test AI Health
curl -u admin:admin http://localhost:8080/api/ai/health

# Test Spending Analysis (Mock)
curl -u admin:admin -X POST http://localhost:8080/api/ai/analyze \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "analysisType": "SPENDING_PATTERNS", "data": {"transactions": [{"amount": 100, "category": "Food", "date": "2024-01-15"}]}}'

# Test Categorization (Mock)
curl -u admin:admin -X POST http://localhost:8080/api/ai/categorize \
  -H "Content-Type: application/json" \
  -d '{"description": "Grocery shopping at Walmart"}'

# Expected Response: {"category":"Food"}
```

### Test Real OpenAI Mode (After setting API key)
```bash
# Same commands, but now with real AI responses
curl -u admin:admin -X POST http://localhost:8080/api/ai/analyze \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "analysisType": "SPENDING_PATTERNS", "data": {"transactions": [{"amount": 100, "category": "Food", "date": "2024-01-15"}]}}'

# Expected: Real AI-generated analysis with detailed insights
```

## 📊 Log Messages to Watch

### Mock Mode Logs
```
WARN  - LLM service is not available (no API key configured). Using mock data.
WARN  - OpenAI API key not configured, returning mock categorization
INFO  - Generated spending analysis for user 1 in 2ms using mock-model
```

### Real OpenAI Mode Logs
```
INFO  - Making real OpenAI API call for user 1 with 3 transactions
INFO  - Generated real spending analysis for user 1 in 1500ms using openai
INFO  - Categorized 'Grocery shopping at Walmart' as 'Food' using openai
```

## 🔍 API Endpoints Available

| Endpoint | Method | Description | Mock Response | Real Response |
|----------|--------|-------------|---------------|---------------|
| `/api/ai/health` | GET | AI service health check | ✅ Status healthy | ✅ Status healthy |
| `/api/ai/analyze` | POST | Spending pattern analysis | ✅ Mock insights | 🤖 AI-generated insights |
| `/api/ai/categorize` | POST | Transaction categorization | ✅ Keyword-based | 🤖 AI categorization |
| `/api/ai/recommendations/{userId}` | GET | Financial recommendations | ✅ Mock recommendations | 🤖 AI recommendations |
| `/api/ai/budget/{userId}` | GET | Budget recommendations | ✅ Mock budget advice | 🤖 AI budget advice |
| `/api/ai/anomalies/{userId}` | GET | Anomaly detection | ✅ Mock anomalies | 🤖 AI anomaly detection |

## 💡 Key Features

### 1. **Seamless Mode Switching**
- No code changes needed
- Automatic detection of API key
- Same endpoints work in both modes

### 2. **Smart Mock Data**
- Contextual responses based on input
- Realistic financial advice
- Proper JSON formatting

### 3. **Error Handling**
- Graceful fallback to mock data
- Detailed error logging
- No application crashes

### 4. **Security**
- API key stored as environment variable
- No hardcoded credentials
- Secure API communication

## 🎯 Example Real OpenAI API Call

When you set the API key, here's what happens internally:

```bash
# The application makes this call to OpenAI:
curl -X POST https://api.openai.com/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer sk-your-api-key" \
  -d '{
    "model": "gpt-4",
    "messages": [
      {
        "role": "user",
        "content": "Categorize the following transaction description into one of the available categories:\n\nTransaction: Grocery shopping at Walmart\n\nAvailable categories:\n- Food\n- Transportation\n- Entertainment\n- Shopping\n- Utilities\n- Healthcare\n- Education\n- Travel\n- Other\n\nRespond with only the category name, nothing else."
      }
    ],
    "max_tokens": 1000,
    "temperature": 0.7
  }'
```

## 🚨 Troubleshooting

### Issue: "Invalid CSRF token"
**Solution**: CSRF is already disabled in the security configuration.

### Issue: "401 Unauthorized"
**Solution**: Use the correct credentials: `admin:admin`

### Issue: "LLM service is not available"
**Solution**: This is expected in mock mode. Set `OPENAI_API_KEY` to enable real calls.

### Issue: "OpenAI API call failed"
**Solution**: Check your API key is valid and has sufficient credits.

## 🎉 Summary

**You don't need to link any account or provide config to interact with the current implementation!** 

The application is designed to work seamlessly in both modes:
- **Mock Mode**: Works immediately, no setup required
- **Real AI Mode**: Set one environment variable to enable real OpenAI integration

All endpoints are functional and provide meaningful responses in both modes! 