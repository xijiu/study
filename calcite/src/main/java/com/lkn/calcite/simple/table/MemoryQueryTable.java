package com.lkn.calcite.simple.table;

import org.apache.calcite.adapter.java.AbstractQueryableTable;
import org.apache.calcite.linq4j.QueryProvider;
import org.apache.calcite.linq4j.Queryable;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.TranslatableTable;

import java.lang.reflect.Type;

/**
 * @author likangning
 * @since 2018/5/11 上午8:33
 */
public class MemoryQueryTable extends AbstractQueryableTable implements TranslatableTable {
	protected MemoryQueryTable(Type elementType) {
		super(elementType);
	}

	@Override
	public <T> Queryable<T> asQueryable(QueryProvider queryProvider, SchemaPlus schemaPlus, String s) {
		return null;
	}

	@Override
	public RelNode toRel(RelOptTable.ToRelContext toRelContext, RelOptTable relOptTable) {
		return null;
	}

	@Override
	public RelDataType getRowType(RelDataTypeFactory relDataTypeFactory) {
		return null;
	}
}
