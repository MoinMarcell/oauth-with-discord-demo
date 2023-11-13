## Spring Boot + Discord OAuth2 + React / vite

This is a sample project for Spring Boot + Discord OAuth2 + React / vite.

### How to run

As vite runs on Port 5173 and Spring Boot on Port 8080, you need to set the url `com.github.moinmarcell.url=http://localhost:5173` in the `application.properties`, if you want to run the application locally.
If you want to run it on a server, you need to set the url to `com.github.moinmarcell.url=https://yourdomain.com`.

1. Create a Discord application from [Discord Developer Portal](https://discord.com/developers/applications).
2. Set the redirect URI to `http://localhost:8080/login/oauth2/code/discord`.
3. Set the client ID and client secret to `application.properties`: `spring.security.oauth2.client.registration.discord.client-id=YOUR_CLIENT_ID` and `spring.security.oauth2.client.registration.discord.client-secret=YOUR_CLIENT_SECRET`.
4. Run the Spring Boot application.
5. In the Terminal navigate to the `frontend` directory and run `npm install` and `npm run dev`.
6. Open `http://localhost:5173` in your browser.
7. Click the login button and login with Discord.
8. You will see your Discord username and avatar.
9. Click the logout button to log out.
10. You will see the login button again.
