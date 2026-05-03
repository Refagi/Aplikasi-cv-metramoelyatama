/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserAkun {
    private final StringProperty id;
    private final StringProperty email;
    private final StringProperty password;
    private final StringProperty role;
    private final StringProperty status;

    public UserAkun(String id, String email, String password, String role, String status) {
        this.id       = new SimpleStringProperty(id);
        this.email    = new SimpleStringProperty(email != null ? email : "");
        this.password = new SimpleStringProperty(password != null ? password : "");
        this.role     = new SimpleStringProperty(role != null ? role : "");
        this.status   = new SimpleStringProperty(status != null ? status : "");
    }

    // id
    public String getId()               { return id.get(); }
    public void setId(String v)         { id.set(v); }
    public StringProperty idProperty()  { return id; }

    // email
    public String getEmail()                { return email.get(); }
    public void setEmail(String v)          { email.set(v); }
    public StringProperty emailProperty()   { return email; }

    // password
    public String getPassword()             { return password.get(); }
    public void setPassword(String v)       { password.set(v); }
    public StringProperty passwordProperty(){ return password; }

    // role
    public String getRole()                 { return role.get(); }
    public void setRole(String v)           { role.set(v); }
    public StringProperty roleProperty()    { return role; }

    // status
    public String getStatus()               { return status.get(); }
    public void setStatus(String v)         { status.set(v); }
    public StringProperty statusProperty()  { return status; }
}
