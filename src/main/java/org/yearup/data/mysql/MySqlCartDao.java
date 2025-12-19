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
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class MySqlCartDao extends MySqlDaoBase implements ShoppingCartDao {
    public MySqlCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        String sql = """
                SELECT 
                   sc.user_id, 
                   sc.product_id, 
                   p.name, 
                   p.price, 
                   p.category_id, 
                   p.description, 
                   p.subcategory, 
                   p.image_url,
                   p.stock,
                   p.featured 
                FROM shopping_cart sc 
                INNER JOIN products p 
                    ON sc.product_id = p.product_id
                WHERE sc.user_id = ?;
                """;
        ShoppingCart shoppingCart = new ShoppingCart();

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet row = statement.executeQuery();

           while (row.next()) {
               ShoppingCartItem product = mapRowItem(row);
               shoppingCart.add(product);
           }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return shoppingCart;
    }

    @Override
    public boolean deleteCart(int userId) {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?;";
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            int rowUpdated = statement.executeUpdate();

            return rowUpdated > 0;

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ShoppingCartItem getByProductId(int productId) {
        String sql = """ 
                SELECT c.product_id, products.name, products.price FROM shopping_cart AS c
                INNER JOIN products
                ON c.product_id = products.product_id
                WHERE c.product_id = ?;
        """;
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
            try (PreparedStatement insertStmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, productId);
                insertStmt.setInt(3, 1);

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

    @Override
    public void purchaseCart(int userId) {
        String profile = "SELECT address, city, state, zip FROM profiles WHERE user_id = ?";
        String deleteItems = "DELETE FROM shopping_cart WHERE user_id = ?";
        String insertOrder = "INSERT INTO orders(order_id, user_id, date, address, city, state, zip, shipping_amount) VALUES (?,?,?,?,?,?,?,?);";
        int orderId = (int) Math.round(Math.random() * 10000);
        LocalDateTime localDateTime = LocalDateTime.now();
        String date = localDateTime.toString();

        try(Connection connection = getConnection())
        {
            try(PreparedStatement checkStmt = connection.prepareStatement(profile)) { // if item exists
                checkStmt.setInt(1, userId);

                ResultSet rs = checkStmt.executeQuery();
                if(!rs.next()) {
                    throw new RuntimeException("User does not exist");
                }
                String address = rs.getString("address");
                String city = rs.getString("city");
                String state = rs.getString("state");
                int zip = rs.getInt("zip");
                try (PreparedStatement insertStmt = connection.prepareStatement(insertOrder, PreparedStatement.RETURN_GENERATED_KEYS)) { // insert order
                    insertStmt.setInt(1, orderId);
                    insertStmt.setInt(2, userId);
                    insertStmt.setString(3, date);
                    insertStmt.setString(4, address);
                    insertStmt.setString(5, city);
                    insertStmt.setString(6, state);
                    insertStmt.setInt(7, zip);
                    insertStmt.setInt(8, 15);
                    insertStmt.executeUpdate();

                }

                try (PreparedStatement statement = connection.prepareStatement(deleteItems))
                {
                    statement.setInt(1, userId);
                    statement.executeUpdate();
                }
                catch (SQLException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }


    private ShoppingCartItem mapRowItem(ResultSet resultSet) throws SQLException {
        ShoppingCartItem item = new ShoppingCartItem();
        Product product = new Product();

        product.setProductId(resultSet.getInt("product_id"));
        product.setName(resultSet.getString("name"));
        product.setPrice(resultSet.getBigDecimal("price"));
        product.setCategoryId(resultSet.getInt("category_id"));
        product.setDescription(resultSet.getString("description"));
        product.setSubCategory(resultSet.getString("subcategory"));
        product.setImageUrl(resultSet.getString("image_url"));
        product.setStock(resultSet.getInt("stock"));
        product.setFeatured(resultSet.getBoolean("featured"));
        item.setProduct(product);

        return item;
    }

}
