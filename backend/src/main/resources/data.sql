-- =====================================================
-- SAMPLE DATA FOR EXPENSE TRACKER
-- =====================================================

-- Insert a test user (password: admin123)
INSERT INTO users (email, password, first_name, last_name, phone, monthly_income, currency, timezone, is_active, created_at) VALUES
('admin@expensetracker.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Admin', 'User', '+91-9876543210', 50000.00, 'INR', 'Asia/Kolkata', true, CURRENT_TIMESTAMP);

-- Insert default categories
INSERT INTO categories (name, description, icon, color, is_default, is_active, created_at) VALUES
('Food & Dining', 'Restaurants, groceries, food delivery', '🍽️', '#FF6B6B', true, true, CURRENT_TIMESTAMP),
('Transportation', 'Fuel, public transport, ride sharing', '🚗', '#4ECDC4', true, true, CURRENT_TIMESTAMP),
('Shopping', 'Clothing, electronics, general shopping', '🛍️', '#45B7D1', true, true, CURRENT_TIMESTAMP),
('Entertainment', 'Movies, games, hobbies, subscriptions', '🎬', '#96CEB4', true, true, CURRENT_TIMESTAMP),
('Healthcare', 'Medical expenses, medicines, insurance', '🏥', '#FFEAA7', true, true, CURRENT_TIMESTAMP),
('Housing', 'Rent, utilities, maintenance', '🏠', '#DDA0DD', true, true, CURRENT_TIMESTAMP),
('Education', 'Courses, books, training', '📚', '#98D8C8', true, true, CURRENT_TIMESTAMP),
('Travel', 'Vacations, business trips', '✈️', '#F7DC6F', true, true, CURRENT_TIMESTAMP),
('Investment', 'Stocks, mutual funds, savings', '📈', '#BB8FCE', true, true, CURRENT_TIMESTAMP),
('Salary', 'Monthly salary, bonuses', '💰', '#82E0AA', true, true, CURRENT_TIMESTAMP),
('Other', 'Miscellaneous expenses', '📝', '#BDC3C7', true, true, CURRENT_TIMESTAMP);

-- Insert sample transactions for the test user
INSERT INTO transactions (user_id, amount, currency, transaction_type, transaction_date, transaction_time, description, category_id, source_type, is_immutable, sync_status, created_at) VALUES
(1, 150.00, 'INR', 'EXPENSE', '2024-01-15', '12:30:00', 'Lunch at office canteen', 1, 'MANUAL', false, 'SYNCED', CURRENT_TIMESTAMP),
(1, 500.00, 'INR', 'EXPENSE', '2024-01-15', '18:45:00', 'Grocery shopping', 1, 'MANUAL', false, 'SYNCED', CURRENT_TIMESTAMP),
(1, 200.00, 'INR', 'EXPENSE', '2024-01-16', '09:15:00', 'Fuel for bike', 2, 'MANUAL', false, 'SYNCED', CURRENT_TIMESTAMP),
(1, 1000.00, 'INR', 'EXPENSE', '2024-01-16', '20:00:00', 'Movie tickets', 4, 'MANUAL', false, 'SYNCED', CURRENT_TIMESTAMP),
(1, 50000.00, 'INR', 'INCOME', '2024-01-01', '09:00:00', 'Monthly salary', 10, 'MANUAL', false, 'SYNCED', CURRENT_TIMESTAMP),
(1, 300.00, 'INR', 'EXPENSE', '2024-01-17', '13:00:00', 'Coffee and snacks', 1, 'MANUAL', false, 'SYNCED', CURRENT_TIMESTAMP),
(1, 1500.00, 'INR', 'EXPENSE', '2024-01-17', '19:30:00', 'Dinner with friends', 1, 'MANUAL', false, 'SYNCED', CURRENT_TIMESTAMP),
(1, 800.00, 'INR', 'EXPENSE', '2024-01-18', '10:00:00', 'Uber ride', 2, 'MANUAL', false, 'SYNCED', CURRENT_TIMESTAMP),
(1, 2500.00, 'INR', 'EXPENSE', '2024-01-18', '16:00:00', 'Shopping for clothes', 3, 'MANUAL', false, 'SYNCED', CURRENT_TIMESTAMP),
(1, 5000.00, 'INR', 'EXPENSE', '2024-01-19', '11:00:00', 'Online course subscription', 7, 'MANUAL', false, 'SYNCED', CURRENT_TIMESTAMP); 