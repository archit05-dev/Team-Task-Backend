# Finnit — Backend

**Find It and Commit It** 🚀

Finnit is a personalized open-source contribution platform that helps developers discover repositories and contribution opportunities based on their skills, interests, preferred technologies, and difficulty level.

This repository contains the backend application, responsible for authentication, user management, personalized repository recommendations, and saved repositories.

## ✨ Features

- **GitHub OAuth Authentication:** Sign in using your GitHub account.
- **JWT Authentication:** Secure API access using access and refresh tokens.
- **Token Management:** Exchange OAuth authorization codes, refresh tokens, and log out.
- **Personalized Recommendations:** Generate repository recommendations based on onboarding preferences.
- **GitHub Integration:** Search for repositories using generated search tags.
- **ML-Powered Recommendations:** Integrate machine learning models to generate search tags and rank repositories.
- **Saved Repositories:** Save, retrieve, and remove repositories for future reference.
- **PostgreSQL Database:** Persist application data.
- **RESTful APIs:** Provide endpoints for communication with the frontend.

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java | Backend programming language |
| Spring Boot | Backend application framework |
| Spring Security | Application security and authentication |
| Spring Security OAuth2 Client | GitHub OAuth login |
| JWT | Access and refresh token authentication |
| PostgreSQL | Relational database |
| Spring Data JPA | Database access and persistence |
| GitHub API | Repository search and GitHub integration |
| Machine Learning Services | Search-tag generation and repository ranking |
| Maven | Dependency management and build automation |
| Render | Backend deployment |

## 🏗️ Architecture

The backend follows a layered architecture to separate responsibilities and make the application easier to maintain.

```text
Frontend
   |
   v
Spring Boot Backend
   |
   +-------------------------+
   |                         |
   v                         v
Controllers             Security Layer
   |                         |
   v                         v
Services                 JWT / OAuth2
   |
   +-------------------------+
   |                         |
   v                         v
Repositories           External Services
   |                    |             |
   v                    v             v
PostgreSQL           GitHub API    ML Services
```

### Main Responsibilities

- **Controllers:** Handle incoming HTTP requests and return responses.
- **Services:** Implement application logic and coordinate external integrations.
- **Repositories:** Handle database persistence.
- **Security Layer:** Manage OAuth2 authentication and validate JWT access tokens.
- **External Integrations:** Communicate with GitHub and the machine learning services.

The diagram represents the high-level architecture; individual classes and package names may differ in the implementation.

## 🔄 How Recommendations Work

Finnit uses onboarding preferences and GitHub repository information to personalize repository recommendations.

The recommendation workflow is:

1. The user signs in through GitHub OAuth.
2. The user completes onboarding by providing preferences.
3. The backend obtains relevant GitHub profile, repository, and contribution information.
4. The backend prepares a compact profile containing information such as languages, interests, skills, learning goals, experience level, preferred difficulty, and preferred issue types.
5. **ML Model 1** generates relevant search tags from the user's profile.
6. The backend constructs GitHub repository search queries using the generated tags.
7. The backend retrieves matching repositories from GitHub.
8. **ML Model 2** ranks the retrieved repositories.
9. The backend returns the recommended repositories to the frontend.

The goal is to help users discover open-source projects that match their interests and development experience.

## 🔐 Authentication

Finnit uses GitHub OAuth for login and JWTs for subsequent API authentication.

### Authentication Flow

```text
Frontend
   |
   | 1. Start GitHub login
   v
Backend OAuth2 Authorization
   |
   v
GitHub Authorization
   |
   | 2. GitHub redirects to backend callback
   v
OAuth2 Success Handler
   |
   | 3. Generate one-time authorization code
   v
Frontend
   |
   | 4. POST /api/auth/exchange
   v
Backend
   |
   | 5. Return access and refresh tokens
   v
Authenticated API Requests
```

### Token Lifetimes

| Token | Expiration |
|---|---|
| Access Token | 15 minutes |
| Refresh Token | 7 days |

### Access Token

Protected endpoints require the access token in the `Authorization` header.

```http
Authorization: Bearer YOUR_ACCESS_TOKEN
```

### Refresh Token

The refresh token is submitted in the request body when refreshing tokens or logging out.

The frontend should use the latest refresh token returned by the backend if token rotation is enabled.

### Logout

The logout endpoint revokes the submitted refresh token. An already-issued access token may remain valid until it expires, depending on the token validation implementation.

The frontend should clear its locally stored tokens after a successful logout.

## 🌐 API Documentation

**Deployed Backend:**  
https://fimmit-backend.onrender.com

