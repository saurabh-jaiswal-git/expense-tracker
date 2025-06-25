# Frontend Specification for Expense Tracker Analytics

## Overview
This document defines the frontend architecture, components, and user experience for presenting analytics and LLM-powered insights in the expense tracker application.

## Technology Stack

### Core Framework
- **React 18** with TypeScript
- **Next.js 14** for SSR and routing
- **Tailwind CSS** for styling
- **Framer Motion** for animations

### State Management
- **Zustand** for global state
- **React Query** for server state management
- **SWR** for data fetching and caching

### Chart Libraries
- **Recharts** for basic charts (bar, line, pie)
- **D3.js** for custom visualizations
- **Chart.js** for additional chart types
- **ApexCharts** for advanced interactive charts

### UI Components
- **Headless UI** for accessible components
- **Radix UI** for complex components
- **React Hook Form** for forms
- **React Hot Toast** for notifications

---

## Application Architecture

### Page Structure
```
/app
├── dashboard/
│   ├── page.tsx                 # Main dashboard
│   ├── spending/
│   │   ├── page.tsx            # Spending analysis
│   │   └── trends.tsx          # Spending trends
│   ├── budget/
│   │   ├── page.tsx            # Budget overview
│   │   └── performance.tsx     # Budget performance
│   ├── goals/
│   │   ├── page.tsx            # Goals overview
│   │   └── progress.tsx        # Goal progress
│   ├── analytics/
│   │   ├── page.tsx            # AI insights
│   │   ├── chat.tsx            # Conversational analytics
│   │   └── recommendations.tsx # Personalized recommendations
│   └── reports/
│       ├── page.tsx            # Reports overview
│       └── [reportId].tsx      # Individual report
├── components/
│   ├── analytics/
│   ├── charts/
│   ├── insights/
│   └── common/
└── hooks/
    ├── useAnalytics.ts
    ├── useLLM.ts
    └── useCharts.ts
```

---

## Dashboard Overview

### Main Dashboard Layout
```tsx
// app/dashboard/page.tsx
export default function Dashboard() {
  return (
    <div className="min-h-screen bg-gray-50">
      <DashboardHeader />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Quick Stats */}
          <QuickStatsSection />
          
          {/* Main Content */}
          <div className="lg:col-span-2">
            <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
              <SpendingOverview />
              <BudgetStatus />
              <GoalProgress />
              <RecentInsights />
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
```

### Quick Stats Section
```tsx
// components/analytics/QuickStatsSection.tsx
export function QuickStatsSection() {
  const { data: stats } = useQuery(['quick-stats'], fetchQuickStats)
  
  return (
    <div className="space-y-6">
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">
          This Month
        </h3>
        <div className="space-y-4">
          <StatCard
            title="Total Spending"
            value={formatCurrency(stats?.totalSpending)}
            change={stats?.spendingChange}
            trend="up"
            color="red"
          />
          <StatCard
            title="Income"
            value={formatCurrency(stats?.totalIncome)}
            change={stats?.incomeChange}
            trend="up"
            color="green"
          />
          <StatCard
            title="Savings"
            value={formatCurrency(stats?.netSavings)}
            change={stats?.savingsChange}
            trend="up"
            color="blue"
          />
          <StatCard
            title="Budget Status"
            value={stats?.budgetStatus}
            change={stats?.budgetUtilization}
            trend={stats?.budgetTrend}
            color="yellow"
          />
        </div>
      </div>
    </div>
  )
}
```

---

## Spending Analysis Page

