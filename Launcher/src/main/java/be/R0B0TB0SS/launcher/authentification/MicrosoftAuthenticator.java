package be.R0B0TB0SS.launcher.authentification;

import be.R0B0TB0SS.launcher.authentification.model.request.MinecraftLoginRequest;
import be.R0B0TB0SS.launcher.authentification.model.request.XSTSAuthorizationProperties;
import be.R0B0TB0SS.launcher.authentification.model.request.XboxLiveLoginProperties;
import be.R0B0TB0SS.launcher.authentification.model.request.XboxLoginRequest;
import be.R0B0TB0SS.launcher.authentification.model.response.*;


import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MicrosoftAuthenticator {
    public static final String MICROSOFT_AUTHORIZATION_ENDPOINT = "https://login.live.com/oauth20_authorize.srf";
    public static final String MICROSOFT_TOKEN_ENDPOINT = "https://login.live.com/oauth20_token.srf";

    public static final String MICROSOFT_REDIRECTION_ENDPOINT = "https://login.live.com/oauth20_desktop.srf";

    public static final String XBOX_LIVE_AUTH_HOST = "user.auth.xboxlive.com";
   public static final String XBOX_LIVE_CLIENT_ID = "000000004C12AE6F";
    public static final String XBOX_LIVE_SERVICE_SCOPE = "service::user.auth.xboxlive.com::MBI_SSL";
   // public static final String XBOX_LIVE_CLIENT_ID = "00000000402b5328";

    public static final String XBOX_LIVE_AUTHORIZATION_ENDPOINT = "https://user.auth.xboxlive.com/user/authenticate";
    public static final String XSTS_AUTHORIZATION_ENDPOINT = "https://xsts.auth.xboxlive.com/xsts/authorize";
    public static final String MINECRAFT_AUTH_ENDPOINT = "https://api.minecraftservices.com/authentication/login_with_xbox";

    public static final String XBOX_LIVE_AUTH_RELAY = "http://auth.xboxlive.com";
    public static final String MINECRAFT_AUTH_RELAY = "rp://api.minecraftservices.com/";

    public static final String MINECRAFT_STORE_ENDPOINT = "https://api.minecraftservices.com/entitlements/mcstore";
    public static final String MINECRAFT_PROFILE_ENDPOINT = "https://api.minecraftservices.com/minecraft/profile";

    public static final String MINECRAFT_STORE_IDENTIFIER = "game_minecraft";


    private final HttpClient http;

    public MicrosoftAuthenticator() {
        this.http = new HttpClient();
    }

    /**
     * Logs in a player using its Microsoft account credentials, and retrieve its Minecraft profile
     *
     * @param email    Player Microsoft account e-mail
     * @param password Player Microsoft account password
     * @return The player Minecraft profile
     * @throws MicrosoftAuthenticationException Thrown if one of the several HTTP requests failed at some point
     */
    public MicrosoftAuthResult loginWithCredentials(String email, String password) throws MicrosoftAuthenticationException {
        CookieHandler currentHandler = CookieHandler.getDefault();
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

        Map<String, String> params = new HashMap<>();
        params.put("login", email);
        params.put("loginfmt", email);
        params.put("passwd", password);

        HttpURLConnection result;

        try {
            PreAuthData authData = preAuthRequest();
            params.put("PPFT", authData.getPPFT());

            result = http.followRedirects(http.postForm(authData.getUrlPost(), params));
        } finally {
            CookieHandler.setDefault(currentHandler);
        }

        try {
            return loginWithTokens(extractTokens(result.getURL().toString()),true);
        } catch (MicrosoftAuthenticationException e) {
            if (match("(identity/confirm)", http.readResponse(result)) != null) {
                throw new MicrosoftAuthenticationException(
                        "User has enabled double-authentication or must allow sign-in on https://account.live.com/activity"
                );
            }

            throw e;
        }
    }

    /**
     * Logs in a player using a webview to display Microsoft login page.
     * <b>This function blocks the current thread until the process is finished; this can cause your application to
     * freeze. When calling from the JavaFX thread or any thread which must not be blocked, use
     * {@link #loginWithAsyncWebview()}</b>
     *
     * @return The player Minecraft profile
     * @throws MicrosoftAuthenticationException Thrown if one of the several HTTP requests failed at some point
     */
    public MicrosoftAuthResult loginWithWebview() throws MicrosoftAuthenticationException {
        try {
            return loginWithAsyncWebview().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new MicrosoftAuthenticationException(e);
        }
    }

    /**
     * Logs in a player using a webview to display Microsoft login page. This function does not block the current thread.
     *
     * @return A future resolved by the player Minecraft profile
     */
    public CompletableFuture<MicrosoftAuthResult> loginWithAsyncWebview() {
        if(!System.getProperty("java.version").startsWith("1."))
            CookieHandler.setDefault(new CookieManager());

        String url = String.format("%s?%s", MICROSOFT_AUTHORIZATION_ENDPOINT, http.buildParams(getLoginParams()));
        LoginFrame frame = new LoginFrame();

        return frame.start(url).thenApplyAsync(result -> {
            try {
                if(result != null)
                    return loginWithTokens(extractTokens(result),true);
                else return null;
            } catch (MicrosoftAuthenticationException e) {
                throw new CompletionException(e);
            }
        });
    }

    /**
     * Logs in a player using a Microsoft account refresh token retrieved earlier.
     *
     * @param refreshToken Player Microsoft account refresh token
     * @return The player Minecraft profile
     * @throws MicrosoftAuthenticationException Thrown if one of the several HTTP requests failed at some point
     */
    public MicrosoftAuthResult loginWithRefreshToken(String refreshToken) throws MicrosoftAuthenticationException {
        Map<String, String> params = getLoginParams();
        params.put("refresh_token", refreshToken);
        params.put("grant_type", "refresh_token");

        MicrosoftRefreshResponse response = http.postFormGetJson(
                MICROSOFT_TOKEN_ENDPOINT,
                params, MicrosoftRefreshResponse.class
        );

        return loginWithTokens(new AuthTokens(response.getAccessToken(), response.getRefreshToken()),true);
    }

    /**
     * Logs in a player using a Microsoft account tokens retrieved earlier.
     * <b>If the token was retrieved using Azure AAD/MSAL, it should be prefixed with d=</b>
     *
     * @param tokens Player Microsoft account tokens pair
     * @return The player Minecraft profile
     * @throws MicrosoftAuthenticationException Thrown if one of the several HTTP requests failed at some point
     */
    public MicrosoftAuthResult loginWithTokens(AuthTokens tokens) throws MicrosoftAuthenticationException {
        return loginWithTokens(tokens,true);
    }

    /**
     * Logs in a player using a Microsoft account tokens retrieved earlier.
     * <b>If the token was retrieved using Azure AAD/MSAL, it should be prefixed with d=</b>
     *
     * @param tokens Player Microsoft account tokens pair
     * @param retrieveProfile Whether to retrieve the player profile
     * @return The player Minecraft profile
     * @throws MicrosoftAuthenticationException Thrown if one of the several HTTP requests failed at some point
     */
    public MicrosoftAuthResult loginWithTokens(AuthTokens tokens, boolean retrieveProfile) throws MicrosoftAuthenticationException {
        XboxLoginResponse xboxLiveResponse = xboxLiveLogin(tokens.getAccessToken());
        XboxLoginResponse xstsResponse = xstsLogin(xboxLiveResponse.getToken());

        String userHash = xstsResponse.getDisplayClaims().getUsers()[0].getUserHash();
        MinecraftLoginResponse minecraftResponse = minecraftLogin(userHash, xstsResponse.getToken());
        MinecraftStoreResponse storeResponse = http.getJson(
                MINECRAFT_STORE_ENDPOINT,
                minecraftResponse.getAccessToken(),
                MinecraftStoreResponse.class
        );

        if (Arrays.stream(storeResponse.getItems()).noneMatch(item -> item.getName().equals(MINECRAFT_STORE_IDENTIFIER))) {
            throw new MicrosoftAuthenticationException("Player didn't buy Minecraft Java Edition or did not migrate its account");
        }
        MinecraftProfile profile = null;
        if (retrieveProfile) {
            profile = http.getJson(
                    MINECRAFT_PROFILE_ENDPOINT,
                    minecraftResponse.getAccessToken(),
                    MinecraftProfile.class
            );
        }

        return new MicrosoftAuthResult(
                profile,
                minecraftResponse.getAccessToken(),
                tokens.getRefreshToken(),
                xboxLiveResponse.getDisplayClaims().getUsers()[0].getUserHash(),
                Base64.getEncoder().encodeToString(minecraftResponse.getUsername().getBytes())
        );
    }


    protected PreAuthData preAuthRequest() throws MicrosoftAuthenticationException {
        Map<String, String> params = getLoginParams();
        params.put("display", "touch");
        params.put("locale", "en");

        String result = http.getText(MICROSOFT_AUTHORIZATION_ENDPOINT, params);

        String ppft = match("sFTTag:'.*value=\"([^\"]*)\"", result);
        String urlPost = match("urlPost: ?'(.+?(?='))", result);

        return new PreAuthData(ppft, urlPost);
    }

    protected XboxLoginResponse xboxLiveLogin(String accessToken) throws MicrosoftAuthenticationException {
        XboxLiveLoginProperties properties = new XboxLiveLoginProperties("RPS", XBOX_LIVE_AUTH_HOST, accessToken);
        XboxLoginRequest<XboxLiveLoginProperties> request = new XboxLoginRequest<>(
                properties, XBOX_LIVE_AUTH_RELAY, "JWT"
        );

        return http.postJson(XBOX_LIVE_AUTHORIZATION_ENDPOINT, request, XboxLoginResponse.class);
    }

    protected XboxLoginResponse xstsLogin(String xboxLiveToken) throws MicrosoftAuthenticationException {
        XSTSAuthorizationProperties properties = new XSTSAuthorizationProperties("RETAIL", new String[]{xboxLiveToken});
        XboxLoginRequest<XSTSAuthorizationProperties> request = new XboxLoginRequest<>(
                properties, MINECRAFT_AUTH_RELAY, "JWT"
        );

        return http.postJson(XSTS_AUTHORIZATION_ENDPOINT, request, XboxLoginResponse.class);
    }

    protected MinecraftLoginResponse minecraftLogin(String userHash, String xstsToken) throws MicrosoftAuthenticationException {
        MinecraftLoginRequest request = new MinecraftLoginRequest(String.format("XBL3.0 x=%s;%s", userHash, xstsToken));
        return http.postJson(MINECRAFT_AUTH_ENDPOINT, request, MinecraftLoginResponse.class);
    }


    protected Map<String, String> getLoginParams() {
        Map<String, String> params = new HashMap<>();
        params.put("client_id", XBOX_LIVE_CLIENT_ID);
        params.put("redirect_uri", MICROSOFT_REDIRECTION_ENDPOINT);
        params.put("scope", XBOX_LIVE_SERVICE_SCOPE);
        params.put("response_type", "token");

        return params;
    }

    protected AuthTokens extractTokens(String url) throws MicrosoftAuthenticationException {
        return new AuthTokens(extractValue(url, "access_token"), extractValue(url, "refresh_token"));
    }

    protected String extractValue(String url, String key) throws MicrosoftAuthenticationException {
        String matched = match(key + "=([^&]*)", url);
        if (matched == null) {
            throw new MicrosoftAuthenticationException("Invalid credentials or tokens");
        }

        try {
            return URLDecoder.decode(matched, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new MicrosoftAuthenticationException(e);
        }
    }

    protected String match(String regex, String content) {
        Matcher matcher = Pattern.compile(regex).matcher(content);
        if (!matcher.find()) {
            return null;
        }

        return matcher.group(1);
    }
}