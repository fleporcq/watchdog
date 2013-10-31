package models;

import org.apache.commons.lang.StringUtils;
import play.data.validation.*;
import play.db.jpa.Model;
import play.libs.Codec;

import javax.persistence.*;
import java.util.List;

@Entity()
@Table(name = "user")
public class User extends Model {

    @Required
    @MaxSize(50)
    @Unique
    public String username;

    @MaxSize(255)
    public String password;

    @Transient
    @Equals("passwordConfirmation")
    @MaxSize(255)
    public String newPassword;

    @Transient
    @MaxSize(255)
    public String passwordConfirmation;

    @Required
    @Enumerated(EnumType.STRING)
    public Role role;

    public static User findByUsername(String username) {
        return User.find("username = ?", username).first();
    }

    public boolean checkPassword(String password) {
        if (password != null) {
            return Codec.hexSHA1(password).equals(this.password);
        }
        return false;
    }

    public static List<User> allOrdered(){
        return User.find("ORDER BY username ASC").fetch();
    }


    @Override
    public User save() {
        if (StringUtils.isNotBlank(this.newPassword)) {
            this.password = Codec.hexSHA1(this.newPassword);
        }
        return super.save();
    }


}
