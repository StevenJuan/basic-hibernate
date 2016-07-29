package org.ttw.basic.model;

/**
 * 用来传递列表的ThreadLocal数据
 * 
 * @author Administrator
 *
 */
public class SystemContext {
	private static ThreadLocal<Integer> pageSize = new ThreadLocal<Integer>();
	private static ThreadLocal<Integer> pageOffset = new ThreadLocal<Integer>();
	private static ThreadLocal<String> sorts = new ThreadLocal<String>();
	private static ThreadLocal<String> order = new ThreadLocal<String>();

	public static Integer getPageSize() {
		return pageSize.get();
	}

	public static void setPageSize(Integer _pageSize) {
		pageSize.set(_pageSize);
		;
	}

	public static Integer getPageOffset() {
		return pageOffset.get();
	}

	public static void setPageOffset(Integer _pageOffset) {
		pageOffset.set(_pageOffset);
		;
	}

	public static String getSorts() {
		return sorts.get();
	}

	public static void setSorts(String _sorts) {
		sorts.set(_sorts);
		;
	}

	public static String getOrder() {
		return order.get();
	}

	public static void setOrder(String _order) {
		order.set(_order);
		;
	}

	public static void removePageSize() {
		pageSize.remove();
	}

	public static void removePageOffset() {
		pageOffset.remove();
	}

	public static void removeSort() {
		sorts.remove();
	}

	public static void removeOder() {
		order.remove();
	}

}
