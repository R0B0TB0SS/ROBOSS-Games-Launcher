package be.R0B0TB0SS.launcher.authentification.model.request;

public class MinecraftLoginRequest
{
    private final String identityToken;

    public MinecraftLoginRequest(String identityToken)
    {
        this.identityToken = identityToken;
    }

    public String getIdentityToken()
    {
        return identityToken;
    }
}