### Spending Overview Component
```tsx
// components/analytics/SpendingOverview.tsx
export function SpendingOverview() {
  const { data: spendingData } = useQuery(['spending-analysis'], fetchSpendingAnalysis)
  
  return (
    <div className="bg-white rounded-lg shadow">
      <div className="p-6 border-b border-gray-200">
        <div className="flex items-center justify-between">
          <h2 className="text-xl font-semibold text-gray-900">
            Spending Analysis
          </h2>
          <PeriodSelector />
        </div>
      </div>
      
      <div className="p-6">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Category Breakdown */}
          <div>
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Spending by Category
            </h3>
            <CategoryPieChart data={spendingData?.spendingByCategory} />
          </div>
          
          {/* Top Categories */}
          <div>
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Top Spending Categories
            </h3>
            <TopCategoriesList data={spendingData?.topCategories} />
          </div>
        </div>
        
        {/* Trends Chart */}
        <div className="mt-8">
          <h3 className="text-lg font-medium text-gray-900 mb-4">
            Spending Trends
          </h3>
          <SpendingTrendsChart data={spendingData?.trends} />
        </div>
        
        {/* Insights */}
        <div className="mt-8">
          <h3 className="text-lg font-medium text-gray-900 mb-4">
            AI Insights
          </h3>
          <InsightsList insights={spendingData?.insights} />
        </div>
      </div>
    </div>
  )
}
```

### Category Pie Chart
```tsx
// components/charts/CategoryPieChart.tsx
export function CategoryPieChart({ data }) {
  return (
    <div className="h-64">
      <ResponsiveContainer width="100%" height="100%">
        <PieChart>
          <Pie
            data={data}
            cx="50%"
            cy="50%"
            outerRadius={80}
            fill="#8884d8"
            dataKey="amount"
            label={({ name, percentage }) => `${name} ${percentage}%`}
          >
            {data?.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={entry.color} />
            ))}
          </Pie>
          <Tooltip formatter={(value) => formatCurrency(value)} />
        </PieChart>
      </ResponsiveContainer>
    </div>
  )
}
```

---

## Budget Performance Page

### Budget Status Component
```tsx
// components/analytics/BudgetStatus.tsx
export function BudgetStatus() {
  const { data: budgetData } = useQuery(['budget-performance'], fetchBudgetPerformance)
  
  return (
    <div className="bg-white rounded-lg shadow">
      <div className="p-6 border-b border-gray-200">
        <h2 className="text-xl font-semibold text-gray-900">
          Budget Performance
        </h2>
      </div>
      
      <div className="p-6">
        {/* Overall Budget Status */}
        <div className="mb-8">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-medium text-gray-900">
              Overall Budget
            </h3>
            <BudgetStatusBadge status={budgetData?.budgetSummary?.status} />
          </div>
          
          <div className="grid grid-cols-3 gap-4 mb-4">
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-900">
                {formatCurrency(budgetData?.budgetSummary?.totalBudget)}
              </div>
              <div className="text-sm text-gray-500">Budget</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-900">
                {formatCurrency(budgetData?.budgetSummary?.actualSpending)}
              </div>
              <div className="text-sm text-gray-500">Spent</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-900">
                {formatCurrency(budgetData?.budgetSummary?.remainingBudget)}
              </div>
              <div className="text-sm text-gray-500">Remaining</div>
            </div>
          </div>
          
          <BudgetProgressBar
            spent={budgetData?.budgetSummary?.actualSpending}
            total={budgetData?.budgetSummary?.totalBudget}
          />
        </div>
        
        {/* Category Breakdown */}
        <div>
          <h3 className="text-lg font-medium text-gray-900 mb-4">
            Category Breakdown
          </h3>
          <CategoryBudgetList data={budgetData?.categoryBreakdown} />
        </div>
      </div>
    </div>
  )
}
```

### Budget Progress Bar
```tsx
// components/charts/BudgetProgressBar.tsx
export function BudgetProgressBar({ spent, total }) {
  const percentage = (spent / total) * 100
  const isOverBudget = percentage > 100
  
  return (
    <div className="w-full">
      <div className="flex justify-between text-sm text-gray-600 mb-2">
        <span>Budget Utilization</span>
        <span>{percentage.toFixed(1)}%</span>
      </div>
      <div className="w-full bg-gray-200 rounded-full h-3">
        <div
          className={`h-3 rounded-full transition-all duration-300 ${
            isOverBudget ? 'bg-red-500' : 'bg-green-500'
          }`}
          style={{ width: `${Math.min(percentage, 100)}%` }}
        />
      </div>
      {isOverBudget && (
        <div className="text-sm text-red-600 mt-1">
          {formatCurrency(spent - total)} over budget
        </div>
      )}
    </div>
  )
}
```

