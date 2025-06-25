import requests
import random
import string
import time

BASE_URL = "http://localhost:8080"

def random_email():
    return "testuser_" + ''.join(random.choices(string.ascii_lowercase, k=8)) + "@example.com"

def test_end_to_end():
    # 1. Register user
    email = random_email()
    user_resp = requests.post(f"{BASE_URL}/api/users/register", json={
        "firstName": "Alice", "lastName": "Anthropic", "email": email, "password": "password123"
    })
    assert user_resp.status_code in (200, 201), user_resp.text
    user_id = user_resp.json()["id"]
    print(f"Registered user {email} with id {user_id}")

    # 2. Fetch categories (assume at least one exists)
    cat_resp = requests.get(f"{BASE_URL}/api/categories")
    assert cat_resp.status_code == 200
    categories = cat_resp.json()
    assert categories, "No categories found"
    category_ids = [cat["id"] for cat in categories]
    print(f"Using category ids {category_ids}")
    category_id = category_ids[0]

    # 3. Create a budget
    budget_resp = requests.post(
        f"{BASE_URL}/api/budgets",
        params={"userId": user_id},
        json={
            "yearMonth": "2025-06",
            "totalBudget": 2000,
            "notes": "Test budget"
        }
    )
    assert budget_resp.status_code == 201, budget_resp.text
    budget_id = budget_resp.json()["id"]
    print(f"Created budget with id {budget_id}")

    # 4. Allocate a category to the budget
    cat_alloc_resp = requests.post(
        f"{BASE_URL}/api/budgets/{budget_id}/categories",
        json={
            "categoryId": category_id,
            "budgetAmount": 2000
        }
    )
    assert cat_alloc_resp.status_code == 201, cat_alloc_resp.text
    print(f"Allocated category {category_id} to budget {budget_id}")

    # 5. Create a goal
    goal_resp = requests.post(f"{BASE_URL}/api/goals", params={"userId": user_id}, json={
        "name": "Vacation",
        "description": "Trip to Goa",
        "targetAmount": 50000,
        "goalType": "SAVINGS",
        "startDate": "2025-06-25",
        "targetDate": "2025-12-25"
    })
    assert goal_resp.status_code == 201, goal_resp.text
    goal_id = goal_resp.json()["id"]
    print(f"Created goal with id {goal_id}")

    # 6. Add transactions (simulate large dataset)
    for i in range(300):  # Adjust for "large" as needed
        random_category_id = random.choice(category_ids)
        t_resp = requests.post(f"{BASE_URL}/api/transactions", json={
            "userId": user_id,
            "amount": random.randint(10, 500),
            "categoryId": random_category_id,
            "transactionType": "EXPENSE",
            "transactionDate": "2025-06-25",
            "description": f"Test Transaction {i+1} (Category {random_category_id})"
        })
        assert t_resp.status_code in (200, 201), t_resp.text
        if (i+1) % 50 == 0:
            print(f"Added {i+1} transactions...")

    # 7. Update goal progress
    progress_resp = requests.put(f"{BASE_URL}/api/goals/{goal_id}/progress", json={"amount": 1000})
    assert progress_resp.status_code == 200, progress_resp.text
    print("Updated goal progress.")

    # 8. Call LLM analytics (spending insights)
    print("Requesting LLM analytics (spending insights)...")
    llm_resp = requests.get(f"{BASE_URL}/api/llm-analytics/spending-insights/{user_id}", params={"period": "MONTHLY"})
    assert llm_resp.status_code == 200, llm_resp.text
    llm_data = llm_resp.json()
    print("LLM Analytics Response:", llm_data)

    # 9. Call Smart Analytics (if available)
    print("Requesting Smart Analytics...")
    smart_resp = requests.get(
        f"{BASE_URL}/api/smart-analytics/spending-analysis/{user_id}",
        params={"period": "MONTHLY"}
    )
    assert smart_resp.status_code == 200, smart_resp.text
    smart_data = smart_resp.json()
    print("Smart Analytics Response:", smart_data)

    print("End-to-end test completed successfully.")

if __name__ == "__main__":
    test_end_to_end() 