package org.ttw.basic.dao;

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
	
}
