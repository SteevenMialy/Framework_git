package test.controller;

import  etu4112.framework.servelet.annotation.Monannotation;
import  etu4112.framework.servelet.annotation.Url;

@Monannotation
public class TestSprint3 {

    @Url(value = "test-sprint3-get", method = "GET")
    public void testerGet() {
        System.out.println("======> La méthode testerGet() a été invoquée ! (Méthode GET) <======");
    }

    @Url(value = "test-sprint3-post", method = "POST")
    public void testerPost() {
        System.out.println("======> La méthode testerPost() a été invoquée ! (Méthode POST) <======");
    }

    @Url("test-sprint3-default")
    public void testerDefault() {
        System.out.println("======> La méthode testerDefault() a été invoquée ! (Par défaut, donc GET) <======");
    }
}