The following table documents the 11 backend and OAuth routes covered by the current API endpoint guide.

### 1. Health and OAuth Endpoints

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| `GET` | `/` | Check whether the backend is running | Public |
| `GET` | `/oauth2/authorization/github` | Start GitHub OAuth login | Public |
| `GET` | `/login/oauth2/code/github` | Handle the GitHub OAuth callback | OAuth flow |

**Health check**

```http
GET /
```

This endpoint checks whether the backend is reachable.

**Start GitHub login**

```http
GET /oauth2/authorization/github
```

Normally triggered through a browser redirect. It starts the GitHub authorization process rather than returning a regular JSON response.

**GitHub OAuth callback**

```http
GET /login/oauth2/code/github
```

Spring Security processes the callback after the user authorizes Finnit.

### 2. Authentication Endpoints

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| `POST` | `/api/auth/exchange` | Exchange a one-time OAuth code for tokens | Public |
| `GET` | `/api/auth/me` | Retrieve the current authenticated user | Access token |
| `POST` | `/api/auth/refresh` | Refresh authentication tokens | Refresh token in request body |
| `POST` | `/api/auth/logout` | Revoke a refresh token | Refresh token in request body |

**Exchange OAuth code**

```http
POST /api/auth/exchange
Content-Type: application/json
```

Example request:

```json
{
  "code": "ONE_TIME_OAUTH_CODE"
}
```

Consumes the one-time authorization code and returns authentication token information.

**Get current user**

```http
GET /api/auth/me
Authorization: Bearer YOUR_ACCESS_TOKEN
```

Returns information about the currently authenticated user.

**Refresh tokens**

```http
POST /api/auth/refresh
Content-Type: application/json
```

Example request:

```json
{
  "refreshToken": "YOUR_REFRESH_TOKEN"
}
```

Uses the refresh token to obtain new authentication token information.

**Logout**

```http
POST /api/auth/logout
Content-Type: application/json
```

Example request:

```json
{
  "refreshToken": "YOUR_REFRESH_TOKEN"
}
```

Revokes the submitted refresh token.

### 3. Recommendation Endpoint

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| `POST` | `/api/recommendations` | Generate personalized repository recommendations | Access token |

**Generate recommendations**

```http
POST /api/recommendations
Authorization: Bearer YOUR_ACCESS_TOKEN
Content-Type: application/json
```

Example request:

```json
{
  "introduction": "I am interested in Java backend development.",
  "preferredTechStack": [
    "Java",
    "Spring Boot"
  ],
  "interests": [
    "Backend Development",
    "Open Source"
  ],
  "preferredIssueTypes": [
    "bug",
    "feature"
  ],
  "preferredDifficulty": "Easy"
}
```

The request represents the user's onboarding preferences.

| Field | Description |
|---|---|
| `introduction` | Optional introduction, limited to 300 characters |
| `preferredTechStack` | Preferred technologies |
| `interests` | Topics used to personalize recommendations |
| `preferredIssueTypes` | Preferred issue types |
| `preferredDifficulty` | Preferred contribution difficulty |

The example values are illustrative. Use the values accepted by the current backend implementation.

### 4. Saved Repository Endpoints

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| `POST` | `/api/saved-repositories` | Save a repository | Access token |
| `GET` | `/api/saved-repositories` | Retrieve saved repositories | Access token |
| `DELETE` | `/api/saved-repositories/{savedRepositoryId}` | Delete a saved repository | Access token |

**Save a repository**

```http
POST /api/saved-repositories
Authorization: Bearer YOUR_ACCESS_TOKEN
Content-Type: application/json
```

Example request:

```json
{
  "githubRepositoryId": 123456789,
  "repositoryName": "spring-boot-example",
  "owner": "example-user",
  "repositoryUrl": "https://github.com/example-user/spring-boot-example",
  "description": "Example repository for testing"
}
```

The `description` field is optional according to the endpoint guide.

Saving the same GitHub repository more than once for the same user returns `409 Conflict`, as documented in the guide.

**Retrieve saved repositories**

```http
GET /api/saved-repositories
Authorization: Bearer YOUR_ACCESS_TOKEN
```

Returns the saved repositories belonging to the authenticated user.

If the user has no saved repositories, the response is an empty JSON array.

Illustrative response:

```json
[
  {
    "id": 1,
    "githubRepositoryId": 123456789,
    "repositoryName": "spring-boot-example",
    "owner": "example-user",
    "repositoryUrl": "https://github.com/example-user/spring-boot-example",
    "description": "Example repository for testing",
    "savedAt": "2026-10-26T21:30:00"
  }
]
```

The `id` field represents the database record ID. It is different from `githubRepositoryId`.

