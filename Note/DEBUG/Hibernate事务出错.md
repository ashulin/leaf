## SSH debug

第一次使用SSH验证模块是否配置正常时，在DAO层依赖注入HibernateTemplate对象，HibernateTemplate中注入SessionFactory。
DAO调用HibernateTemplate.save()保存实体类时报错

`HibernateTemplate事务bug`

#### bug code:

```java
org.springframework.dao.InvalidDataAccessApiUsageException: Write operations are not allowed in read-only mode (FlushMode.MANUAL): Turn your Session into FlushMode.COMMIT/AUTO or remove 'readOnly' marker from transaction definition.

	at org.springframework.orm.hibernate5.HibernateTemplate.checkWriteOperationAllowed(HibernateTemplate.java:1093)
	at org.springframework.orm.hibernate5.HibernateTemplate.lambda$save$11(HibernateTemplate.java:637)
	at org.springframework.orm.hibernate5.HibernateTemplate.doExecute(HibernateTemplate.java:383)
	at org.springframework.orm.hibernate5.HibernateTemplate.executeWithNativeSession(HibernateTemplate.java:349)
	at org.springframework.orm.hibernate5.HibernateTemplate.save(HibernateTemplate.java:636)
```
这次debug让我发现自己debug能力太弱，又百度又google的，不如直接看源码
>点开HibernateTemplate.save(HibernateTemplate.java:636)
```java
public Serializable save(Object entity) throws DataAccessException {
        return (Serializable)nonNull(this.executeWithNativeSession((session) -> {
            this.checkWriteOperationAllowed(session);
            return session.save(entity);
        }));
    }
```
 ()->{} 是lambda表达式的匿名内部类;操作return session.save(entity)保存实体类前有this.checkWriteOperationAllowed(session)。
 >点开checkWriteOperationAllowed方法
 ```java
 protected void checkWriteOperationAllowed(Session session) throws InvalidDataAccessApiUsageException {
    if (this.isCheckWriteOperations() && SessionFactoryUtils.getFlushMode(session).lessThan(FlushMode.COMMIT)) {
        throw new InvalidDataAccessApiUsageException("Write operations are not allowed in read-only mode (FlushMode.MANUAL): Turn your Session into FlushMode.COMMIT/AUTO or remove 'readOnly' marker from transaction definition.");
    }
}
 ```
可以看到这个方法中抛出异常输出了前面的提示语句。
>查看第一个if条件isCheckWriteOperations()
```java
public boolean isCheckWriteOperations() {
        return this.checkWriteOperations;
    }
```
>查看checkWriteOperations属性
```java
public class HibernateTemplate implements HibernateOperations, InitializingBean {
    private static final Method createQueryMethod;
    private static final Method getNamedQueryMethod;
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private SessionFactory sessionFactory;
    @Nullable
    private String[] filterNames;
    private boolean exposeNativeSession = false;
    private boolean checkWriteOperations = true;
    private boolean cacheQueries = false;
    @Nullable
    private String queryCacheRegion;
    private int fetchSize = 0;
    private int maxResults = 0;
***
}
```
发现checkWriteOperations = true;
所以只要在使用save/update/saveOrUpdate等方法时，在前面先使用
`hibernateTemplate.setCheckWriteOperations(false);`就可以了
