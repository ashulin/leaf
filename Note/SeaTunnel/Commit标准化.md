### Background 

At present, the commit message format of our committer and PMCs is not uniform when merging code.

### Motivation

The advantages of standardization:

1. Easy to quickly browse the commit log
2. Supports filtering commits (such as document changes) for quick information search
3. Change logs(Release) can be generated directly from the commit log.

### To be determined
1. Short words for each type
2. Format of words
    -   [ ] Uppercase the first letter and all parts
    -   [ ] All lowercase
3. Whether to leave Spaces between types
    -   [ ] Need a space
    -   [ ] Don't need Space

### Standardized

```shell
# type1
[{commit-type}] {module-abbreviation} commit-message (#{PR-Number})
# type2
[{chores}] commit-message (#{PR-Number})
```



#### Commit Type

- feature: The new features
  - feat `alternative`
- hotfix: fix bug
  -  bugfix `alternative`
  -  bug `alternative`
- doc: Document changes, adding, modifying, and fixing errors
  - docs `alternative`
- improve: Not a bug fix or a new feature
- refactor: Incompatible changes
- tests: Unit and integration testing
- e2e: End to End testing

#### chores

-   build: Changes in the build process，build tools
-   license
-   CI/CD: Including github's CI, and deployment scripts( dockerfile, shell, k8s yaml), etc.

#### Module abbreviation 

-   seatunnel-api: [api]

    >    The V2 API module

-   seatunnel-api-base: [apis]

    >   The V1 API base module

    -   [api/base]  `alternative`
    -   [api-base] `alternative`

-   seatunnel-api-flink: [apis] [flink]

    >   The flink's V1 API module

    -   [api/flink]  `alternative`
    -   [api-flink] `alternative`

-   seatunnel-api-spark: [apis] [spark]

    

-   seatunnel-common: [common]

    >    common code module

-   seatunnel-connectors-{engine-type}-{conenctor-name}: [{engine-type}] [connector] [{conenctor-name}] 

    >    connector-V1 for each engine

    -   [{engine-type}] [{conenctor-name}]  `alternative`

-   seatunnel-conenctors-v2/connector-{connector-name}:

    -    [connector] [{connector-name}]: Connector Implementation
        -    [source] [{connector-name}]:  `alternative`
        -    [sink] [{connector-name}]:  `alternative`
    -    [catalog] [{connector-name}]: catalog Implementation

    >   Connector or catalog management implementation for each data source/storage engine of API V2

-   seatunnel-transforms-{engine}-{transform-name}: [engine] [transform] [transform-name]

    >   Each engine API implements  transforms of API V1

-   seatunnel-core: 

    -   seatunnel-core-base: [core]

        >    the starter-v1 base module

        -   [core/base]  `alternative`
        -   [core-base] `alternative`

    -   seatunnel-core-{engine-type}: [core] [{engine-type}]

        >    starter-V1 for each engine

    -   seatunnel-core-starter: [starter]

        >   the starter-v2 base module

    -   seatunnel-{engine-type}-starter: [starter] [{engine-type}]

        >   starter-V2 for each engine

    -   seatunnel-starter: [starter] [engine]

        >   starter-V2 for seatunnel engine

-   seatunnel-dist: [dist]

-   seatunnel-e2e

    -   seatunnel-e2e-common:  [e2e]

    -   seatunnel-connector-{engine}-{connector-name}-e2e:  [e2e] [{engine}] [{connector-name}]

        >   Connector-V1 E2E test for each engine

    -   seatunnel-connector-v2-e2e/connector-{connector-name}-e2e: [e2e] [{connector-name}]

        >   Connector-V2 Unified E2E test

    -   connector-{connector-name}-{engine}-e2e: [e2e] [{connector-name}] [{engine-name}]

        >   Connector-V2 E2E test for each engine, **Legacy code that needs to be integrated into seatunnel-connector-v2-e2e**

    -   connector-engine-e2e: [e2e] [{connector-name}] [engine]

        >   Connector-V2 E2E test for seatunnel engine, **Legacy code that needs to be integrated into seatunnel-connector-v2-e2e**

-   seatunnel-engine: [engine]

    -   seatunnel-engine-client: [engine] [client]
    -   seatunnel-engine-common: [engine] [common]
    -   seatunnel-engine-core: [engine] [core]
    -   seatunnel-engine-server: [engine] [{feature-name}]
    -   seatunnel-engine-storage:  [engine] [storage]

-   seatunnel-examples: [example]

    >   There will be no new commits

-    seatunnel-formats/seatunnel-format-{format-name}: [format] [{format-name}]

-   seatunnel-plugin-discovery: [spi]

-   seatunnel-translation:

    -   seatunnel-translation-base: [translation]
    -   seatunnel-translation-{engine}-common: [translation] [{engine}]
    -   seatunnel-translation-{engine}-{engine-version}: [translation] [{engine}-{engine-version}]