package io.hexlet;

import java.sql.*;
import java.util.List;

public class App {
    // Нужно указывать базовое исключение,
    // потому что выполнение запросов может привести к исключениям
    public static void main(String[] args) throws SQLException {
        // Создаем соединение с базой в памяти
        // База создается прямо во время выполнения этой строчки
        // Здесь hexlet_test — это имя базы данных
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:hexlet_test")) {
            String sql1 = "CREATE TABLE users (id BIGINT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(255), phone VARCHAR(255))";
            // Чтобы выполнить запрос, создадим объект statement
            try (Statement statement = conn.createStatement()) {
                statement.execute(sql1);
            }

            String sql2 = "INSERT INTO users (username, phone) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, "Sarah");
                preparedStatement.setString(2, "123456");
                preparedStatement.executeUpdate();

                preparedStatement.setString(1, "Max");
                preparedStatement.setString(2, "234567");
                preparedStatement.executeUpdate();

                preparedStatement.setString(1, "Del");
                preparedStatement.setString(2, "99937656");
                preparedStatement.executeUpdate();

                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    System.out.println(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("DB have not returned an id after saving the entity");
                }
            }

            String sql3 = "DELETE FROM users WHERE username = 'Del'";
            try (PreparedStatement preparedStatement2 = conn.prepareStatement(sql3)) {
                try (Statement statement3 = conn.createStatement()) {
                    ResultSet resultSet = statement3.executeQuery("SELECT * FROM users");
                    while (resultSet.next()) {
                        System.out.printf("%s %s\n", resultSet.getLong("id"),
                                resultSet.getString("username"));
                    }
                }
                preparedStatement2.executeUpdate();
            }

            String sql4 = "SELECT * FROM users";
            try (Statement statement3 = conn.createStatement()) {
                ResultSet resultSet = statement3.executeQuery(sql4);
                while (resultSet.next()) {
                    System.out.printf("%s %s %s\n", resultSet.getLong("id"),
                            resultSet.getString("username"),
                            resultSet.getString("phone"));
                }
            }
        }
    }
}