**Delete a saved repository**

```http
DELETE /api/saved-repositories/1
Authorization: Bearer YOUR_ACCESS_TOKEN
```

Deletes a saved repository belonging to the authenticated user.

Use the database record's `id` returned by the saved-repositories endpoint, not the GitHub repository ID.

The documented successful response is `204 No Content`.

> **Note:** Request and response examples are illustrative. Verify exact DTO fields and response schemas against the current source code before treating them as a formal API contract.

## ⚙️ Configuration

Configure the required application properties and environment variables before running the backend.

Typical configuration categories include:

- PostgreSQL datasource URL, username, and password.
- GitHub OAuth client ID and client secret.
- JWT signing secret.
- Frontend origin and OAuth redirect configuration.
- Machine learning service configuration, if required by the deployment.

Keep environment-specific values outside source control.

For example, the JWT secret can be configured through an environment variable:

```properties
jwt.secret=${JWT_SECRET}
```

Use the exact property names required by the current application configuration.

**Never commit real database credentials, OAuth client secrets, JWT secrets, access tokens, refresh tokens, or API keys to the repository.**

## 🚀 Running Locally

### Prerequisites

- Java version compatible with the project configuration.
- Maven.
- PostgreSQL.
- GitHub OAuth application credentials.
- Access to the required external services.

### 1. Clone the Repository

```bash
  git clone <YOUR_BACKEND_REPOSITORY_URL>
cd <YOUR_BACKEND_DIRECTORY>
```

Replace the placeholders with your actual repository URL and directory name.

### 2. Configure Environment Variables

Set the required database, GitHub OAuth, JWT, and external-service configuration.

Do not use production credentials in publicly shared examples.

### 3. Start PostgreSQL

Create the required database and configure the datasource properties to point to it.

### 4. Run the Application

Using the Maven wrapper, if present:

```bash
 ./mvnw spring-boot:run
```

On Windows:

```bash
  mvnw.cmd spring-boot:run
```

Alternatively, if Maven is installed:

```bash
  mvn spring-boot:run
```

The exact Java and Maven versions should match the project's build configuration.

### 5. Verify the Backend

Open the health-check endpoint:

```http
GET /
```

The documented successful response is:

```text
Finnit Backend is running!
```

## ☁️ Deployment

The backend is deployed on Render.

**Backend URL:**  
https://fimmit-backend.onrender.com

Deployment requires the appropriate environment variables, database connectivity, OAuth configuration, and frontend integration settings.

Before deploying changes, verify:

- Database connectivity.
- GitHub OAuth callback configuration.
- Frontend redirect URL.
- JWT secret configuration.
- CORS and Spring Security rules.
- External machine learning service connectivity, where applicable.

## 🛡️ Security Considerations

- Protect application secrets using environment variables.
- Never expose database passwords, OAuth secrets, or JWT signing secrets.
- Require access tokens for protected endpoints.
- Validate JWTs before allowing authenticated requests.
- Handle refresh-token rotation and revocation according to the implementation.
- Restrict saved-repository operations to the authenticated user's records.
- Configure CORS to allow the intended frontend origin.
- Avoid logging sensitive authentication information.
- Ensure OAuth redirects use the correct frontend URL.
- Remember that revoking a refresh token does not necessarily invalidate an already-issued access token immediately.

## 🧪 Common HTTP Status Codes

The following status codes are documented for the current API.

| Status Code | Meaning |
|---|---|
| `200 OK` | Request completed successfully |
| `201 Created` | Repository was saved successfully |
| `204 No Content` | Operation completed without a response body |
| `400 Bad Request` | Request is malformed or fails validation |
| `401 Unauthorized` | Access token is missing, invalid, or expired |
| `404 Not Found` | Saved repository does not exist or does not belong to the authenticated user |
| `409 Conflict` | Repository is already saved by the current user |

Actual responses may depend on the endpoint and exception-handling configuration.

## 🎯 Project Goals

Finnit aims to make open-source contribution more approachable by helping developers:

- Discover repositories aligned with their skills and interests.
- Find contribution opportunities appropriate to their experience.
- Explore technologies they want to learn.
- Save interesting repositories for later.
- Navigate the process of finding and contributing to open-source projects.

## 🤝 Contributing

Contributions, suggestions, and improvements are welcome.

1. Fork the repository.
2. Create a feature branch.
3. Implement and test your changes.
4. Commit your changes with clear messages.
5. Open a pull request describing your contribution.

Please avoid committing secrets, credentials, or environment-specific configuration.

## 📄 License

Add the project's chosen license here once it has been selected and included in the repository.