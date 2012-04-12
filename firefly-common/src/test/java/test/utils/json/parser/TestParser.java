package test.utils.json.parser;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import test.utils.json.Book;
import test.utils.json.CollectionObj;
import test.utils.json.SimpleObj;
import test.utils.json.SimpleObj2;
import test.utils.json.User;

import com.firefly.utils.json.Json;

public class TestParser {
	@Test
	public void test() {
		SimpleObj i = new SimpleObj();
		i.setAge(10);
		i.setId(33442);
		i.setNumber(30);
		i.setName("PengtaoQiu\nAlvin");
		i.setType((short)-33);
		i.setWeight(55.47f);
		i.setHeight(170.5);
		String jsonStr = Json.toJson(i);
		System.out.println(jsonStr);
		
		SimpleObj i2 = Json.toObject(jsonStr, SimpleObj.class);
		Assert.assertThat(i2.getAge(), is(10));
		Assert.assertThat(i2.getId(), is(33442));
		Assert.assertThat(i2.getNumber(), is(30));
		Assert.assertThat(i2.getDate(), is(0L));
		Assert.assertThat(i2.getName(), is("PengtaoQiu\nAlvin"));
		Assert.assertThat(i2.getType(), is((short)-33));
		Assert.assertThat(i2.getHeight(), is(170.5));
		Assert.assertThat(i2.getWeight(), is(55.47f));
	}
	
	@Test
	public void test2() {
		SimpleObj i = new SimpleObj();
		i.setAge(10);
		i.setId(33442);
		i.setNumber(30);
		i.setName("PengtaoQiu\nAlvin");
		
		SimpleObj i2 = new SimpleObj();
		i2.setAge(20);
		i2.setId(12341);
		i2.setNumber(33);
		i2.setName("Tom");
		i.setContact1(i2);
		String jsonStr = Json.toJson(i);
		System.out.println(jsonStr);
		
		SimpleObj temp = Json.toObject(jsonStr, SimpleObj.class);
		Assert.assertThat(temp.getId(), is(33442));
		Assert.assertThat(temp.getContact1().getId(), is(12341));
		Assert.assertThat(temp.getContact1().getName(), is("Tom"));
		Assert.assertThat(temp.getContact1().getAge(), is(20));
		Assert.assertThat(temp.getContact2(), nullValue());
	}
	
	@Test
	public void test3() {
		String jsonStr = "{\"id\":33442,\"date\":null,\"add1\":{}, \"add2\":{}, \"add3\":{}, \"add4\":{}, \"add5\":null,\"add6\":\"sdfsdf\",\"contact2\":{}, \"number\":30,\"height\":null,\"name\":\"PengtaoQiu\nAlvin\",\"type\":null,\"weight\":40.3}";
		SimpleObj temp = Json.toObject(jsonStr, SimpleObj.class);
		Assert.assertThat(temp.getName(), is("PengtaoQiu\nAlvin"));
		Assert.assertThat(temp.getId(), is(33442));
		Assert.assertThat(temp.getWeight(), is(40.3F));
	}
	
	@Test
	public void test4() {
		SimpleObj2 so2 = new SimpleObj2();
		so2.setId(334);
		
		User user = new User();
		user.setId(2434L);
		user.setName("Pengtao");
		so2.setUser(user);
		
		Book book = new Book();
		book.setId(23424);
		book.setPrice(3.4);
		book.setSell(true);
		book.setText("cccccccc");
		book.setTitle("ddddd");
		so2.setBook(book);
		
		String jsonStr = Json.toJson(so2);
		System.out.println(jsonStr);
		
		SimpleObj2 temp = Json.toObject(jsonStr, SimpleObj2.class);
		Assert.assertThat(temp.getBook().getPrice(), is(3.4));
		Assert.assertThat(temp.getBook().getTitle(), nullValue());
		Assert.assertThat(temp.getId(), is(334));
	}
	
	@Test
	public void test5() {
List<LinkedList<SimpleObj>> list = new LinkedList<LinkedList<SimpleObj>>();
		
		LinkedList<SimpleObj> list1 = new LinkedList<SimpleObj>();
		for (int j = 0; j < 10; j++) {
			SimpleObj i = new SimpleObj();
			i.setAge(10);
			i.setId(33442 + j);
			i.setNumber(30);
			i.setName("PengtaoQiu\nAlvin");
			
			SimpleObj i2 = new SimpleObj();
			i2.setAge(20);
			i2.setId(12341);
			i2.setNumber(33);
			i2.setName("Tom");
			i.setContact1(i2);
			list1.add(i);
		}
		list.add(list1);
		
		list1 = new LinkedList<SimpleObj>();
		for (int j = 0; j < 10; j++) {
			SimpleObj i = new SimpleObj();
			i.setAge(10);
			i.setId(1000 + j);
			i.setNumber(30);
			i.setName("PengtaoQiu\nAlvin");
			
			SimpleObj i2 = new SimpleObj();
			i2.setAge(20);
			i2.setId(12341);
			i2.setNumber(33);
			i2.setName("Tom");
			i.setContact1(i2);
			list1.add(i);
		}
		list.add(list1);
		
		CollectionObj o = new CollectionObj();
		o.setList(list);
		String json = Json.toJson(o);
		System.out.println(json);
		
		CollectionObj o2 = Json.toObject(json, CollectionObj.class);
		System.out.println(o2.getList().get(0).get(0).getId());
		System.out.println(o2.getList().get(1).get(0).getId());
		Assert.assertThat(o2.getList().size(), is(2));
		Assert.assertThat(o2.getList().get(0).size(), is(10));
		Assert.assertThat(o2.getList().get(0).get(1).getId(), is(33443));
		Assert.assertThat(o2.getList().get(1).get(1).getId(), is(1001));
	}
	
	public static void main(String[] args) {
		
	}
}