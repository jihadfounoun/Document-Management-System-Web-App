package beans;

public class User {
    private int id;
    private String email,username, name;
    public User(int userId,String email,String username,String name) {
        this.id=userId;
        this.email=email;
        this.name=name;
        this.username=username;
    }
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
    public User() {
    }

    

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }
}
