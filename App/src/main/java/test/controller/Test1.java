package test.controller;

import etu4112.framework.servelet.annotation.Monannotation;
import etu4112.framework.servelt.annotation.Url;
@Monannotation
public class Test1 {
    @Url("user-liste")
    public void getToutesLesListes() {
       
    }

    @Url("user-delete")
    public void supprimerUtilisateur() {
      
    }
}