---

## AI Insights Page

### AI Insights Component
```tsx
// components/analytics/AIInsights.tsx
export function AIInsights() {
  const [analysisType, setAnalysisType] = useState('spending_patterns')
  const [timeframe, setTimeframe] = useState('last_3_months')
  
  const { data: insights, isLoading, refetch } = useQuery(
    ['ai-insights', analysisType, timeframe],
    () => fetchAIInsights({ analysisType, timeframe }),
    { enabled: false }
  )
  
  return (
    <div className="max-w-4xl mx-auto">
      <div className="bg-white rounded-lg shadow">
        <div className="p-6 border-b border-gray-200">
          <h1 className="text-2xl font-bold text-gray-900">
            AI-Powered Insights
          </h1>
          <p className="text-gray-600 mt-2">
            Get personalized financial insights and recommendations powered by AI
          </p>
        </div>
        
        <div className="p-6">
          {/* Analysis Controls */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Analysis Type
              </label>
              <select
                value={analysisType}
                onChange={(e) => setAnalysisType(e.target.value)}
                className="w-full border border-gray-300 rounded-md px-3 py-2"
              >
                <option value="spending_patterns">Spending Patterns</option>
                <option value="budget_optimization">Budget Optimization</option>
                <option value="savings_opportunities">Savings Opportunities</option>
                <option value="goal_progress">Goal Progress</option>
              </select>
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Timeframe
              </label>
              <select
                value={timeframe}
                onChange={(e) => setTimeframe(e.target.value)}
                className="w-full border border-gray-300 rounded-md px-3 py-2"
              >
                <option value="last_month">Last Month</option>
                <option value="last_3_months">Last 3 Months</option>
                <option value="last_6_months">Last 6 Months</option>
                <option value="last_year">Last Year</option>
              </select>
            </div>
          </div>
          
          <button
            onClick={() => refetch()}
            disabled={isLoading}
            className="w-full bg-blue-600 text-white py-3 px-4 rounded-md hover:bg-blue-700 disabled:opacity-50"
          >
            {isLoading ? 'Generating Insights...' : 'Generate Insights'}
          </button>
          
          {/* Insights Display */}
          {insights && (
            <div className="mt-8 space-y-6">
              <InsightCard insight={insights} />
              <RecommendationsList recommendations={insights.recommendations} />
              <AnomaliesList anomalies={insights.anomalies} />
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
```

### Insight Card
```tsx
// components/insights/InsightCard.tsx
export function InsightCard({ insight }) {
  return (
    <div className="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-lg p-6">
      <div className="flex items-start space-x-4">
        <div className="flex-shrink-0">
          <div className="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center">
            <BrainIcon className="w-6 h-6 text-white" />
          </div>
        </div>
        
        <div className="flex-1">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            AI Analysis
          </h3>
          <p className="text-gray-700 leading-relaxed">
            {insight.insights}
          </p>
          
          <div className="mt-4 flex items-center space-x-4 text-sm text-gray-500">
            <span>Confidence: {(insight.confidenceScore * 100).toFixed(0)}%</span>
            <span>Model: {insight.modelUsed}</span>
            <span>Processing: {insight.processingTime}ms</span>
          </div>
        </div>
      </div>
    </div>
  )
}
```

---

## Conversational Analytics Page

