package io.hexlet;

import java.sql.*;

public class App {
    // Нужно указывать базовое исключение,
    // потому что выполнение запросов может привести к исключениям
    public static void main(String[] args) throws SQLException {
        // Создаем соединение с базой в памяти
        // База создается прямо во время выполнения этой строчки
        // Здесь hexlet_test — это имя базы данных
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:hexlet_test");

        String sql = "CREATE TABLE users (id BIGINT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(255), phone VARCHAR(255))";
        // Чтобы выполнить запрос, создадим объект statement
        Statement statement = conn.createStatement();
        statement.execute(sql);
        statement.close(); // В конце закрываем

        String sql2 = "INSERT INTO users (username, phone) VALUES ('tommy', '123456789')";
        Statement statement2 = conn.createStatement();
        statement2.executeUpdate(sql2);
        statement2.close();

        String sql3 = "SELECT * FROM users";
        Statement statement3 = conn.createStatement();
        // Здесь вы видите указатель на набор данных в памяти СУБД
        ResultSet resultSet = statement3.executeQuery(sql3);
        // Набор данных — это итератор
        // Мы перемещаемся по нему с помощью next() и каждый раз получаем новые значения
        while (resultSet.next()) {
            System.out.println(resultSet.getLong("id"));
            System.out.println(resultSet.getString("username"));
            System.out.println(resultSet.getString("phone"));
        }
        statement3.close();

        // Закрываем соединение
        conn.close();
    }
}
