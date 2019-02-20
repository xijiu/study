package com.lkn.algorithm.lru;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author likangning
 * @since 2019/2/19 上午10:13
 */
public class LRU<T extends Comparable> {

	private static final int MAX_CACHE_SIZE = 4;

	private Map<T, Item<T>> map = Maps.newLinkedHashMapWithExpectedSize(MAX_CACHE_SIZE);

	private Item<T> first = null;

	private Item<T> last = null;

	private int size = 0;


	public synchronized void add(T t) {
		Item<T> item = map.get(t);
		// 缓存中的内容不发生变化，只调整item顺序
		if (item != null) {
			itemAdjust(item);
		} else {
			addNewItem(t);
		}
	}

	/**
	 * 添加新节点
	 * @param t 内容
	 */
	private void addNewItem(T t) {
		Item<T> newItem = new Item<>(t);
		map.put(t, newItem);
		if (size < MAX_CACHE_SIZE) {
			// 直接加入至队尾
			becomeFirst(newItem);
			size++;
		} else {
			// 删除最后一个元素
			deleteLastItem();
			// 将新元素加入队首
			becomeFirst(newItem);
		}
	}

	private void deleteLastItem() {
		if (last == null) {
			return;
		}
		map.remove(last.data);
		Item<T> frontItem = last.front;
		frontItem.behind = null;
		last = frontItem;
	}

	/**
	 * item顺序调整
	 * @param item	目标项
	 */
	private void itemAdjust(Item<T> item) {
		Item<T> frontItem = item.front;
		Item<T> behindItem = item.behind;
		// 如果当前节点的前一个节点为空，那么说明当前节点即为首节点
		// 如果当前节点的前一个节点不为空
		if (frontItem != null) {
			if (behindItem == null) {
				frontItem.behind = null;
				last = frontItem;
			} else {
				frontItem.behind = behindItem;
				item.behind.front = frontItem;
			}
			becomeFirst(item);
		}
	}

	/**
	 * 成为第一个元素
	 * @param item	目标元素
	 */
	private void becomeFirst(Item<T> item) {
		if (first == null) {
			first = item;
			item.behind = null;
			item.front = null;
		} else {
			first.front = item;
			item.behind = first;
			item.front = null;
			first = item;
		}
	}

	private static class Item<T extends Comparable> {
		T data;
		Item<T> front;
		Item<T> behind;

		Item(T data) {
			this.data = data;
		}
	}

	@Override
	public String toString() {
		if (first != null) {
			Item<T> curr = first;
			StringBuilder sb = new StringBuilder();
			while (curr != null) {
				sb.append(curr.data).append(", ");
				curr = curr.behind;
			}
			sb.delete(sb.length() - 2, sb.length());
			return sb.toString();
		}
		return "";
	}
}