### Chat Interface
```tsx
// components/analytics/ChatInterface.tsx
export function ChatInterface() {
  const [messages, setMessages] = useState([])
  const [input, setInput] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  
  const sendMessage = async () => {
    if (!input.trim()) return
    
    const userMessage = { type: 'user', content: input, timestamp: new Date() }
    setMessages(prev => [...prev, userMessage])
    setInput('')
    setIsLoading(true)
    
    try {
      const response = await fetch('/api/analytics/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ question: input })
      })
      
      const data = await response.json()
      const aiMessage = {
        type: 'ai',
        content: data.data.answer,
        data: data.data.data,
        followUpQuestions: data.data.followUpQuestions,
        timestamp: new Date()
      }
      
      setMessages(prev => [...prev, aiMessage])
    } catch (error) {
      console.error('Error sending message:', error)
    } finally {
      setIsLoading(false)
    }
  }
  
  return (
    <div className="max-w-4xl mx-auto h-screen flex flex-col">
      <div className="bg-white rounded-lg shadow flex-1 flex flex-col">
        <div className="p-6 border-b border-gray-200">
          <h1 className="text-2xl font-bold text-gray-900">
            Ask About Your Finances
          </h1>
          <p className="text-gray-600 mt-2">
            Ask questions about your spending, budget, goals, and more
          </p>
        </div>
        
        {/* Messages */}
        <div className="flex-1 overflow-y-auto p-6 space-y-4">
          {messages.map((message, index) => (
            <MessageBubble key={index} message={message} />
          ))}
          
          {isLoading && (
            <div className="flex items-center space-x-2 text-gray-500">
              <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600" />
              <span>AI is thinking...</span>
            </div>
          )}
        </div>
        
        {/* Input */}
        <div className="p-6 border-t border-gray-200">
          <div className="flex space-x-4">
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && sendMessage()}
              placeholder="Ask about your finances..."
              className="flex-1 border border-gray-300 rounded-md px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
            <button
              onClick={sendMessage}
              disabled={isLoading || !input.trim()}
              className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 disabled:opacity-50"
            >
              Send
            </button>
          </div>
          
          {/* Quick Questions */}
          <div className="mt-4 flex flex-wrap gap-2">
            {['How much did I spend this month?', 'What\'s my budget status?', 'How are my goals progressing?'].map((question) => (
              <button
                key={question}
                onClick={() => setInput(question)}
                className="text-sm bg-gray-100 text-gray-700 px-3 py-1 rounded-full hover:bg-gray-200"
              >
                {question}
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
```

### Message Bubble
```tsx
// components/analytics/MessageBubble.tsx
export function MessageBubble({ message }) {
  const isUser = message.type === 'user'
  
  return (
    <div className={`flex ${isUser ? 'justify-end' : 'justify-start'}`}>
      <div className={`max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${
        isUser 
          ? 'bg-blue-600 text-white' 
          : 'bg-gray-100 text-gray-900'
      }`}>
        <p className="text-sm">{message.content}</p>
        
        {message.data && (
          <div className="mt-2 p-2 bg-white bg-opacity-10 rounded">
            <div className="text-xs">
              <div>Amount: {formatCurrency(message.data.amount)}</div>
              <div>Percentage: {message.data.percentage}%</div>
              <div>Trend: {message.data.trend}%</div>
            </div>
          </div>
        )}
        
        {message.followUpQuestions && (
          <div className="mt-2 space-y-1">
            {message.followUpQuestions.map((question, index) => (
              <button
                key={index}
                className="block text-xs text-blue-200 hover:text-white text-left"
              >
                {question}
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
```

---

## Recommendations Page

