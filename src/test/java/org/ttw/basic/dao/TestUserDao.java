package org.ttw.basic.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.ttw.basic.model.Pager;
import org.ttw.basic.model.SystemContext;
import org.ttw.basic.model.User;
import org.ttw.basic.test.util.AbstractDbUnitTestCase;
import org.ttw.basic.test.util.EntitiesHelper;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import javassist.tools.rmi.ObjectNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/beans.xml")
@TestExecutionListeners({
	DbUnitTestExecutionListener.class,
	DependencyInjectionTestExecutionListener.class
	})
public class TestUserDao extends AbstractDbUnitTestCase{
	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private IUserDao userDao;
	
	@Before
	public void setUp() throws DataSetException, SQLException, IOException{
		Session session = sessionFactory.openSession();
		TransactionSynchronizationManager.bindResource(sessionFactory,new SessionHolder(session));
		this.backupAllTable();
	}
	
	@Test
//	@DatabaseSetup("/t_user.xml")
	public void testLoad() throws DatabaseUnitException, SQLException{
		IDataSet dSet = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,dSet);
		User user = userDao.load(1);
		EntitiesHelper.assertUser(user);
	}
	
//	@Test(expected=ObjectNotFoundException.class)
//	public void testDelete() throws DatabaseUnitException, SQLException{
//		IDataSet dSet = createDateSet("t_user");
//		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,dSet);
//		userDao.delete(1);
//		User tuUser = userDao.load(1);
//		System.err.println(tuUser.getUsername());
//	}
	
	@Test
	public void testListByArgs() throws DatabaseUnitException, SQLException{
		IDataSet dSet = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,dSet);
		SystemContext.setOrder("desc");
		SystemContext.setSorts("id");
		List<User> expected = userDao.list("from User where id>? and id<?",new Object[]{1,4}); 
		List<User> actual = Arrays.asList(new User(3,"admin3"),new User(2,"admin2"));
		assertNotNull(expected);
		assertTrue(expected.size()==2);
		EntitiesHelper.assertUsers(expected,actual);
	}
	
	@Test
	public void testFindByArgs() throws DatabaseUnitException, SQLException{
		IDataSet dSet = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,dSet);
		SystemContext.setOrder("desc");
		SystemContext.setSorts("id");
		SystemContext.setPageSize(3);
		SystemContext.setPageOffset(0);
		Pager<User> expected = userDao.find("from User where id>=? and id<=?",new Object[]{1,10}); 
		List<User> actual = Arrays.asList(new User(10,"admin10"),new User(9,"admin9"),new User(8,"admin8"));
		assertNotNull(expected);
		assertTrue(expected.getTotal()==10);
		assertTrue(expected.getOffset()==0);
		assertTrue(expected.getSize()==3);
		EntitiesHelper.assertUsers(expected.getDatas(),actual);
	}
	
	
	@Test
	public void testListByArgsAndAlias() throws DatabaseUnitException, SQLException{
		IDataSet dSet = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,dSet);
		SystemContext.setOrder("asc");
		SystemContext.setSorts("id");
		Map<String,Object> alias = new HashMap<String,Object>();
		alias.put("ids",Arrays.asList(1,2,3,5,6,7,8,9));
		List<User> expected = userDao.list("from User where id>? and id<? and id in(:ids)",new Object[]{1,5},alias); 
		List<User> actual = Arrays.asList(new User(2,"admin2"),new User(3,"admin3"));
		assertNotNull(expected);
		assertTrue(expected.size()==2);
		EntitiesHelper.assertUsers(expected,actual);
	}
	
	@Test
	public void testFindByArgsAndAlias() throws DatabaseUnitException, SQLException{
		IDataSet dSet = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,dSet);
		SystemContext.removeOder();
		SystemContext.removeSort();
		SystemContext.setPageOffset(0);
		SystemContext.setPageSize(3);
		Map<String,Object> alias = new HashMap<String,Object>();
		alias.put("ids",Arrays.asList(1,2,4,5,6,7,8,10));
		Pager<User> expected = userDao.find("from User where id>=? and id<=? and id in(:ids)",new Object[]{1,10},alias); 
		List<User> actual = Arrays.asList(new User(1,"admin1"),new User(2,"admin2"),new User(4,"admin4"));
		assertNotNull(expected);
		assertTrue(expected.getTotal()==8);
		assertTrue(expected.getOffset()==0);
		assertTrue(expected.getSize()==3);
		EntitiesHelper.assertUsers(expected.getDatas(),actual);
	}
	
	@Test
	public void testListSQLByArgs() throws DatabaseUnitException, SQLException{
		IDataSet dSet = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,dSet);
		SystemContext.setOrder("desc");
		SystemContext.setSorts("id");
		List<User> expected = userDao.listBySql("select * from t_user where id>? and id<?",new Object[]{1,4},User.class,true); 
		List<User> actual = Arrays.asList(new User(3,"admin3"),new User(2,"admin2"));
		assertNotNull(expected);
		assertTrue(expected.size()==2);
		EntitiesHelper.assertUsers(expected,actual);
	}
	
	@Test
	public void testListSQLByArgsAndAlias() throws DatabaseUnitException, SQLException{
		IDataSet dSet = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,dSet);
		SystemContext.setOrder("asc");
		SystemContext.setSorts("id");
		Map<String,Object> alias = new HashMap<String,Object>();
		alias.put("ids",Arrays.asList(1,2,3,5,6,7,8,9));
		List<User> expected = userDao.listBySql("select * from t_user where id>? and id<? and id in(:ids)",new Object[]{1,5},alias,User.class,true); 
		List<User> actual = Arrays.asList(new User(2,"admin2"),new User(3,"admin3"));
		assertNotNull(expected);
		assertTrue(expected.size()==2);
		EntitiesHelper.assertUsers(expected,actual);
	}
	
	@Test
	public void testFindSQLByArgs() throws DatabaseUnitException, SQLException{
		IDataSet dSet = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,dSet);
		SystemContext.setOrder("desc");
		SystemContext.setSorts("id");
		SystemContext.setPageSize(3);
		SystemContext.setPageOffset(0);
		Pager<User> expected = userDao.findBySql("select * from t_user where id>=? and id<=?",new Object[]{1,10},User.class,true); 
		List<User> actual = Arrays.asList(new User(10,"admin10"),new User(9,"admin9"),new User(8,"admin8"));
		assertNotNull(expected);
		assertTrue(expected.getTotal()==10);
		assertTrue(expected.getOffset()==0);
		assertTrue(expected.getSize()==3);
		EntitiesHelper.assertUsers(expected.getDatas(),actual);
	}
	@Test
	public void testFindSQLByArgsAndAlias() throws DatabaseUnitException, SQLException{
		IDataSet dSet = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,dSet);
		SystemContext.removeOder();
		SystemContext.removeSort();
		SystemContext.setPageOffset(0);
		SystemContext.setPageSize(3);
		Map<String,Object> alias = new HashMap<String,Object>();
		alias.put("ids",Arrays.asList(1,2,4,5,6,7,8,10));
		Pager<User> expected = userDao.findBySql("select * from t_user where id>=? and id<=? and id in(:ids)",new Object[]{1,10},alias,User.class,true); 
		List<User> actual = Arrays.asList(new User(1,"admin1"),new User(2,"admin2"),new User(4,"admin4"));
		assertNotNull(expected);
		assertTrue(expected.getTotal()==8);
		assertTrue(expected.getOffset()==0);
		assertTrue(expected.getSize()==3);
		EntitiesHelper.assertUsers(expected.getDatas(),actual);
	}
	
	
	
	
	@After
	public void tearDown() throws FileNotFoundException, DatabaseUnitException, SQLException{
		SessionHolder holder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
		Session session = holder.getSession();
		session.flush();
		TransactionSynchronizationManager.unbindResource(sessionFactory);
		this.resumeTable();
	}
}
