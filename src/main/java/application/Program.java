package application;

import db.DB;
import db.DbException;
import entities.Order;
import entities.OrderStatus;
import entities.Product;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Program {

    public static void main(String[] args) {

        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        try{
            conn = DB.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM tb_order\n "
                    + "INNER JOIN tb_order_product ON tb_order.id = tb_order_product.order_id\n"
                    + "INNER JOIN tb_product ON tb_product.id = tb_order_product.product_id");

            Map<Long, Order> map = new HashMap<>();
            Map<Long, Product> prods = new HashMap<>();
            while(rs.next()){
                Long orderId = rs.getLong("order_id");
                if(map.get(orderId) == null) {
                    Order order = instantiateOrder(rs);
                    map.put(orderId, order);
                }
                Long productId = rs.getLong("product_id");
                if(prods.get(productId) == null){
                    Product product = instantiateProduct(rs);
                    prods.put(productId, product);
                }
                map.get(orderId).getProducts().add(prods.get(productId));
            }

            for(Long orderId : map.keySet()){
                System.out.println(map.get(orderId));
                for(Product p : map.get(orderId).getProducts()){
                    System.out.println(p);
                }
                System.out.println();
            }
        }
        catch(SQLException e){
            throw new DbException(e.getMessage());
        }
    }



    private static Order instantiateOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("order_id"));
        order.setLatitude(rs.getDouble("latitude"));
        order.setLongitude(rs.getDouble("longitude"));
        order.setMoment(rs.getTimestamp("moment").toInstant());
        order.setStatus(OrderStatus.values()[rs.getInt("status")]);
        return order;

    }

    private static Product instantiateProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setDescription(rs.getString("description"));
        product.setImageUri(rs.getString("image_uri"));
        product.setName(rs.getString("name"));
        product.setPrice(rs.getDouble("price"));
        return product;
    }

}
