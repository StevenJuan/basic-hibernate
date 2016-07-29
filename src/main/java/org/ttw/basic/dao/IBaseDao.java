package org.ttw.basic.dao;

import java.util.List;
import java.util.Map;

import org.ttw.basic.model.Pager;

/**
 * 公共DAO处理对象，这个对象包含了hibernate的所有基本操作和对SQL操作
 * @author Administrator
 *
 * @param <T>
 */
public interface IBaseDao<T> {
	public T add(T t);
	public void update(T t);
	public void delete(int id);
	public T load(int id);
	
	/**
	 * 不分页列表对象
	 * @param hql 查询对象的hql
	 * @param args 查询参数
	 * @return 一组不分页的列表
	 */
	public List<T> list(String hql,Object[] args);
	public List<T> list(String hql,Object arg);
	public List<T> list(String hql);
	/**
	 * 基于别名和查询参数的混合列表对象
	 * @param hql
	 * @param args
	 * @param alias 别名
	 * @return
	 */
	public List<T> list(String hql, Object[] args, Map<String,Object> alias);
	public List<T> listByAlias(String hql, Map<String,Object> alias);
	
	
	/**
	 * 分页列表对象
	 * @param hql 查询对象的hql
	 * @param args 查询参数
	 * @return 一组不分页的列表
	 */
	public Pager<T> find(String hql,Object[] args);
	public Pager<T> find(String hql,Object arg);
	public Pager<T> find(String hql);
	/**
	 * 基于别名和查询参数的混合列表对象
	 * @param hql
	 * @param args
	 * @param alias 别名
	 * @return
	 */
	public Pager<T> find(String hql, Object[] args, Map<String,Object> alias);
	public Pager<T> findByAlias(String hql, Map<String,Object> alias);
	
	/**
	 * 根据hql查询一组对象
	 * @param hql
	 * @param args
	 * @return
	 */
	public Object queryObject(String hql, Object[] args);
	public Object queryObject(String hql, Object arg);
	public Object queryObject(String hql);
	public Object queryObject(String hql, Object[] args,Map<String,Object> alias);
	public Object queryObjectByAlias(String hql, Map<String,Object> alias);
	
	/**
	 * 根据HQL更新对象
	 * @param hql
	 * @param args
	 */
	public void updateByHql(String hql, Object[] args);
	public void updateByHql(String hql, Object arg);
	public void updateByHql(String hql);
	
	/**
	 * 不带分页根据SQL查询对象，不包含关联对象
	 * @param sql
	 * @param args
	 * @param clz 查询的实体对象
	 * @param hasEtity 该对象是否是一个hibernate所管理的实体，如果不是需要使用setResultTransform查询
	 * @return 一组对象
	 */
	public <N extends Object>List<N> listBySql(String sql, Object[] args, Class<?> clz, boolean hasEtity);
	public <N extends Object>List<N> listBySql(String sql, Object arg, Class<?> clz, boolean hasEtity);
	public <N extends Object>List<N> listBySql(String sql, Class<?> clz, boolean hasEtity);
	public <N extends Object>List<N> listBySql(String sql, Object[] args, Map<String,Object> alias,Class<?> clz, boolean hasEtity);
	public <N extends Object>List<N> listByAliasSql(String sql, Map<String,Object> alias, Class<?> clz, boolean hasEtity);
	
	/**
	 * 带分页根据SQL查询对象，不包含关联对象
	 * @param sql
	 * @param args
	 * @param clz 查询的实体对象
	 * @param hasEtity 该对象是否是一个hibernate所管理的实体，如果不是需要使用setResultTransform查询
	 * @return 一组对象
	 */
	public <N extends Object>Pager<N> findBySql(String sql, Object[] args, Class<?> clz, boolean hasEtity);
	public <N extends Object>Pager<N> findBySql(String sql, Object arg, Class<?> clz, boolean hasEtity);
	public <N extends Object>Pager<N> findBySql(String sql, Class<?> clz, boolean hasEtity);
	public <N extends Object>Pager<N> findBySql(String sql, Object[] args, Map<String,Object> alias,Class<?> clz, boolean hasEtity);
	public <N extends Object>Pager<N> findByAliasSql(String sql, Map<String,Object> alias, Class<?> clz, boolean hasEtity);
}
