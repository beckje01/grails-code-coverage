package sample.plugin

class MyBean {

    String name

    String toUpper() {
        return name?.toUpperCase()
    }

}
