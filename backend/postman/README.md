# Test API - Postman Collection Guide

## 🚀 Quick Start

### 1. Import the Collection
1. Open Postman
2. Click **Import** button
3. Select the `TestAPI_Collection.json` file
4. The collection will be imported with all endpoints organized by feature

### 2. Set Up Environment Variables
The collection uses these variables:
- `base_url`: Set to `http://localhost:8088/api/v1` (default)
- `jwt_token`: Automatically set after successful login

### 3. Authentication Flow
**IMPORTANT**: Follow this sequence for testing:

1. **Register User** (optional - if you don't have an account)
    - Use the "Register User" request in the 🔐 Authentication folder
    - Modify the email/password in the request body

2. **Login** (required for all other endpoints)
    - Use the "Login User" request
    - The JWT token will be automatically saved to `jwt_token` variable
    - All subsequent requests will use this token

3. **Test Other Endpoints**
    - All other endpoints require authentication
    - The Bearer token is automatically applied to all requests

## 📁 Collection Structure

### 🔐 Authentication
- **Register User**: Create new user account
- **Login User**: Get JWT token (auto-saves to variable)
- **Activate Account**: Account activation endpoint

### 👥 User Management
- Complete CRUD operations for users
- Role assignment and user status management
- Pagination support for listing users

### 📋 Project Management
- Create, read, update, delete projects
- Get projects by owner
- Toggle project active status

### 🏃‍♂️ Sprint Management
- Full sprint lifecycle management
- Search and filter sprints
- Status management and active sprint tracking

### ✅ Task Management
- Task CRUD operations with status tracking
- Task assignment and priority management
- Search tasks with multiple filters
- "My Tasks" endpoint for current user

### 🤝 Meeting Management
- Schedule and manage meetings
- Participant management
- Filter by type, project, and date
- Upcoming and today's meetings

### 🔔 Notifications
- Notification system with read/unread status
- Bulk operations (mark all as read)
- Search and filter notifications
- Unread count tracking

### 🎯 User Stories
- User story management with priority and status
- Link to product backlog items

### 📝 Product Backlog & Blockers
- Backlog management
- Issue/blocker tracking with priority levels

### 🔑 Roles
- Role management system
- Assign roles to users

## 🔧 Testing Tips

### 1. **Start with Authentication**
Always run the Login request first to get your JWT token.

### 2. **Use Test Scripts**
The Login request includes a test script that automatically saves the JWT token.

### 3. **Modify Sample Data**
Update the request bodies with your own test data:
- Change email addresses
- Modify project names
- Adjust dates and priorities

### 4. **Check Response Status**
Each request includes basic status code tests to verify success.

### 5. **Pagination Parameters**
Most list endpoints support pagination:
- `page`: Page number (default: 0)
- `size`: Items per page (default: 10)
- `sort`: Sort field (default: id)
- `direction`: Sort direction (asc/desc)

## 🌐 Server Configuration

### Default Settings
- **Base URL**: `http://localhost:8088/api/v1`
- **Port**: 8088 (configured in application-dev.yml)
- **Database**: PostgreSQL on localhost:5432

### Alternative Access Methods

#### 1. Swagger UI (Recommended for exploration)
- URL: `http://localhost:8088/api/v1/swagger-ui.html`
- Interactive API documentation
- Built-in testing capabilities

#### 2. OpenAPI JSON
- URL: `http://localhost:8088/api/v1/v3/api-docs`
- Raw OpenAPI specification
- Can be imported into other tools

## 🐛 Troubleshooting

### Common Issues

1. **401 Unauthorized**
    - Make sure you've logged in and have a valid JWT token
    - Check that the token is properly set in the collection variables

2. **404 Not Found**
    - Verify the server is running on port 8088
    - Check the base_url variable is correct

3. **Database Connection Issues**
    - Ensure PostgreSQL is running
    - Verify database credentials in application-dev.yml

4. **Email Service Issues**
    - The app uses a local mail server (port 1025)
    - For testing, you can skip email verification

### Server Startup
\`\`\`bash
# Start the Spring Boot application
mvn spring-boot:run

# Or if you have the JAR file
java -jar target/test-api-0.0.1-SNAPSHOT.jar
\`\`\`

## 📊 Sample Test Workflow

1. **Setup Phase**
    - Register a new user
    - Login to get JWT token

2. **Project Management**
    - Create a new project
    - Get all projects
    - Update project details

3. **Sprint Planning**
    - Create sprints for the project
    - Add user stories to product backlog
    - Create tasks for user stories

4. **Team Collaboration**
    - Assign tasks to users
    - Schedule meetings
    - Track progress and blockers

5. **Monitoring**
    - Check notifications
    - Review task status
    - Monitor sprint progress

This collection provides comprehensive coverage of all API endpoints with realistic sample data for thorough testing.
