package com.lkn.calcite.simple;

import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaFactory;
import org.apache.calcite.schema.SchemaPlus;

import java.util.Map;

public class MemorySchemaFactory implements SchemaFactory {

	public static MemorySchema TOTAL_SCHEMA = null;
	@Override
	public Schema create(SchemaPlus parentSchema, String name, Map<String, Object> operand) {
		TOTAL_SCHEMA = new MemorySchema(name);
		return TOTAL_SCHEMA;
	}
}
