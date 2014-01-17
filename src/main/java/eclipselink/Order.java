package eclipselink;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.NoSql;


@Entity
@NoSql(dataFormat=DataFormatType.MAPPED)
public class Order {

	@Id 
    @GeneratedValue
    @Field(name="_id")
	private String id;
	
	@Temporal(TemporalType.DATE)
	private Date date;
	
	@Column(name = "custInfo") private String customerInfo;
	
	@ElementCollection
	private List<Item> items;	
	
	public Order() {
		super();
	}
	
	public Order(String customerInfo) {
		super();
		this.customerInfo = customerInfo;
		this.date = new Date();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCustomerInfo() {
		return customerInfo;
	}

	public void setCustomerInfo(String customerInfo) {
		this.customerInfo = customerInfo;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}	
	
}
