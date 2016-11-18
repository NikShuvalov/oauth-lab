package shuvalov.nikita.songbird;

/**
 * Created by NikitaShuvalov on 11/17/16.
 */

public class BearerTokenResponse {
    String token_type, access_token;

    public String getToken_type() {
        return token_type;
    }

    public String getAccess_token() {
        return access_token;
    }
}
