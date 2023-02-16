## Multiple table proposal

### Backgroud & Motivation

In the CDC scenario, we found that when there are too many CDC Sources, too many database links will be occupied, which will affect the stability of the database.
For this reason, we expect to reduce the number of Sources when synchronizing all tables. Since the current design is that each Source synchronizes one table, we expect one Source to handle multiple tables.

**Advantages**: take up fewer database connections, reduce database pressure
**Disadvantage**: In the `SeaTunnel zeta`, multiple tables will be in a pipeline, and the granularity of fault tolerance will become larger.

### Overall Design

![mutil-table-dag](resources/Multiple%20table%20proposal/mutil-table-dag.png)

1.   Load `CatalogFactory` SPI through Config file.
2.   Create `Catalog` using `CatalogFactory`.
3.   Create `CatalogTable`s with `Catalog` and configured options.
     -   If the table does not exist in the sink, create an inferred CatalogTable in the sink.
4.   Fill the obtained `CatalogTable`s into `TableFactoryContext`, and use them in`TableSinkFactory`, `TableSourceFactory`, `TableTransformFactory`.
5.   If Source supports multiple tables, its `TableSourceFactory` must implement the `SupportMultipleTable` interface, use the information of multiple CatalogTables to create `MultipleRowType`, and `SeaTunnelSource#getProducedType` will return `MultipleRowType`.
6.   Use `MultipleRowType` inside Source to deserialize data into `SeaTunnelRow`, and add table name to `SeaTunnelRow`.
7.   The engine distributes data according to `MultipleRowType` and `SeaTunnelRow`'s table name.

#### Config design

```config
source {
  MySQL-CDC {
    parallelism = 1
    // RegEx to get multiple tables
    database-name = "inventory_.*"
    table-name = ".*"
    result_table_name = "cdc1"
  }
}
transform {
  DistributionTransform {
    source_table_name = "cdc1"
    result_table_name = "transform1"
  }
  
  Filter {
    source_table_name = "transform1#test"
    result_table_name = "filter1#test"
  }
}
sink {
  // sink option template
  Doris {
    source_table_name = "transform1"
    nodeUrls = ["e2e_dorisdb:8030"]
    username = root
    password = ""
    database = "test"
    batch_max_rows = 100
    doris.config = {
      format = "JSON"
      strip_outer_array = true
    }
  }
  // Specify options for a single table
  Doris {
    source_table_name = "filter1#test"
    nodeUrls = ["e2e_dorisdb:8030"]
    username = root
    password = ""
    database = "test"
    batch_max_rows = 200
    doris.config = {
      format = "JSON"
      strip_outer_array = true
    }
  }
}
```

#### Related pseudo-code

```java
// For Source deserialization and Row distribution
public class MultipleRowType implements SeaTunnelDataType<SeaTunnelRow> {
    private final String[] tableNames;
    private final SeaTunnelRowType[] rowTypes;
}
```

```java
// create, get, update catalog
public interface Catalog {
    // query
    List<String> listDatabases() throws CatalogException;
    List<String> listTables(String databaseName) throws CatalogException, DatabaseNotExistException;
    // get catalog table
    CatalogTable getTable(TablePath tablePath) throws CatalogException, TableNotExistException;
    // update catalog table
    void createTable(TablePath tablePath, CatalogTable table, boolean ignoreIfExists) throws TableAlreadyExistException, DatabaseNotExistException, CatalogException;
    void dropTable(TablePath tablePath, boolean ignoreIfNotExists)
            throws TableNotExistException, CatalogException;
}
```

```java
// Declare that the Source supports multiple tables, and control the number of tables by itself
public interface SupportMultipleTable {
    /**
     * A connector can pick tables and return the accepted and remaining tables.
     */
    Result applyTables(TableFactoryContext context);

    final class Result {
        private final List<CatalogTable> acceptedTables;
        private final List<CatalogTable> remainingTables;

        private Result(
                List<CatalogTable> acceptedTables,
                List<CatalogTable> remainingTables) {
            this.acceptedTables = acceptedTables;
            this.remainingTables = remainingTables;
        }
    }
}
```

#### Adapter

##### SeaTunnel Zeta

```java
// pseudo-code
public class DistributionTransform extends SeaTunnelTransform<Record<?>> {
    // Use MultipleRowType to distribute records to corresponding data channels
    private MultipleRowType multiRowType;
}
```

##### Flink

-   Operator chain: avoid row serialization of different structures
-   OutputTag & Context#output: Use side-output streams to distribute data to corresponding channels
