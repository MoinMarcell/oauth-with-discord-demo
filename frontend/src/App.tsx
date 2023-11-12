import './App.css'
import {useEffect, useState} from "react";
import axios from "axios";

type DiscordUser = {
    username: string,
    avatar: string,
}

function App() {
    const [discordUser, setDiscordUser] = useState<DiscordUser | undefined>(undefined);

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
                        </div>
                    ) :
                    (
                        <div>
                            <h1>Welcome, Guest!</h1>
                            <button onClick={() => window.open("http://localhost:8080/login", "_self")}>Login with
                                Discord
                            </button>
                        </div>
                    )
            }
        </>
    )
}

export default App
