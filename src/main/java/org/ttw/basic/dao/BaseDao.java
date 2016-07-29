/**
 * 
 */
package org.ttw.basic.dao;

import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.ttw.basic.model.Pager;
import org.ttw.basic.model.SystemContext;

/**
 * @author Administrator
 *
 */
@SuppressWarnings("unchecked")
public class BaseDao<T> implements IBaseDao<T> {

	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Inject
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * 创建一个class的对象来获取泛型的class
	 */
	private Class<?> clz;

	public Class<?> getClz() {
		if (clz == null) {
			// 获取泛型的Class对象
			clz = ((Class<?>) (((ParameterizedType) (this.getClass().getGenericSuperclass()))
					.getActualTypeArguments()[0]));
		}
		return clz;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#add(java.lang.Object)
	 */
	@Override
	public T add(T t) {
		getSession().save(t);
		return t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#update(java.lang.Object)
	 */
	@Override
	public void update(T t) {
		getSession().update(t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#delet(int)
	 */
	@Override
	public void delete(int id) {
		getSession().delete(this.load(id));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#load(int)
	 */
	@Override
	public T load(int id) {
		return (T) getSession().load(getClz(), id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#list(java.lang.String,
	 * java.lang.Object[])
	 */
	@Override
	public List<T> list(String hql, Object[] args) {
		return this.list(hql,args,null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#list(java.lang.String, java.lang.Object)
	 */
	@Override
	public List<T> list(String hql, Object arg) {
		return this.list(hql,new Object[]{arg});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#list(java.lang.String)
	 */
	@Override
	public List<T> list(String hql) {
		return this.list(hql,null);
	}

	private String iniSort(String hql){
		String order = SystemContext.getOrder();
		String sort = SystemContext.getSorts();
		if (sort != null && !"".equals(sort.trim())) {
			hql += " order by " + sort;
			if (!"desc".equals(order))
				hql += " asc";
			else {
				hql += " desc";
			}
		}
		return hql;
	}
	@SuppressWarnings("rawtypes")
	private void setAliasParameter(Query query,Map<String,Object> alias){
		if(alias!=null){
			Set<String> keys=alias.keySet();
			for(String key:keys){
				Object val = alias.get(key);
				if(val instanceof Collection){
					//查询条件是列表
					query.setParameterList(key,(Collection)val);
				}else{
					query.setParameter(key,val);
				}
			}
		}
	}
	private void setParameter(Query query,Object[] args){
		if(args!=null&&args.length>0){
			int index=0;
			for(Object arg:args){
				query.setParameter(index++,arg);
			}
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#list(java.lang.String,
	 * java.lang.Object[], java.util.Map)
	 */

	@Override
	public List<T> list(String hql, Object[] args, Map<String, Object> alias) {
		hql= iniSort(hql);
		Query query = getSession().createQuery(hql);
		setAliasParameter(query,alias);
		setParameter(query,args);
		return query.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#list(java.lang.String, java.util.Map)
	 */
	@Override
	public List<T> listByAlias(String hql, Map<String, Object> alias) {
		return this.list(hql,null,alias);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#find(java.lang.String,
	 * java.lang.Object[])
	 */
	@Override
	public Pager<T> find(String hql, Object[] args) {
		return this.find(hql,args,null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#find(java.lang.String, java.lang.Object)
	 */
	@Override
	public Pager<T> find(String hql, Object arg) {
		return this.find(hql,new Object[]{arg});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#find(java.lang.String)
	 */
	@Override
	public Pager<T> find(String hql) {
		return this.find(hql,null);
	}

	@SuppressWarnings("rawtypes")
	private void setPagers(Query query,Pager pages){
		Integer pageSize = SystemContext.getPageSize();
		Integer pageOffset = SystemContext.getPageOffset();
		if(pageOffset<0||pageOffset==null) pageOffset=0;
		if(pageSize==null||pageSize<0) pageSize=15;
		pages.setOffset(pageOffset);
		pages.setSize(pageSize);
		query.setFirstResult(pageOffset).setMaxResults(pageSize);
	}
	private String getCountHql(String hql,boolean isHql){
		String e=hql.substring(hql.indexOf("from"));
		String c="select count(*) "+e;
		if(isHql){
			c.replace("fetch","");
		}
		return c;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#find(java.lang.String,
	 * java.lang.Object[], java.util.Map)
	 */
	@Override
	public Pager<T> find(String hql, Object[] args, Map<String, Object> alias) {
		hql=iniSort(hql);
		String cq = getCountHql(hql,true);
		Query cQuery = getSession().createQuery(cq);
		Query query = getSession().createQuery(hql);
		//设置别名参数
		setAliasParameter(query,alias);
		setAliasParameter(cQuery,alias);
		//设置参数
		setParameter(query,args);
		setParameter(cQuery,args);
		Pager<T> pages =new Pager<T>();
		setPagers(query,pages);
		List<T> datas = query.list();
		pages.setDatas(datas);
		long total = (Long)cQuery.uniqueResult();
		pages.setTotal(total);
		return pages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#find(java.lang.String, java.util.Map)
	 */
	@Override
	public Pager<T> findByAlias(String hql, Map<String, Object> alias) {
		return this.find(hql,null,alias);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#queryObject(java.lang.String,
	 * java.lang.Object[])
	 */
	@Override
	public Object queryObject(String hql, Object[] args) {
		return this.queryObject(hql,args,null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#queryObject(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public Object queryObject(String hql, Object arg) {
		return this.queryObject(hql,new Object[]{arg});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#queryObject(java.lang.String)
	 */
	@Override
	public Object queryObject(String hql) {
		return this.queryObject(hql,null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#updateByHql(java.lang.String,
	 * java.lang.Object[])
	 */
	@Override
	public void updateByHql(String hql, Object[] args) {
		Query query = getSession().createQuery(hql);
		setParameter(query,args);
		query.executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#updateByHql(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void updateByHql(String hql, Object arg) {
		this.updateByHql(hql,new Object[]{arg});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#updateByHql(java.lang.String)
	 */
	@Override
	public void updateByHql(String hql) {
		this.updateByHql(hql,null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#listBySql(java.lang.String,
	 * java.lang.Object[], java.lang.Class, boolean)
	 */
	@Override
	public <N extends Object>List<N> listBySql(String sql, Object[] args, Class<?> clz, boolean hasEtity) {
		return this.listBySql(sql,args,null,clz,hasEtity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#listBySql(java.lang.String,
	 * java.lang.Object, java.lang.Class, boolean)
	 */
	@Override
	public <N extends Object>List<N> listBySql(String sql, Object arg, Class<?> clz, boolean hasEtity) {
		return this.listBySql(sql,new Object[]{arg},clz,hasEtity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#listBySql(java.lang.String,
	 * java.lang.Class, boolean)
	 */
	@Override
	public <N extends Object>List<N> listBySql(String sql, Class<?> clz, boolean hasEtity) {
		return this.listBySql(sql,null,clz,hasEtity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#listBySql(java.lang.String,
	 * java.lang.Object[], java.util.Map, java.lang.Class, boolean)
	 */
	@Override
	public <N extends Object>List<N> listBySql(String sql, Object[] args, Map<String, Object> alias, Class<?> clz, boolean hasEtity) {
		sql = iniSort(sql);
		SQLQuery sq = getSession().createSQLQuery(sql);
		setAliasParameter(sq,alias);
		setParameter(sq,args);
		if(hasEtity){
			sq.addEntity(clz);
		}else{
			sq.setResultTransformer(Transformers.aliasToBean(clz));
		}
		return sq.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#listBySql(java.lang.String,
	 * java.util.Map, java.lang.Class, boolean)
	 */
	@Override
	public <N extends Object>List<N> listByAliasSql(String sql, Map<String, Object> alias, Class<?> clz, boolean hasEtity) {
		return this.listBySql(sql,null,alias,clz,hasEtity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#findBySql(java.lang.String,
	 * java.lang.Object[], java.lang.Class, boolean)
	 */
	@Override
	public <N extends Object>Pager<N> findBySql(String sql, Object[] args, Class<?> clz, boolean hasEtity) {
		return this.findBySql(sql,args,null,clz,hasEtity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#findBySql(java.lang.String,
	 * java.lang.Object, java.lang.Class, boolean)
	 */
	@Override
	public <N extends Object>Pager<N> findBySql(String sql, Object arg, Class<?> clz, boolean hasEtity) {
		return this.findBySql(sql,new Object[]{arg},clz,hasEtity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#findBySql(java.lang.String,
	 * java.lang.Class, boolean)
	 */
	@Override
	public <N extends Object>Pager<N> findBySql(String sql, Class<?> clz, boolean hasEtity) {
		return this.findBySql(sql,null,clz,hasEtity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#findBySql(java.lang.String,
	 * java.lang.Object[], java.util.Map, java.lang.Class, boolean)
	 */
	@Override
	public <N extends Object>Pager<N> findBySql(String sql, Object[] args, Map<String, Object> alias, Class<?> clz, boolean hasEtity) {
		sql = iniSort(sql);
		String cq = getCountHql(sql,false);
		SQLQuery sqQuery = getSession().createSQLQuery(sql);
		SQLQuery cqQuery = getSession().createSQLQuery(cq);
		setAliasParameter(sqQuery,alias);
		setAliasParameter(cqQuery,alias);
		setParameter(sqQuery,args);
		setParameter(cqQuery,args);
		Pager<N> pages = new Pager<N>();
		setPagers(sqQuery,pages);
		if(hasEtity){
			sqQuery.addEntity(clz);
		}else {
			sqQuery.setResultTransformer(Transformers.aliasToBean(clz));
		}
		
		List<N> datas = sqQuery.list();
		pages.setDatas(datas);
		long total = ((BigInteger)cqQuery.uniqueResult()).longValue();
		pages.setTotal(total);
		return pages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ttw.basic.dao.BaseDaoI#findBySql(java.lang.String,
	 * java.util.Map, java.lang.Class, boolean)
	 */
	@Override
	public <N extends Object>Pager<N> findByAliasSql(String sql, Map<String, Object> alias, Class<?> clz, boolean hasEtity) {
		return this.findBySql(sql,null,alias,clz,hasEtity);
	}

	@Override
	public Object queryObject(String hql, Object[] args, Map<String, Object> alias) {
		Query query = getSession().createQuery(hql);
		setAliasParameter(query,alias);
		setParameter(query,args);
		return query.uniqueResult();
	}

	@Override
	public Object queryObjectByAlias(String hql, Map<String, Object> alias) {
		return this.queryObject(hql,null,alias);
	}

}
