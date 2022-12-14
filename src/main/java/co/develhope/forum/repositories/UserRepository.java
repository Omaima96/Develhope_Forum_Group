package co.develhope.forum.repositories;

import co.develhope.forum.dao.rowmapper.UserRowMapper;
import co.develhope.forum.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {

    private static final Logger log = LoggerFactory.getLogger(ForumRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User findById(int userID){
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM user u INNER JOIN user_data ud ON u.id_User=ud.User_id_User WHERE id_User=?",
                    new UserRowMapper(), userID);
            return user;
        }catch (IncorrectResultSizeDataAccessException e){
            return null;
        }
    }

    public User findByName(String username) {
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM user u INNER JOIN user_data ud ON u.id_User=ud.User_id_User WHERE User_Name=?",
                    new UserRowMapper(), username);
            int userModelID = jdbcTemplate.queryForObject("SELECT id_User FROM `user` WHERE User_Name = ?",
                    Integer.class, user.getUsername());
            user.setId(userModelID);
            boolean isActive= jdbcTemplate.queryForObject("SELECT isActive FROM user WHERE User_Name = ?",
                    boolean.class, username);
            user.setActive(isActive);
            List<String> userRoles = jdbcTemplate.queryForObject("SELECT Roles_Type FROM user,user_roles WHERE User_Name=? AND user.User_Roles_id_User_Roles = id_User_Roles", List.class, username);
            user.setRoles(userRoles);
            return user;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public User findByActivationCode(String activationCode) {
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM user u INNER JOIN user_data ud ON u.id_User=ud.User_id_User WHERE u.User_ActivationCode=?",
                    new UserRowMapper(), activationCode);
            int userModelID = jdbcTemplate.queryForObject("SELECT id_User FROM `user` WHERE User_Name = ?",
                    Integer.class, user.getUsername());
            user.setId(userModelID);
            return user;
        }catch (IncorrectResultSizeDataAccessException e){
            return null;
        }
    }

    public void banUser(boolean banned, String username) {
        String banSQL = "UPDATE user SET isActive = ? WHERE User_Name = ?";
        jdbcTemplate.update(banSQL, banned, username);
    }

    public User findByResetPasswordCode(String resetPasswordCode) {
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM user u INNER JOIN user_data ud ON u.id_User=ud.User_id_User WHERE u.ResetPasswordCode=?",
                    new UserRowMapper(), resetPasswordCode);
            int userModelID = jdbcTemplate.queryForObject("SELECT id_User FROM `user` WHERE User_Name = ?",
                    Integer.class, user.getUsername());
            user.setId(userModelID);
            return user;
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("ERROR", e);
            return null;
        }
    }

    @Deprecated
    public List<String> getUserRoles(String username) {
        String querySQL = "SELECT User_Roles_id_User_Roles FROM user WHERE User_Name = ?";
        List<String> userRoles = jdbcTemplate.queryForList(querySQL, String.class, username);
        return userRoles;
    }

    public void setPasswordCode(String resetPasswordCode, String username){
        jdbcTemplate.update("UPDATE user SET ResetPasswordCode = ? WHERE User_Name = ?", resetPasswordCode, username);
    }

    public void resetPassword(String password, String resetPasswordCode) {
        String passwordSQL = "UPDATE user SET User_Password = ? WHERE ResetPasswordCode = ?";
        jdbcTemplate.update(passwordSQL, password, resetPasswordCode);
    }

    public int deleteUser(String username) {
        int count = jdbcTemplate.update("DELETE FROM `user` WHERE User_Name = ? ",username);
        return count;
    }

    public List<Map<String, Object>> users() {
        String query = "SELECT * FROM user";
        List<Map<String,Object>> usersList = jdbcTemplate.queryForList(query);
        return usersList;
    }
}