### Recommendations List
```tsx
// components/analytics/RecommendationsList.tsx
export function RecommendationsList() {
  const { data: recommendations } = useQuery(['recommendations'], fetchRecommendations)
  
  return (
    <div className="max-w-4xl mx-auto">
      <div className="bg-white rounded-lg shadow">
        <div className="p-6 border-b border-gray-200">
          <h1 className="text-2xl font-bold text-gray-900">
            Personalized Recommendations
          </h1>
          <p className="text-gray-600 mt-2">
            AI-powered suggestions to improve your financial health
          </p>
        </div>
        
        <div className="p-6">
          {/* Summary Stats */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
            <div className="bg-blue-50 p-4 rounded-lg text-center">
              <div className="text-2xl font-bold text-blue-600">
                {recommendations?.summary?.totalRecommendations}
              </div>
              <div className="text-sm text-blue-600">Total Recommendations</div>
            </div>
            <div className="bg-green-50 p-4 rounded-lg text-center">
              <div className="text-2xl font-bold text-green-600">
                {formatCurrency(recommendations?.summary?.totalPotentialSavings)}
              </div>
              <div className="text-sm text-green-600">Potential Savings</div>
            </div>
            <div className="bg-yellow-50 p-4 rounded-lg text-center">
              <div className="text-2xl font-bold text-yellow-600">
                {formatCurrency(recommendations?.summary?.easyWins)}
              </div>
              <div className="text-sm text-yellow-600">Easy Wins</div>
            </div>
            <div className="bg-red-50 p-4 rounded-lg text-center">
              <div className="text-2xl font-bold text-red-600">
                {recommendations?.summary?.highPriorityCount}
              </div>
              <div className="text-sm text-red-600">High Priority</div>
            </div>
          </div>
          
          {/* Recommendations */}
          <div className="space-y-6">
            {recommendations?.recommendations?.map((rec) => (
              <RecommendationCard key={rec.id} recommendation={rec} />
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
```

### Recommendation Card
```tsx
// components/analytics/RecommendationCard.tsx
export function RecommendationCard({ recommendation }) {
  const [isExpanded, setIsExpanded] = useState(false)
  
  return (
    <div className="border border-gray-200 rounded-lg p-6 hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <div className="flex items-center space-x-3 mb-2">
            <PriorityBadge priority={recommendation.priority} />
            <h3 className="text-lg font-semibold text-gray-900">
              {recommendation.title}
            </h3>
          </div>
          
          <p className="text-gray-600 mb-4">
            {recommendation.description}
          </p>
          
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
            <div>
              <div className="text-sm text-gray-500">Potential Savings</div>
              <div className="font-semibold text-green-600">
                {formatCurrency(recommendation.estimatedSavings)}
              </div>
            </div>
            <div>
              <div className="text-sm text-gray-500">Difficulty</div>
              <div className="font-semibold">
                {recommendation.implementationDifficulty}
              </div>
            </div>
            <div>
              <div className="text-sm text-gray-500">Time to Implement</div>
              <div className="font-semibold">
                {recommendation.timeToImplement}
              </div>
            </div>
            <div>
              <div className="text-sm text-gray-500">Confidence</div>
              <div className="font-semibold">
                {(recommendation.confidenceScore * 100).toFixed(0)}%
              </div>
            </div>
          </div>
          
          {isExpanded && (
            <div className="mt-4 space-y-4">
              <div>
                <h4 className="font-medium text-gray-900 mb-2">Action Items</h4>
                <ul className="list-disc list-inside space-y-1 text-gray-600">
                  {recommendation.actionItems.map((item, index) => (
                    <li key={index}>{item}</li>
                  ))}
                </ul>
              </div>
              
              <div>
                <h4 className="font-medium text-gray-900 mb-2">Expected Impact</h4>
                <p className="text-gray-600">{recommendation.impact}</p>
              </div>
            </div>
          )}
        </div>
        
        <button
          onClick={() => setIsExpanded(!isExpanded)}
          className="ml-4 text-blue-600 hover:text-blue-700"
        >
          {isExpanded ? 'Show Less' : 'Show More'}
        </button>
      </div>
    </div>
  )
}
```

---

## Mobile Responsiveness

### Responsive Design Principles
```css
/* Tailwind CSS responsive classes */
.container {
  @apply max-w-7xl mx-auto px-4 sm:px-6 lg:px-8;
}

.grid-layout {
  @apply grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6;
}

.chart-container {
  @apply h-64 sm:h-80 lg:h-96;
}

.mobile-menu {
  @apply fixed inset-0 z-50 lg:hidden;
}
```

