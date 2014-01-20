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
import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.DB;

/**
 * Tests for EclpiseLink for MongoDB.
 * 
 * @author Tobias Trelle, codecentric AG.
 */
public class OrderTest {

	/** entity manager. */
	private static EntityManager em;
	
	/** TX - with MongoDB ?!? */
	private EntityTransaction tx;
	
	@BeforeClass public static void setUpPU() {
		em = Persistence.createEntityManagerFactory("mongodb").createEntityManager();
	}
	
	/**
	 * Attention: EclipseLink requires an active transaction although MongoDB itself
	 * <b>DOES NOT</b> transaction at all!
	 */
	@Before public void setUp() {
		// is needed by JPA/EntityManager
		tx = em.getTransaction();
		tx.begin();
		
		// test preparation: drop the database
        DB db = ((MongoConnection)em.unwrap(javax.resource.cci.Connection.class)).getDB();
        db.dropDatabase();
	}
	
	/**
	 * Uses JPQL query (that gets translated to native MongoDB query.
	 */
	@Test public void should_find_by_items_quantity() {
		// given
		Order order = new Order("Tobias Trelle");
		List<Item> items = new ArrayList<Item>();
		items.add( new Item(1, 47.11, "Item #1") );
		items.add( new Item(2, 42.0, "Item #2") );
		order.setItems(items);
		em.persist(order);
		em.flush();
		order = null;
		
		// when
		order = em
				.createQuery("SELECT o FROM Order o JOIN o.items i WHERE i.quantity = 2", Order.class)
				.getSingleResult();
		
		// then
		assertNotNull( order );
		assertEquals( 2, order.getItems().size() );
	}

	/**
	 * Uses native MongoDB query (which consists of the full find command like used in the
	 * Mongo shell, not only the query string itself.
	 * <p>
	 * Note that EclipseLink converts the names of collections field to upper case.
	 */
	@Test public void should_find_by_primary_with_native_query() {
		// given
		Order order = new Order("Tobias Trelle");
		List<Item> items = new ArrayList<Item>();
		String id;
		
		items.add( new Item(1, 47.11, "Item #1") );
		items.add( new Item(2, 42.0, "Item #2") );
		order.setItems(items);
		em.persist(order);
		em.flush();
		id = order.getId();
		order = null;
		
		// when
		order = (Order)em
				.createNativeQuery("db.ORDER.findOne({_id: \"" + id + "\"})", Order.class)
				.getSingleResult();
		
		// then
		assertNotNull( order );
		assertEquals( 2, order.getItems().size() );
	}
	
	/**
	 * Native queries with nested paths (in this example "ITEMS.QUANTITY") do not seem
	 * to work properly, they raise an error.
	 */
	@Ignore public void should_find_by_items_quantity_with_native_query() {
		// given
		Order order = new Order("Tobias Trelle");
		List<Item> items = new ArrayList<Item>();
		
		items.add( new Item(1, 47.11, "Item #1") );
		items.add( new Item(2, 42.0, "Item #2") );
		order.setItems(items);
		em.persist(order);
		em.flush();
		order = null;
		
		// when
		order = (Order)em
				.createNativeQuery("db.ORDER.findOne({\"ITEMS.QUANTITY\": 2})", Order.class)
				.getSingleResult();
		
		// then
		assertNotNull( order );
		assertEquals( 2, order.getItems().size() );
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
