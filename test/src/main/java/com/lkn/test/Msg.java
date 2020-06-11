package com.lkn.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author likangning
 * @since 2020/5/29 上午9:14
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Msg {

	private int port;

	private int msgType;

	private int batchPos;

	private String msgBody;

}
