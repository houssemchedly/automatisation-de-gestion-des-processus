# API Testing Troubleshooting Guide

## Common Issues and Solutions

### 1. Authentication Errors (400/401/403)

**Problem**: All requests failing with authentication errors
**Solution**:
1. Make sure your Spring Boot API is running on `http://localhost:8080`
2. Update the `base_url` variable in Postman to match your API URL
3. The API uses French field names: `nom` (last name), `prenom` (first name)
4. Register a user first, then activate the account before logging in

### 2. Account Activation Required

Your API requires email activation. After registering:
1. Check your application logs for the activation token
2. Use the "Activate Account" endpoint with the token
3. Then try logging in

### 3. Base URL Configuration

Update your Postman environment:
- Variable: `base_url`
- Value: `http://localhost:8088/api/v1` (or your actual API URL)

### 4. Testing Workflow

1. **Register User** → Creates account (returns 202)
2. **Activate Account** → Activates with token from logs
3. **Login User** → Gets JWT token (saves automatically)
4. **Test other endpoints** → All authenticated requests work

### 5. Field Name Reference

**Registration requires:**
- `nom` (last name)
- `prenom` (first name)
- `email`
- `password` (minimum 8 characters)

**Login requires:**
- `email`
- `password`
