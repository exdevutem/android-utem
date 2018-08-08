package cl.inndev.miutem.interfaces;

public interface IAuthenticatorServer {
    String getToken(final String user, final String pass);
}
