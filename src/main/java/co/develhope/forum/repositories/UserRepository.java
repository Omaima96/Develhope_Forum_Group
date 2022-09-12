package co.develhope.forum.repositories;

import co.develhope.forum.dto.response.UserDTO;
import co.develhope.forum.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

// inspired by https://www.devxperiences.com/pzwp1/2022/05/19/spring-boot-security-configuration-practically-explained-part2-jdbc-authentication/
@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User findById(int id){
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM user WHERE id_user = ?",
                    BeanPropertyRowMapper.newInstance(User.class), id);
            return user;
        }catch (IncorrectResultSizeDataAccessException e){
            return null;
        }
    }

    public User findByName(String username) {

        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM user,user_data WHERE User_Name=? AND user.User_Data_id_User_Data=id_User_Data",
                    BeanPropertyRowMapper.newInstance(User.class), username);
            user.grantAuthorities(this.getUserRoles(user.getUserName()));
            int userModelID = jdbcTemplate.queryForObject("SELECT id_User FROM `user` WHERE User_Name = ?",
                    Integer.class, new Object[]{user.getUserName()});
            user.setId(userModelID);
            boolean isActive= jdbcTemplate.queryForObject("SELECT isActive FROM user WHERE User_Name = ?",
                    boolean.class, username);
            user.setActive(isActive);
            return user;

        } catch (IncorrectResultSizeDataAccessException e) {

            return null;
        }
    }

    public User findByActivationCode(String activationCode) {
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM user WHERE User_ActivationCode=?",
                    BeanPropertyRowMapper.newInstance(User.class), activationCode);

            int userModelID = jdbcTemplate.queryForObject("SELECT id_User FROM `user` WHERE User_Name = ?",
                    Integer.class, new Object[]{user.getUserName()});
            user.setId(userModelID);
            return user;
        }catch (IncorrectResultSizeDataAccessException e){
            return null;
        }
    }





    private List<String> getUserRoles(String userName) {

        String querySQL = "SELECT User_Roles_id_User_Roles FROM user WHERE User_Name = ?";
        //dataSource.
        List<String> userRoles = jdbcTemplate.queryForList(querySQL, String.class, userName);
        return userRoles;
    }

}
