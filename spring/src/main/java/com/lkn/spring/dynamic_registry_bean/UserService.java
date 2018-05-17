package com.lkn.spring.dynamic_registry_bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author likangning
 * @since 2018/5/17 下午2:53
 */
@Component
public class UserService {

	public String getNameById(Long id) {
		return "name" + id;
	}
}