### Mobile Navigation
```tsx
// components/navigation/MobileNavigation.tsx
export function MobileNavigation() {
  const [isOpen, setIsOpen] = useState(false)
  
  return (
    <>
      <button
        onClick={() => setIsOpen(true)}
        className="lg:hidden p-2 rounded-md text-gray-400 hover:text-gray-500"
      >
        <MenuIcon className="w-6 h-6" />
      </button>
      
      {isOpen && (
        <div className="fixed inset-0 z-50 lg:hidden">
          <div className="fixed inset-0 bg-gray-600 bg-opacity-75" />
          <div className="fixed inset-y-0 left-0 flex w-full max-w-xs flex-col bg-white">
            <div className="flex items-center justify-between p-4 border-b">
              <h2 className="text-lg font-semibold">Analytics</h2>
              <button
                onClick={() => setIsOpen(false)}
                className="p-2 rounded-md text-gray-400 hover:text-gray-500"
              >
                <XIcon className="w-6 h-6" />
              </button>
            </div>
            
            <nav className="flex-1 px-4 py-6 space-y-2">
              <NavLink href="/dashboard" onClick={() => setIsOpen(false)}>
                Dashboard
              </NavLink>
              <NavLink href="/dashboard/spending" onClick={() => setIsOpen(false)}>
                Spending Analysis
              </NavLink>
              <NavLink href="/dashboard/budget" onClick={() => setIsOpen(false)}>
                Budget Performance
              </NavLink>
              <NavLink href="/dashboard/goals" onClick={() => setIsOpen(false)}>
                Goal Progress
              </NavLink>
              <NavLink href="/dashboard/analytics" onClick={() => setIsOpen(false)}>
                AI Insights
              </NavLink>
            </nav>
          </div>
        </div>
      )}
    </>
  )
}
```

---

## Real-time Updates

