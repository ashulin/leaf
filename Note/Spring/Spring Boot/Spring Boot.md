@ConfigurationProperties绑定逻辑

版本：2.4.5

主要类：

SpringConfigurationPropertySource

JavaBeanBinder

PropertyMapper

DefaultPropertyMapper

方法：

org.springframework.boot.context.properties.source.SpringConfigurationPropertySource#getConfigurationProperty

org.springframework.boot.context.properties.bind.Binder#findProperty

```java
//org.springframework.boot.context.properties.bind.JavaBeanBinder
	@Override
	public <T> T bind(ConfigurationPropertyName name, Bindable<T> target, Context context,
			DataObjectPropertyBinder propertyBinder) {
		boolean hasKnownBindableProperties = target.getValue() != null && hasKnownBindableProperties(name, context);
        // 获取Bean信息
		Bean<T> bean = Bean.get(target, hasKnownBindableProperties);
		if (bean == null) {
			return null;
		}
		BeanSupplier<T> beanSupplier = bean.getSupplier(target);
		boolean bound = bind(propertyBinder, bean, beanSupplier, context);
		return (bound ? beanSupplier.get() : null);
	}

	private <T> boolean bind(BeanSupplier<T> beanSupplier, DataObjectPropertyBinder propertyBinder,
			BeanProperty property) {
		String propertyName = property.getName();
		ResolvableType type = property.getType();
		Supplier<Object> value = property.getValue(beanSupplier);
		Annotation[] annotations = property.getAnnotations();
        // 使用Binder#bindDataObject中匿名类
		Object bound = propertyBinder.bindProperty(propertyName,
				Bindable.of(type).withSuppliedValue(value).withAnnotations(annotations));
		if (bound == null) {
			return false;
		}
		if (property.isSettable()) {
			property.setValue(beanSupplier, bound);
		}
		else if (value == null || !bound.equals(value.get())) {
			throw new IllegalStateException("No setter found for property: " + property.getName());
		}
		return true;
	}
	static class Bean<T> {
        private void addMethodIfPossible(Method method, String prefix, int parameterCount,
				BiConsumer<BeanProperty, Method> consumer) {
			if (method != null && method.getParameterCount() == parameterCount && method.getName().startsWith(prefix)
					&& method.getName().length() > prefix.length()) {
				String propertyName = Introspector.decapitalize(method.getName().substring(prefix.length()));
			// beanProperty -> bean-property
            consumer.accept(this.properties.computeIfAbsent(propertyName, this::getBeanProperty), method);
			}
		}
		private BeanProperty getBeanProperty(String name) {
			return new BeanProperty(name, this.type);
		}
	}
	static class BeanProperty {
		private final String name;
        private final ResolvableType declaringClassType;
		BeanProperty(String name, ResolvableType declaringClassType) {
			// 获取虚线格式的名称
			this.name = DataObjectPropertyName.toDashedForm(name);
			this.declaringClassType = declaringClassType;
		}
    }
```



```java
//org.springframework.boot.context.properties.bind.Binder
	private Object bindDataObject(ConfigurationPropertyName name, Bindable<?> target, BindHandler handler,
			Context context, boolean allowRecursiveBinding) {
		if (isUnbindableBean(name, target, context)) {
			return null;
		}
		Class<?> type = target.getType().resolve(Object.class);
		if (!allowRecursiveBinding && context.isBindingDataObject(type)) {
			return null;
		}
        // 匿名DataObjectPropertyBinder类，将Bean
		DataObjectPropertyBinder propertyBinder = (propertyName, propertyTarget) -> bind(name.append(propertyName),
				propertyTarget, handler, context, false, false);
		return context.withDataObject(type, () -> {
			for (DataObjectBinder dataObjectBinder : this.dataObjectBinders) {
				Object instance = dataObjectBinder.bind(name, target, context, propertyBinder);
				if (instance != null) {
					return instance;
				}
			}
			return null;
		});
	}

interface DataObjectPropertyBinder {

	/**
	 * Bind the given property.
	 * @param propertyName the property name (in lowercase dashed form, e.g.
	 * {@code first-name})
	 * @param target the target bindable
	 * @return the bound value or {@code null}
	 */
	Object bindProperty(String propertyName, Bindable<?> target);

}
```

