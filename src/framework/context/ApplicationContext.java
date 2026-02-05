package framework.context;

public class ApplicationContext {

    private final BeanRegistry registry;
    private final BeanFactory beanFactory;
    private final ScannerAPI scanner;

    public ApplicationContext() {
        this.registry = new BeanRegistry();
        this.beanFactory = new BeanFactory(registry);
        this.scanner = new ScannerAPI(registry);
    }

    public void scan(final String basePackage) {
        scanner.scan(basePackage);
    }

    public <T> T getBean(final Class<T> type) {
        return beanFactory.getBean(type);
    }
}
