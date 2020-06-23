package encapsulations;

public class Usuario {

    private String user;
    private String name;
    private String passw;

    public Usuario(String user, String name, String passw) {
        this.user = user;
        this.name = name;
        this.passw = passw;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassw() {
        return passw;
    }

    public void setPassw(String passw) {
        this.passw = passw;
    }
}
