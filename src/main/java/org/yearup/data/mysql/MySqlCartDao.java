package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlCartDao extends MySqlDaoBase implements ShoppingCartDao {
    public MySqlCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        String sql = "SELECT * FROM shopping_cart WHERE user_id = ?;";
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet row = statement.executeQuery();

            if(row.next()) {
                return mapRow(row);
            }

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return new ShoppingCart();
    }

    @Override
    public ShoppingCart deleteCart(int userId) {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?;";
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet row = statement.executeQuery();

            if(row.next()) {
                return mapRow(row);
            }

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public ShoppingCartItem getByProductId(int productId) {
        String sql = "SELECT c.product_id, products.name, products.price FROM shopping_cart AS c\n" +
                "INNER JOIN products\n" +
                "ON c.product_id = products.product_id\n" +
                "WHERE c.product_id = 10;";
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, productId);

            ResultSet row = statement.executeQuery();

            if(row.next()) {
                return mapRowItem(row);
            }

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public int addItem(int userId, int productId) {
        // quantity is 0;
        String checkIfExists = "SELECT user_id, product_id, quantity FROM shopping_cart WHERE user_id = ? AND product_id = ?";
        String sql = "INSERT INTO shopping_cart(user_id, product_id, quantity) VALUES (?,?,?);";
        String updateQuantity = "UPDATE shopping_cart SET quantity = quantity + 1 WHERE user_id = ? AND product_id = ?";
        try(Connection connection = getConnection())
        {
            try(PreparedStatement checkStmt = connection.prepareStatement(checkIfExists)) { // if item exists
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, productId);

                ResultSet rs = checkStmt.executeQuery();
                if(rs.next()) { // update item
                    try(PreparedStatement updateStmt = connection.prepareStatement(updateQuantity)) {
                        updateStmt.setInt(1, userId);
                        updateStmt.setInt(2, productId);
                        return updateStmt.executeUpdate(); // update query
                    }
                }
            }
            try (PreparedStatement insertStmt = connection.prepareStatement(sql)) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, productId);
                insertStmt.executeUpdate();

                ResultSet keys = insertStmt.getGeneratedKeys();
                if(keys.next()) {
                    return keys.getInt(1);
                }
            }

            return 0;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int addItem(int userId, int productId, int quantity) {
        String sql = "INSERT INTO shopping_cart(user_id, product_id, quantity) VALUES (?,?,?);";
        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);

            ps.executeUpdate();

            return PreparedStatement.RETURN_GENERATED_KEYS;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateCart(int productId, ShoppingCartItem cartItem) {
        String sql = "UPDATE categories SET quantity = ? WHERE category_id = ?";
        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, cartItem.getQuantity());
            ps.setInt(2, cartItem.getProductId());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    private ShoppingCart mapRow(ResultSet resultSet) throws SQLException {
        int user_id = resultSet.getInt("user_id");
        int product_id = resultSet.getInt("product_id");
        int quantity = resultSet.getInt("quantity");

        ShoppingCart shoppingCart = new ShoppingCart();
        return shoppingCart;
    }
    private ShoppingCartItem mapRowItem(ResultSet resultSet) throws SQLException {
        ShoppingCartItem item = new ShoppingCartItem();
        int userId = resultSet.getInt("user_id");
        return item;
    }

}
