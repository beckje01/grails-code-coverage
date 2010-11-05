package sample.plugin

class MyBeanTests extends GroovyTestCase {

    void testToUppercase() {
        MyBean bean = new MyBean(name: 'mike')
        assertEquals bean.name.toUpperCase(), bean.toUpper()
    }

}
