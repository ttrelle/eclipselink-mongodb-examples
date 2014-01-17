package eclipselink;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoConnection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.DB;

public class OrderTest {

	/** entity manager. */
	private static EntityManager em;
	
	/** TX - with MongoDB ?!? */
	private EntityTransaction tx;
	
	@BeforeClass public static void setUpPU() {
		em = Persistence.createEntityManagerFactory("mongodb").createEntityManager();
	}
	
	@Before public void setUp() {
		tx = em.getTransaction();
		tx.begin();
        DB db = ((MongoConnection)em.unwrap(javax.resource.cci.Connection.class)).getDB();
        db.dropDatabase();
	}
	
	@Test public void should_find_by_items_quantity() {
		// given
		Order order = new Order("Tobias Trelle");
		List<Item> items = new ArrayList<Item>();
		items.add( new Item(1, 47.11, "Item #1") );
		items.add( new Item(2, 42.0, "Item #2") );
		order.setItems(items);
		em.persist(order);
		em.flush();
		
		// when
		List<Order> orders = em
				.createQuery("SELECT o FROM Order o JOIN o.items i WHERE i.quantity = 2", Order.class)
				.getResultList();
		
		// then
		assertNotNull(orders);
		assertEquals(1, orders.size());
		Order o = orders.get(0);
		assertNotNull( o.getItems() );
		assertEquals( 2, o.getItems().size() );
	}
	
	@After public void tearDown() {
		tx.commit();
	}
	
	@AfterClass public static void closeEntityManager() {
		if (em != null) {
			em.close();
		}
	}
}
