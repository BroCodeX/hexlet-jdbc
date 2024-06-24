package io.hexlet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class App {
    // Нужно указывать базовое исключение,
    // потому что выполнение запросов может привести к исключениям
    public static void main(String[] args) throws SQLException {
        // Создаем соединение с базой в памяти
        // База создается прямо во время выполнения этой строчки
        // Здесь hexlet_test — это имя базы данных
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:hexlet_test");
        try (Statement statement = conn.createStatement()) {
            String sql1 = "CREATE TABLE users (id BIGINT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(255), phone VARCHAR(255))";
            statement.execute(sql1);
        }
        UserDAO dao = new UserDAO(conn);

        var user = new User("Maria", "888888888");
        var user3 = new User("Jack", "12345678");
        user.getId(); // null
        dao.save(user);
        dao.save(user3);
        System.out.println(user.getId());
        System.out.println(user3.getId());// Здесь уже выводится какой-то id

        dao.printed();
        System.out.println("=====");

// Возвращается Optional<User>
        var user2 = dao.find(1L).get();
        System.out.println(user2.getId() == user.getId()); // true

        dao.del(user3);
        dao.printed();
        System.out.println("=====");

        var user4 = dao.find(3L).orElse(new User("null","null"));
        System.out.println(user4);
    }
}

@AllArgsConstructor
class UserDAO {
    private Connection connection;

    public void save(User user) throws SQLException {
        if (user.getId() == null) {
            String sql = "INSERT INTO users (username, phone) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = this.connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPhone());
                preparedStatement.executeUpdate();

                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("DB have not returned an id after saving an entity");
                }
            }
        }
    }

    public void del(User user) throws SQLException {
        if (user.getId() != null) {
            String sql = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement preparedStatement = this.connection
                    .prepareStatement(sql)){
                preparedStatement.setLong(1, user.getId());
                preparedStatement.executeUpdate();
                user.setId(null);
        }
    } else {
            throw new SQLException("DB have not returned an id after saving an entity");
        }
    }

    public Optional<User> find (Long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String username = resultSet.getString("username");
                String phone = resultSet.getString("phone");
                User user = new User(username, phone);
                user.setId(id);
                return Optional.of(user);
            }
            return Optional.empty();
        }
    }

    public void printed () throws SQLException {
        String sql = "SELECT * FROM users";
        try (Statement statement = this.connection.createStatement()) {
            var resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String phone = resultSet.getString("phone");
                System.out.printf("%s %s\n", username, phone);
            }
        }
    }
}

@Data
@RequiredArgsConstructor
@ToString
class User {
    private Long id;
    private final String username;
    private final String phone;
}
