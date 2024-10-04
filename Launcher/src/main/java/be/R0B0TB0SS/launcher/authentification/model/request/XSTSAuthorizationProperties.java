package be.R0B0TB0SS.launcher.authentification.model.request;

public class XSTSAuthorizationProperties
{
    private final String SandboxId;
    private final String[] UserTokens;

    public XSTSAuthorizationProperties(String SandboxId, String[] UserTokens)
    {
        this.SandboxId = SandboxId;
        this.UserTokens = UserTokens;
    }

    public String getSandboxId()
    {
        return SandboxId;
    }

    public String[] getUserTokens()
    {
        return UserTokens;
    }
}
