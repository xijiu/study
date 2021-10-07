package com.lkn;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author likangning
 * @since 2020/5/23 上午8:46
 */
@Setter
@Getter
public class Role implements Comparable<Role> {

	private Long id;

	private String name;

	private List<Department> departments = new ArrayList<>();

	@Override
	public int compareTo(Role o) {
		return this.name.compareTo(o.name);
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Role) {
			return this.name.equals(((Role)obj).name);
		}
		return false;
	}
}
