package be.R0B0TB0SS.launcher.authentification.model.response;

public class XboxLoginResponse
{
    private final String IssueInstant;
    private final String NotAfter;
    private final String Token;
    private final XboxLoginResponse.XboxLiveLoginResponseClaims DisplayClaims;

    public XboxLoginResponse(String IssueInstant, String NotAfter, String Token, XboxLoginResponse.XboxLiveLoginResponseClaims DisplayClaims)
    {
        this.IssueInstant = IssueInstant;
        this.NotAfter = NotAfter;
        this.Token = Token;
        this.DisplayClaims = DisplayClaims;
    }

    public String getIssueInstant()
    {
        return IssueInstant;
    }

    public String getNotAfter()
    {
        return NotAfter;
    }

    public String getToken()
    {
        return Token;
    }

    public XboxLoginResponse.XboxLiveLoginResponseClaims getDisplayClaims()
    {
        return DisplayClaims;
    }

    public static class XboxLiveLoginResponseClaims
    {
        private final XboxLoginResponse.XboxLiveUserInfo[] xui;

        public XboxLiveLoginResponseClaims(XboxLoginResponse.XboxLiveUserInfo[] xui)
        {
            this.xui = xui;
        }

        public XboxLoginResponse.XboxLiveUserInfo[] getUsers()
        {
            return xui;
        }
    }

    public static class XboxLiveUserInfo
    {
        private final String uhs;

        public XboxLiveUserInfo(String uhs)
        {
            this.uhs = uhs;
        }

        public String getUserHash()
        {
            return uhs;
        }
    }
}
