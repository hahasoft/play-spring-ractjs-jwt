# Poc login with jwt 

## Require 

- mysql 
  - run mysql and create schema jwt_security
  ```
    docker compose up   
  ```
  - remove docker mysql
  ```
    docker compose down
    docker volume rm mysql_mysql_data
  ```

- frontend (fe-react-jwt)

   ```
   npm create vite@latest fe-react-jwt --template react
   cd fe-react-jwt 
   npm install axios react-router-dom
   ```

- backend (spring-boot-jwt)

# FrontEnd Diagram
```mermaid
sequenceDiagram
    participant User
    participant Browser
    participant Router
    participant AuthContext
    participant AxiosInstance
    participant LocalStorage
    participant Server

    User->>Browser: เปิดเว็บแอป
    Browser->>Router: รับ request URL

    alt User not authenticated
        Router->>Login: /login route
        Login-->>User: Render login form

        User->>Login: กรอก email และ password
        Login->>AuthContext: call login function
        AuthContext->>AxiosInstance: POST /auth/authenticate
        AxiosInstance->>Server: ส่ง email และ password
        Server-->>AxiosInstance: ส่งกลับ user data และ access token
        AxiosInstance->>AuthContext: ส่งกลับ user data
        AuthContext->>LocalStorage: เก็บ user data
        AuthContext-->>Login: ส่งกลับ user data
        Login->>Router: Navigate to /dashboard
    else User authenticated
        Router->>Dashboard: /dashboard route
        Dashboard->>AuthContext: get user from context
        AuthContext->>LocalStorage: get user data
        LocalStorage-->>AuthContext: ส่งกลับ user data
        AuthContext-->>Dashboard: ส่งกลับ user data
        Dashboard->>AxiosInstance: GET /demo-controller
        AxiosInstance->>Server: ส่ง request
        Server-->>AxiosInstance: ส่งกลับ error 401 (Unauthorized)
        
        alt 401 Unauthorized and not retried
            AxiosInstance->>LocalStorage: get user data
            LocalStorage-->>AxiosInstance: ส่งกลับ user data
            AxiosInstance->>Server: POST /auth/refresh-token with refresh token
            Server-->>AxiosInstance: ส่งกลับ new access token
            AxiosInstance->>LocalStorage: update user data with new access token
            LocalStorage-->>AxiosInstance: ส่งกลับ updated user data
            AxiosInstance->>Server: re-send original request
            Server-->>AxiosInstance: ส่งกลับ demo content
            AxiosInstance-->>Dashboard: ส่งกลับ demo content
        else 401 Unauthorized and retried or other error
            AxiosInstance->>LocalStorage: remove user data
            LocalStorage-->>AxiosInstance: ส่งกลับ empty
            AxiosInstance->>Router: Navigate to /login
        end
        
        Dashboard-->>User: Render demo content and user info
    end

    User->>Dashboard: Click logout
    Dashboard->>AuthContext: call logout function
    AuthContext->>LocalStorage: remove user data
    LocalStorage-->>AuthContext: ส่งกลับ empty
    AuthContext-->>Dashboard: ส่งกลับ logout success
    Dashboard->>Router: Navigate to /login
```