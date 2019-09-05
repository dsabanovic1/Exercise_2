package models;

import database.Database;
import security.Encrypt;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.ValidationException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TokenSession {

    @JsonProperty
    String userId;

    @JsonProperty
    String token;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public static TokenSession generateSessionToken(String userId)
            throws NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException, SQLException{

        TokenSession tokenSession = new TokenSession();
        String token = Encrypt.generateStrongPasswordHash(userId.toString());
        tokenSession.userId=userId;
        tokenSession.token=token;


        PreparedStatement ps = Database.prepareStatement("SELECT * FROM exercise2.token_sessions WHERE user_id = ? LIMIT 1");
        ps.setString(1, userId);
        ResultSet rs  = ps.executeQuery();

        if(rs.next())
        {
            tokenSession.setUserId(userId);
            tokenSession.setToken(rs.getString("token"));
            return tokenSession;
        }
        ps = Database.prepareStatement("INSERT INTO exercise2.token_sessions (token_sessions_id, user_id, token) VALUES (?,?,?)");
        ps.setString(1, Database.generatorId(5));
        ps.setString(2,userId);
        ps.setString(3,token);
        ps.executeUpdate();


        return tokenSession;
    }

    public static TokenSession findByToken(String token)
            throws SQLException,ClassNotFoundException, ValidationException
    {
        PreparedStatement ps = Database.prepareStatement("SELECT * FROM exercise2.token_sessions WHERE token = ? LIMIT 1");
        ps.setString(1,token);
        ResultSet rs = ps.executeQuery();

        if(!rs.next()){
            return null;
        }

        TokenSession tokenSession = new TokenSession();

        tokenSession.setUserId(rs.getString("user_id"));
        tokenSession.setToken(rs.getString("token"));

        return tokenSession;
    }
}