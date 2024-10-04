package be.R0B0TB0SS.launcher.authentification;


import be.R0B0TB0SS.launcher.authentification.model.response.MinecraftProfile;

public class MicrosoftAuthResult
{
    private final MinecraftProfile profile;
    private final String accessToken;
    private final String refreshToken;
    private final String xuid;
    private final String clientId;

    public MicrosoftAuthResult(MinecraftProfile profile, String accessToken, String refreshToken, String xuid, String clientId)
    {
        this.profile = profile;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.xuid = xuid;
        this.clientId = clientId;
    }

    /**
     * @return The player Minecraft profile (contains its UUID and username)
     */
    public MinecraftProfile getProfile()
    {
        return profile;
    }

    /**
     * @return The Minecraft access token
     */
    public String getAccessToken()
    {
        return accessToken;
    }

    /**
     * @return The Microsoft refresh token that can be used to log the user back silently using
     * {@link MicrosoftAuthenticator#loginWithRefreshToken(String)}
     */
    public String getRefreshToken()
    {
        return refreshToken;
    }

    /**
     * @return The XUID of the player
     */
    public String getXuid()
    {
        return this.xuid;
    }

    /**
     * @return The client ID of the player
     */
    public String getClientId()
    {
        return this.clientId;
    }
}