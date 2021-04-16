import javax.swing.*;

public class Main {
    public static void main(String[] args) {
//        Auth auth = new Auth();
//        auth.openAuthorizationPage();
//        auth.runHttpServer();


        JFrame frame = new Swing();
        AuthGitLab authGitLab = new AuthGitLab();
        authGitLab.openAuthorizationPage();
        authGitLab.runHttpServer();
    }
}
