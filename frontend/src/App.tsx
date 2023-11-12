import './App.css'
import {useEffect, useState} from "react";
import axios from "axios";

type DiscordUser = {
    username: string,
    avatar: string,
}

function App() {
    const [discordUser, setDiscordUser] = useState<DiscordUser | undefined>(undefined);

    function login() {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin

        window.open(host + "/oauth2/authorization/discord", "_self")
    }

    function logout() {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin

        window.open(host + "/logout", "_self")
    }

    useEffect(() => {
        axios.get("/api/auth/me")
            .then((response) => setDiscordUser(response.data))
            .catch((e) => console.log(e));
    }, []);

    return (
        <>
            {
                discordUser ? (
                        <div>
                            <h1>Welcome, {discordUser.username}!</h1>
                            <img
                                src={`https://cdn.discordapp.com/avatars/${discordUser.username}/${discordUser.avatar}.png`}
                                alt={discordUser.username}/>
                            <br/>
                            <button onClick={logout}>Logout</button>
                        </div>
                    ) :
                    (
                        <div>
                            <h1>Welcome, Guest!</h1>
                            <button onClick={login}>Login with
                                Discord
                            </button>
                        </div>
                    )
            }
        </>
    )
}

export default App
