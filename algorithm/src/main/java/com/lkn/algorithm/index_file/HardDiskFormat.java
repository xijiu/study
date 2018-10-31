package com.lkn.algorithm.index_file;

/**
 * 硬盘存储文件的格式
 *
 * 第一个4k存储空间为当前索引的描述信息
 * 		0-16字节：根节点的位置
 *
 * 每个节点占用4096字节（即4k）的存储空间
 * 		1、每个元素占用16个字节
 *
 * 					  左孩子id           元素内容           右孩子id
 * 				|----4byte----|--------8byte--------|----4byte----|
 *
 * 		2、每个node的前16个字节为当前索引文件的说明信息，例如所含element个数等
 *
 * @author likangning
 * @since 2018/10/25 上午9:07
 */
public class HardDiskFormat {
}