### WebSocket Integration
```tsx
// hooks/useWebSocket.ts
export function useWebSocket(userId: string) {
  const [socket, setSocket] = useState(null)
  const [notifications, setNotifications] = useState([])
  
  useEffect(() => {
    const ws = new WebSocket(`ws://localhost:8080/ws/analytics/${userId}`)
    
    ws.onopen = () => {
      console.log('WebSocket connected')
    }
    
    ws.onmessage = (event) => {
      const data = JSON.parse(event.data)
      
      switch (data.event) {
        case 'budget_alert':
          setNotifications(prev => [...prev, {
            type: 'budget',
            title: 'Budget Alert',
            message: `You're ${data.data.utilization}% through your ${data.data.category} budget`,
            severity: data.data.utilization > 90 ? 'high' : 'medium'
          }])
          break
          
        case 'goal_milestone':
          setNotifications(prev => [...prev, {
            type: 'goal',
            title: 'Goal Milestone',
            message: `Congratulations! You've reached ${data.data.milestone} of your ${data.data.goalName} goal`,
            severity: 'low'
          }])
          break
          
        case 'anomaly_detected':
          setNotifications(prev => [...prev, {
            type: 'anomaly',
            title: 'Unusual Transaction',
            message: `Unusual transaction detected: ${data.data.reason}`,
            severity: data.data.severity
          }])
          break
      }
    }
    
    setSocket(ws)
    
    return () => {
      ws.close()
    }
  }, [userId])
  
  return { socket, notifications, setNotifications }
}
```

### Notification System
```tsx
// components/common/NotificationSystem.tsx
export function NotificationSystem() {
  const { notifications, setNotifications } = useWebSocket(userId)
  
  return (
    <div className="fixed top-4 right-4 z-50 space-y-2">
      {notifications.map((notification, index) => (
        <NotificationToast
          key={index}
          notification={notification}
          onDismiss={() => {
            setNotifications(prev => prev.filter((_, i) => i !== index))
          }}
        />
      ))}
    </div>
  )
}
```

---

## Performance Optimization

### Data Fetching Strategy
```tsx
// hooks/useAnalytics.ts
export function useAnalytics() {
  const queryClient = useQueryClient()
  
  // Prefetch data for better UX
  const prefetchAnalytics = useCallback(() => {
    queryClient.prefetchQuery(['spending-analysis'], fetchSpendingAnalysis)
    queryClient.prefetchQuery(['budget-performance'], fetchBudgetPerformance)
    queryClient.prefetchQuery(['goal-progress'], fetchGoalProgress)
  }, [queryClient])
  
  // Optimistic updates
  const updateBudget = useMutation(
    updateBudgetData,
    {
      onMutate: async (newBudget) => {
        await queryClient.cancelQueries(['budget-performance'])
        const previousBudget = queryClient.getQueryData(['budget-performance'])
        
        queryClient.setQueryData(['budget-performance'], old => ({
          ...old,
          budgetSummary: { ...old.budgetSummary, ...newBudget }
        }))
        
        return { previousBudget }
      },
      onError: (err, newBudget, context) => {
        queryClient.setQueryData(['budget-performance'], context.previousBudget)
      },
      onSettled: () => {
        queryClient.invalidateQueries(['budget-performance'])
      }
    }
  )
  
  return { prefetchAnalytics, updateBudget }
}
```

### Chart Optimization
```tsx
// components/charts/OptimizedChart.tsx
export function OptimizedChart({ data, type, ...props }) {
  const [isVisible, setIsVisible] = useState(false)
  const chartRef = useRef()
  
  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setIsVisible(true)
          observer.disconnect()
        }
      },
      { threshold: 0.1 }
    )
    
    if (chartRef.current) {
      observer.observe(chartRef.current)
    }
    
    return () => observer.disconnect()
  }, [])
  
  if (!isVisible) {
    return <div ref={chartRef} className="h-64 bg-gray-100 animate-pulse rounded" />
  }
  
  return (
    <div ref={chartRef}>
      {type === 'pie' && <PieChart data={data} {...props} />}
      {type === 'line' && <LineChart data={data} {...props} />}
      {type === 'bar' && <BarChart data={data} {...props} />}
    </div>
  )
}
```

---

## Accessibility

### ARIA Labels and Roles
```tsx
// components/charts/AccessibleChart.tsx
export function AccessibleChart({ data, title, description }) {
  return (
    <div role="region" aria-label={title}>
      <h3 id="chart-title" className="sr-only">{title}</h3>
      <p id="chart-description" className="sr-only">{description}</p>
      
      <div aria-labelledby="chart-title" aria-describedby="chart-description">
        <Chart data={data} />
      </div>
      
      <div className="mt-4">
        <h4 className="text-sm font-medium text-gray-900 mb-2">Data Summary</h4>
        <ul className="text-sm text-gray-600 space-y-1">
          {data.map((item, index) => (
            <li key={index}>
              {item.name}: {formatCurrency(item.value)} ({item.percentage}%)
            </li>
          ))}
        </ul>
      </div>
    </div>
  )
}
```

### Keyboard Navigation
```tsx
// components/common/KeyboardNavigation.tsx
export function KeyboardNavigation() {
  useEffect(() => {
    const handleKeyPress = (event) => {
      if (event.key === 'Escape') {
        // Close modals, dropdowns, etc.
      }
      
      if (event.ctrlKey && event.key === 'k') {
        // Open search
        event.preventDefault()
      }
    }
    
    document.addEventListener('keydown', handleKeyPress)
    return () => document.removeEventListener('keydown', handleKeyPress)
  }, [])
  
  return null
}
```

---

This frontend specification provides a comprehensive foundation for building a modern, responsive, and accessible analytics dashboard that effectively presents all the LLM-powered insights and analytics data from your expense tracker API. 